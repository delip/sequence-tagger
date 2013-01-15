package edu.jhu.nlp.sequence.classify;

import cc.mallet.types.Sequence;

/**
 * 
 * @author Delip Rao
 *
 */
public class DefaultOutputCallback implements OutputCallback {

  @SuppressWarnings("unchecked")
  public void process(Sequence input, Sequence output) {
    // assert input.size() == output.size()
    for(int i = 0; i < input.size(); i++) {
      System.out.println(output.get(i));
      //FeatureVector fv = (FeatureVector)input.get(i);
      //System.out.println(" " + fv.toString(true));
    }
    System.out.println("");
  }

}
