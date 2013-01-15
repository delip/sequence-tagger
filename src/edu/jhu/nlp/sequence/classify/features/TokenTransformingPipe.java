package edu.jhu.nlp.sequence.classify.features;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;


public abstract class TokenTransformingPipe extends Pipe {
  
  private static final long serialVersionUID = -3805902643380474655L;

  public abstract Token transform(Token token);
  
  @Override
  public Instance pipe (Instance carrier) {
    Object data = carrier.getData();
    if(data instanceof TokenSequence) {
      TokenSequence sequence = (TokenSequence) data;
      for(int i = 0; i < sequence.size(); i++) {
        Token token = sequence.get(i);
        sequence.set(i, transform(token));
      }
        
    }
    return carrier;
  }

}
