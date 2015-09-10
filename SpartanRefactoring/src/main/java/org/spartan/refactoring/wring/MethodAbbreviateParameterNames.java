package org.spartan.refactoring.wring;

import static org.spartan.refactoring.wring.Wrings.rename;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEditGroup;
import org.spartan.refactoring.utils.JavaTypeNameParser;
import org.spartan.refactoring.utils.MethodExplorer;
import org.spartan.refactoring.utils.Rewrite;

/**
 * A {@link Wring} that abbreviates the names of variables that have a generic
 * variation. The abbreviated name is the first character in the last word of
 * the variable's name.
 *
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2015/08/24
 */
public class MethodAbbreviateParameterNames extends Wring<MethodDeclaration> {
  @Override String description(final MethodDeclaration d) {
    return d.getName().toString();
  }
  @Override Rewrite make(final MethodDeclaration d, final ExclusionManager exclude) {
    final List<SingleVariableDeclaration> vd = find(d.parameters());
    final Map<SimpleName, SimpleName> renameMap = new HashMap<>();
    if (vd == null)
      return null;
    for (final SingleVariableDeclaration v : vd) {
      final JavaTypeNameParser parser = new JavaTypeNameParser(v.getType().toString());
      if (legal(v, d, parser, renameMap.values()))
        renameMap.put(v.getName(), d.getAST().newSimpleName(parser.shortName()));
    }
    if (exclude != null)
      exclude.exclude(d);
    return new Rewrite("Rename parameters in method " + d.getName().toString(), d) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        for (final SimpleName key : renameMap.keySet())
          rename(key, renameMap.get(key), d, r, g);
      }
    };
  }
  private List<SingleVariableDeclaration> find(final List<SingleVariableDeclaration> ds) {
    final List<SingleVariableDeclaration> $ = new ArrayList<>();
    for (final SingleVariableDeclaration d : ds)
      if (suitable(d))
        $.add(d);
    return $.size() != 0 ? $ : null;
  }
  @SuppressWarnings("static-method") private boolean legal(final SingleVariableDeclaration d, final MethodDeclaration m, final JavaTypeNameParser parser,
      final Collection<SimpleName> newNames) {
    final MethodExplorer e = new MethodExplorer(m);
    for (final SimpleName n : e.localVariables())
      if (n.getIdentifier().equals(parser.shortName()))
        return false;
    for (final SimpleName n : newNames)
      if (n.getIdentifier().equals(parser.shortName()))
        return false;
    return !m.getName().getIdentifier().equalsIgnoreCase(parser.shortName());
  }
  @SuppressWarnings("static-method") private boolean suitable(final SingleVariableDeclaration d) {
    return (new JavaTypeNameParser(d.getType().toString())).isGenericVariation(d.getName().getIdentifier());
  }
}
