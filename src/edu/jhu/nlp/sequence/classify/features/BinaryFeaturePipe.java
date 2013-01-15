package edu.jhu.nlp.sequence.classify.features;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;

/**
 * Refactored from Mallet SimpleTagger
 * Reads input in CONLL style format and makes instances out of it.
 * @author Delip Rao
 */

/**
 * Converts an external encoding of a sequence of elements with binary
 * features to a {@link FeatureVectorSequence}.  If target processing
 * is on (training or labeled test data), it extracts element labels
 * from the external encoding to create a target {@link LabelSequence}.
 * Two external encodings are supported:
 * <ol>
 *  <li> A {@link String} containing lines of whitespace-separated tokens.</li>
 *  <li> a {@link String}<code>[][]</code>.</li>
 * </ol>
 *
 * Both represent rows of tokens. When target processing is on, the last token
 * in each row is the label of the sequence element represented by
 * this row. All other tokens in the row, or all tokens in the row if
 * not target processing, are the names of features that are on for
 * the sequence element described by the row.
 *           
 */
final class BinaryFeaturePipe extends Pipe
{
  /**
   * 
   */
  private static final long serialVersionUID = -569220061644698765L;

  /**
   * Creates a new
   * <code>BinaryFeaturePipe</code> instance.
   */
  public BinaryFeaturePipe()
  {
    super (new Alphabet(), new LabelAlphabet());
  }

  /**
   * Parses a string representing a sequence of rows of tokens into an
   * array of arrays of tokens.
   *
   * @param sentence a <code>String</code>
   * @return the corresponding array of arrays of tokens.
   */
  private String[][] parseSentence(String sentence)
  {
    String[] lines = sentence.split("\n");
    String[][] tokens = new String[lines.length][];
    for (int i = 0; i < lines.length; i++)
      tokens[i] = lines[i].split(" ");
    return tokens;
  }

  public Instance pipe (Instance carrier)
  {
    Object inputData = carrier.getData();
    Alphabet features = getDataAlphabet();
    LabelAlphabet labels;
    LabelSequence target = null;
    String [][] tokens;
    if (inputData instanceof String)
      tokens = parseSentence((String)inputData);
    else if (inputData instanceof String[][])
      tokens = (String[][])inputData;
    else
      throw new IllegalArgumentException("Not a String or String[][]; got "+inputData);
    FeatureVector[] fvs = new FeatureVector[tokens.length];
    if (isTargetProcessing())
    {
      labels = (LabelAlphabet)getTargetAlphabet();
      target = new LabelSequence (labels, tokens.length);
    }
    for (int l = 0; l < tokens.length; l++) {
      int nFeatures;
      if (isTargetProcessing())
      {
        if (tokens[l].length < 1)
          throw new IllegalStateException ("Missing label at line " + l + " instance "+carrier.getName ());
        nFeatures = tokens[l].length - 1;
        target.add(tokens[l][nFeatures]);
      }
      else nFeatures = tokens[l].length;
      int featureIndices[] = new int[nFeatures];
      for (int f = 0; f < nFeatures; f++)
        featureIndices[f] = features.lookupIndex(tokens[l][f]);
      fvs[l] =  new FeatureVector(features, featureIndices);
    }
    carrier.setData(new FeatureVectorSequence(fvs));
    if (isTargetProcessing())
      carrier.setTarget(target);
    else
      carrier.setTarget(new LabelSequence(getTargetAlphabet()));
    return carrier;
  }
}