package edu.jhu.nlp.sequence.classify.features;

/**
 * Features for text inside parenthesis
 * Modified from original code by Ryan McDonald in edu.upenn.ldc.lctl.taggers
 * Modifications include
 *  - Mallet RC4 compliance
 *  - Minor fixes
 * @author Delip Rao
 */

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class InBracket extends Pipe {

  private static final long serialVersionUID = 3412410205124847147L;
  
  String name;
  boolean ignoreCase;

  public InBracket (String name, boolean ignoreCase) {
    this.name = name;
    this.ignoreCase = ignoreCase;
  }

  public Instance pipe (Instance carrier) {
    TokenSequence ts = (TokenSequence) carrier.getData();
    int depth = 0;
    for (int i = 0; i < ts.size(); i++) {
      
      Token t = ts.get(i);
      String s = t.getText();
      s = ignoreCase ? s.toLowerCase() : s;
      if(s.equals("(")) {
        depth++;
        t.setFeatureValue(name,1.0);
      }
      else if(s.equals(")")) {
        depth--;
        t.setFeatureValue(name,1.0);
      }
      else if(depth > 0)
        t.setFeatureValue(name,1.0);			
    }
    return carrier;
  }
}
