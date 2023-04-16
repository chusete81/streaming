package org.chusete.reactorpatterns.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.chusete.reactorpatterns.configuration.PropertiesConfiguration;
import org.chusete.reactorpatterns.configuration.WordsLoad;
import org.chusete.reactorpatterns.service.MiscService;
import org.chusete.reactorpatterns.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MiscServiceImpl implements MiscService {

    @Autowired
    PropertiesConfiguration props;

    @Autowired
    WordsLoad words;

    @Override
    public String randomPhrase() {
        final var maxWordLength = props.getMaxWordLength();
        final var maxNumWords = props.getMaxNumWords();

        StringBuilder sb = new StringBuilder();
        sb.append(randomWord(maxWordLength));

        var numWords = CommonUtils.randomInt(maxNumWords);
        while (numWords-- > 0)
            sb.append(" ").append(randomWord(maxWordLength));

        return sb.toString();
    }

    @Override
    public int wordCount(String s) {
        return s.split(" ").length;
    }

    @Override
    public int letterCount(String s) {
        return s.replaceAll(" ", "").length();
    }

    private String randomWord(int limit) {
        String word;

        do {
            word = words.getRandomWord();
        } while (word.length() > limit);

        return word;
    }
}
