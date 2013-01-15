package edu.jhu.nlp.sequence.classify.features;

import java.util.regex.Pattern;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.tsf.FeaturesInWindow;
import cc.mallet.pipe.tsf.OffsetConjunctions;
import cc.mallet.pipe.tsf.RegexMatches;
import cc.mallet.pipe.tsf.TokenText;
import cc.mallet.pipe.tsf.TokenTextCharNGrams;
import cc.mallet.pipe.tsf.TokenTextCharPrefix;
import cc.mallet.pipe.tsf.TokenTextCharSuffix;

/**
 * A basic NER feature factory. Implement extensions of this for additional features
 * (for example cross lingual features)
 * @author Delip Rao
 *
 */
public class BasicNERFeatureFactory extends CompositeFeatureFactory {

  private String _langIDPrefix = "SOURCE";
  
  public BasicNERFeatureFactory() {
    this("SOURCE");
  }
  
  public BasicNERFeatureFactory(String langIDPrefix) {
    _langIDPrefix = langIDPrefix;
  }
  
  public void init() {
    _featurePipeline.rebuild();
    // convert strings to tokens
    _featurePipeline.addPipe(new TokenizerPipe());
  }
  
  public void build() {
    init();
    buildSpecific();
    _featurePipeline.addPipe(new LangIDPrefixingPipe(_langIDPrefix));
  }
  
  public void buildSpecific() {
    _featurePipeline.addPipes(new Pipe [] {
        /* Pattern matching features on the words. */
        
        new RegexMatches("INITCAP", Pattern.compile (FC.CAPS+".*")),
        new RegexMatches("CAPITALIZED", Pattern.compile (FC.CAPS+FC.LOW+"*")),
        new RegexMatches("ALLCAPS", Pattern.compile (FC.CAPS+"+")),
        new RegexMatches("MIXEDCAPS", Pattern.compile ("[A-Z][a-z]+[A-Z][A-Za-z]*")),

        new RegexMatches("CONTAINSDIGITS", Pattern.compile (".*[0-9].*")),
        new RegexMatches("SINGLEDIGITS", Pattern.compile ("[0-9]")),
        new RegexMatches("DOUBLEDIGITS", Pattern.compile ("[0-9][0-9]")),
        new RegexMatches("ALLDIGITS", Pattern.compile ("[0-9]+")),
        new RegexMatches("NUMERICAL", Pattern.compile ("[-0-9]+[\\.,]+[0-9\\.,]+")),
        new RegexMatches("ALPHNUMERIC", Pattern.compile ("[A-Za-z0-9]+")),
        new RegexMatches("ROMAN", Pattern.compile ("[ivxdlcm]+|[IVXDLCM]+")),
        new RegexMatches("MULTIDOTS", Pattern.compile ("\\.\\.+")),
        new RegexMatches("ENDSINDOT", Pattern.compile ("[^\\.]+.*\\.")),
        new RegexMatches("CONTAINSDASH", Pattern.compile(FC.ALPHANUM+"+-"+FC.ALPHANUM+"*")),
        new RegexMatches("ACRO", Pattern.compile ("[A-Z][A-Z\\.]*\\.[A-Z\\.]*")),
        new RegexMatches("LONELYINITIAL", Pattern.compile (FC.CAPS+"\\.")),
        new RegexMatches("SINGLECHAR", Pattern.compile (FC.ALPHA)),
        new RegexMatches("CAPLETTER", Pattern.compile ("[A-Z]")),
        new RegexMatches("PUNC", Pattern.compile (FC.PUNT)),
        new RegexMatches("QUOTE", Pattern.compile (FC.QUOTE)),
        new RegexMatches("STARTDASH", Pattern.compile ("-.*")),
        new RegexMatches("ENDDASH", Pattern.compile (".*-")),
        new RegexMatches("FORWARDSLASH", Pattern.compile ("/")),       
        new RegexMatches("ISBRACKET", Pattern.compile ("[()]")),       
        
        
        new TokenSequenceLowercase(),

        /* Make the word a feature. */
        new TokenText("WORD="),

        new TokenTextCharSuffix("SUFFIX2=",2),
        new TokenTextCharSuffix("SUFFIX3=",3),
        new TokenTextCharSuffix("SUFFIX4=",4),
        new TokenTextCharPrefix("PREFIX2=",2),
        new TokenTextCharPrefix("PREFIX3=",3),
        new TokenTextCharPrefix("PREFIX4=",4),
        new InBracket("INBRACKET",true),
        new TokenTextCharNGrams ("CHARNGRAM=", new int[] {2,3,4}),
        
        /* FeatureInWindow features. */
        new FeaturesInWindow("WINDOW=",-1,1,
          Pattern.compile("WORD=.*|SUFFIX.*|PREFIX.*|[A-Z]+"),true),
          
        
        new OffsetConjunctions(true,Pattern.compile("WORD=.*"),
              new int[][]{{-1},{1},{-1,0}})
    });
  }

  @Override
  public Pipe getPipe() {
    return getPipe(new TokenSequence2FeatureVectorSequence (true, true));
  }
  
}
