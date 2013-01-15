package edu.jhu.nlp.sequence.classify.features;

import cc.mallet.types.Token;
import cc.mallet.util.PropertyList;
import cc.mallet.util.PropertyList.Iterator;

public class LangIDPrefixingPipe extends TokenTransformingPipe {

  private static final long serialVersionUID = 5477417916977727247L;
  private String _langIDPrefix = "SOURCE";
  
  public LangIDPrefixingPipe(String langIDPrefix) {
    _langIDPrefix = langIDPrefix;
  }
  
  @Override
  public Token transform(Token token) {
    PropertyList featureList = token.getFeatures();
    Iterator featureIterator = featureList.iterator();
    for(; featureIterator.hasNext(); featureIterator.next()) {
      String key = featureIterator.getKey();
      double value = featureIterator.getNumericValue();
      token.setFeatureValue(_langIDPrefix + "_" +  key, value);
    }
    return token;
  }

}
