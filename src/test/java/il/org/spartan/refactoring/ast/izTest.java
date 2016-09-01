package il.org.spartan.refactoring.ast;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.ast.extract.*;
import static il.org.spartan.refactoring.engine.into.*;
import static org.eclipse.jdt.core.dom.ASTNode.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import org.junit.*;

import il.org.spartan.*;

/** Test class for class {@link iz}
 * @author Yossi Gil
 * @since 2015-07-17 */
@SuppressWarnings({ "javadoc", "static-method" }) //
public class izTest {
  @Test public void booleanLiteralFalseOnNull() {
    azzert.that(iz.booleanLiteral(e("null")), is(false));
  }

  @Test public void booleanLiteralFalseOnNumeric() {
    azzert.that(iz.booleanLiteral(e("12")), is(false));
  }

  @Test public void booleanLiteralFalseOnThis() {
    azzert.that(iz.booleanLiteral(e("this")), is(false));
  }

  @Test public void booleanLiteralTrueOnFalse() {
    azzert.that(iz.booleanLiteral(e("false")), is(true));
  }

  @Test public void booleanLiteralTrueOnTrue() {
    azzert.that(iz.booleanLiteral(e("true")), is(true));
  }

  @Test public void callIsSpecificTrue() {
    azzert.that(iz.constant(e("this")), is(true));
  }

  @Test public void canMakeExpression() {
    e("2+2");
  }

  @Test public void isConstantFalse() {
    azzert.that(iz.constant(e("a")), is(false));
  }

  @Test public void isNullFalse1() {
    azzert.that(iz.nullLiteral(e("this")), is(false));
  }

  @Test public void isNullFalse2() {
    azzert.that(iz.thisLiteral(e("this.a")), is(false));
  }

  @Test public void isNullTrue() {
    azzert.that(iz.nullLiteral(e("null")), is(true));
  }

  @Test public void isOneOf() {
    azzert.that(iz.oneOf(e("this"), CHARACTER_LITERAL, NUMBER_LITERAL, NULL_LITERAL, THIS_EXPRESSION), is(true));
  }

  @Test public void isThisFalse1() {
    azzert.that(iz.thisLiteral(e("null")), is(false));
  }

  @Test public void isThisFalse2() {
    azzert.that(iz.thisLiteral(e("this.a")), is(false));
  }

  @Test public void isThisTrue() {
    azzert.that(iz.thisLiteral(e("this")), is(true));
  }

  @Test public void negative0() {
    azzert.that(iz.negative(e("0")), is(false));
  }

  @Test public void negative1() {
    azzert.that(iz.negative(e("0")), is(false));
  }

  @Test public void negativeMinus1() {
    azzert.that(iz.negative(e("- 1")), is(true));
  }

  @Test public void negativeMinus2() {
    azzert.that(iz.negative(e("- 2")), is(true));
  }

  @Test public void negativeMinusA() {
    azzert.that(iz.negative(e("- a")), is(true));
  }

  @Test public void negativeNull() {
    azzert.that(iz.negative(e("null")), is(false));
  }

  @Test public void numericLiteralFalse1() {
    azzert.that(iz.numericLiteral(e("2*3")), is(false));
  }

  @Test public void numericLiteralFalse2() {
    azzert.that(iz.numericLiteral(e("2*3")), is(false));
  }

  @Test public void numericLiteralTrue() {
    azzert.that(iz.numericLiteral(e("1")), is(true));
  }

  @Test public void seriesA_3() {
    azzert.nay(iz.infixPlus(e("(i+j)")));
    azzert.aye(iz.infixPlus(core(e("(i+j)"))));
    azzert.nay(iz.infixMinus(e("(i-j)")));
    azzert.aye(iz.infixMinus(core(e("(i-j)"))));
  }

  @Test public void isDeMorganAND() {
    azzert.aye(iz.deMorgan(CONDITIONAL_AND));
  }

  @Test public void isDeMorganGreater() {
    azzert.nay(iz.deMorgan(GREATER));
  }

  @Test public void isDeMorganGreaterEuals() {
    azzert.nay(iz.deMorgan(GREATER_EQUALS));
  }

  @Test public void isDeMorganOR() {
    azzert.aye(iz.deMorgan(CONDITIONAL_OR));
  }
}
