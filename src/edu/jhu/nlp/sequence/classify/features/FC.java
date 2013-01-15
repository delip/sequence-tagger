package edu.jhu.nlp.sequence.classify.features;

/**
 * A listing of feature constants
 * @author Delip Rao
 *
 */
public class FC {
  
  private FC() {}
  
  public static final String CAPS = "[A-Z]";
  public static final String LOW = "[a-z]";
  public static final String CAPSNUM = "[A-Z0-9]";
  public static final String ALPHA = "[A-Za-z]";
  public static final String ALPHANUM = "[A-Za-z0-9]";
  public static final String PUNT = "[,\\.;:?!]";
  public static final String QUOTE = "[\"`']";

}
