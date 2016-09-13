package il.org.spartan.spartanizer.engine;

import static il.org.spartan.spartanizer.assemble.plant.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.java.*;

/** Replace a variable with an expression
 * @author Yossi Gil
 * @year 2015 */
public final class LocalInliner {
  static Wrapper<ASTNode>[] wrap(final ASTNode[] ns) {
    @SuppressWarnings("unchecked") final Wrapper<ASTNode>[] $ = new Wrapper[ns.length];
    int i = 0;
    for (final ASTNode t : ns)
      $[i++] = new Wrapper<>(t);
    return $;
  }

  final SimpleName name;
  final ASTRewrite rewriter;
  final TextEditGroup editGroup;

  public LocalInliner(final SimpleName n) {
    this(n, null, null);
  }

  public LocalInliner(final SimpleName name, final ASTRewrite rewriter, final TextEditGroup editGroup) {
    this.name = name;
    this.rewriter = rewriter;
    this.editGroup = editGroup;
  }

  public LocalInlineWithValue byValue(final Expression replacement) {
    return new LocalInlineWithValue(replacement);
  }

  public class LocalInlineWithValue extends Wrapper<Expression> {
    LocalInlineWithValue(final Expression replacement) {
      super(extract.core(replacement));
    }

    /** Computes the number of AST nodes added as a result of the replacement
     * operation.
     * @param es JD
     * @return A non-negative integer, computed from the number of occurrences
     *         of {@link #name} in the operands, and the size of the
     *         replacement. */
    public int addedSize(final ASTNode... ns) {
      return uses(ns).size() * (metrics.size(get()) - 1);
    }

    public boolean canInlineinto(final ASTNode... ns) {
      return Collect.definitionsOf(name).in(ns).isEmpty() && (sideEffects.free(get()) || uses(ns).size() <= 1);
    }

    public boolean canSafelyInlineinto(final ASTNode... ns) {
      return canInlineinto(ns) && unsafeUses(ns).isEmpty();
    }

    @SafeVarargs public final void inlineInto(final ASTNode... ns) {
      inlineinto(wrap(ns));
    }

    /** Computes the total number of AST nodes in the replaced parameters
     * @param es JD
     * @return A non-negative integer, computed from original size of the
     *         parameters, the number of occurrences of {@link #name} in the
     *         operands, and the size of the replacement. */
    public int replacedSize(final ASTNode... ns) {
      return metrics.size(ns) + uses(ns).size() * (metrics.size(get()) - 1);
    }

    @SuppressWarnings("unchecked") private void inlineinto(final Wrapper<ASTNode>... ns) {
      for (final Wrapper<ASTNode> n : ns)
        inlineintoSingleton(get(), n);
    }

    private void inlineintoSingleton(final ASTNode replacement, final Wrapper<ASTNode> n) {
      final ASTNode oldExpression = n.get();
      final ASTNode newExpression = duplicate.of(n.get());
      n.set(newExpression);
      rewriter.replace(oldExpression, newExpression, editGroup);
      for (final ASTNode use : Collect.usesOf(name).in(newExpression))
        rewriter.replace(use, !(use instanceof Expression) ? replacement : plant((Expression) replacement).into(use.getParent()), editGroup);
    }

    private List<SimpleName> unsafeUses(final ASTNode... ns) {
      return Collect.unsafeUsesOf(name).in(ns);
    }

    private List<SimpleName> uses(final ASTNode... ns) {
      return Collect.usesOf(name).in(ns);
    }
  }
}