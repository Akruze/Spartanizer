package org.spartan.refactoring.utils;

/**
 * A utility parser that resolves a variable's short name, and determines
 * whether a pre-existing name is a generic variation of the type's name. <br>
 * A variable's short name is a single-character name, determined by the first
 * character in the last word of the type's name.<br>
 * For example:
 * <code><pre>  public void execute(HTTPSecureConnection httpSecureConnection) {...}</pre></code>
 * would become<br>
 * <code><pre>  public void execute(HTTPSecureConnection c) {...}</pre></code>
 *
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2015-08-25
 */
public class JavaTypeNameParser {
  /** The type name managed by this instance */
  public final String typeName;
  /**
   * Instantiates this class
   *
   * @param typeName the Java type name to parse
   * @param isCollection denotes whether the type is a collection or a varargs
   *          parameter
   */
  public JavaTypeNameParser(final String typeName) {
    this.typeName = typeName;
  }
  /**
   * Returns whether a variable name is a generic variable of the type name
   *
   * @param variableName the name of the variable
   * @return true if the variable name is a generic variation of the type name,
   *         false otherwise
   */
  public boolean isGenericVariation(final String variableName) {
    return typeName.equalsIgnoreCase(variableName) || lowerCaseContains(typeName, variableName) || lowerCaseContains(typeName, toSingular(variableName));
  }
  /**
   * Returns the calculated short name for the type
   *
   * @return the type's short name
   */
  public String shortName() {
    return String.valueOf(Character.toLowerCase(lastName().charAt(0)));
  }
  @SuppressWarnings("static-method") private String toSingular(final String s) {
    return s == null ? null
        : s.endsWith("ies") ? s.substring(0, s.length() - 3) + "y" : s.endsWith("es") ? s.substring(0, s.length() - 2) : s.endsWith("s") ? s.substring(0, s.length() - 1) : s;
  }
  /**
   * Shorthand for n.equals(this.shortName())
   *
   * @param s JD
   * @return true if the provided name equals the type's short name
   */
  public boolean isShort(final String s) {
    return s.equals(shortName());
  }
  String lastName() {
    return typeName.substring(lastNameIndex());
  }
  int lastNameIndex() {
    for (int $ = typeName.length() - 1; $ > 0; --$) {
      if (isLower($) && isUpper($ - 1))
        return $ - 1;
      if (isUpper($) && isLower($ - 1))
        return $;
    }
    return 0;
  }
  private boolean isLower(final int i) {
    return Character.isLowerCase(typeName.charAt(i));
  }
  private boolean isUpper(final int i) {
    return Character.isUpperCase(typeName.charAt(i));
  }
  @SuppressWarnings("static-method") private boolean lowerCaseContains(final String string, final String substring) {
    return string.toLowerCase().contains(substring.toLowerCase());
  }
}