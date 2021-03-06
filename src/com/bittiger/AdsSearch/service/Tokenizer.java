package com.bittiger.AdsSearch.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.PorterStemmer;

@Service
public final class Tokenizer {
    private AttributeFactory factory;
    private StandardTokenizer tokenizer;
    private PorterStemmer stemmer;
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Tokenizer.class);

    public Tokenizer() {
        this.factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
        this.tokenizer = new StandardTokenizer(factory);
        this.stemmer = new PorterStemmer();
    }
    
    //TODO A elegant solution for concurrency need to be implemented rather than 
    //Simply using synchronized.
    public synchronized List<String> tokenize(String text) throws IOException {
        List<String> tokens = new ArrayList<>();
        if (StringUtils.isBlank(text)) return tokens;
        
        this.tokenizer.setReader(new StringReader(text));
        try {
            tokenizer.reset();
        } catch (IOException e1) {
            logger.error(e1.getMessage());
        }
        
        CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);
        try {
            while (tokenizer.incrementToken()) {
                String unstemmedToken = attr.toString().toLowerCase();
                stemmer.setCurrent(unstemmedToken);
                stemmer.stem();
                String stemmedToken = stemmer.getCurrent();
                tokens.add(unstemmedToken);
                //Sometimes lucene give weird result...like stem 'boy' to 'boi'
                if (!stemmedToken.equals(unstemmedToken)) {
                    tokens.add(stemmedToken);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        tokenizer.close();
        return tokens;
    }
    
}
