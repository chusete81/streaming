package org.chusete.reactorpatterns.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.chusete.reactorpatterns.configuration.PropertiesConfiguration;
import org.chusete.reactorpatterns.service.MiscService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MiscServiceImpl implements MiscService {

    @Autowired
    PropertiesConfiguration props;

    @Override
    public String randomPhrase() {
        final var maxWordLength = props.getMaxWordLength();
        final var maxNumWords = props.getMaxNumWords();

        StringBuilder sb = new StringBuilder();
        sb.append(randomWord(maxWordLength));

        var numWords = randomInt(maxNumWords);
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
        var numChars = randomInt(limit);

        StringBuilder sb = new StringBuilder();

        while (numChars-- >= 0)
            sb.append(randomLetter());

        return sb.toString();
    }

    private String randomLetter() {
        var letters = new String[] {
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        return letters[randomInt(letters.length)];
    }

    private int randomInt(int limit) {
        return (int) (Math.random() * limit);
    }
}
