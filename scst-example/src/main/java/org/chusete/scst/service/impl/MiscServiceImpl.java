package org.chusete.scst.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.chusete.scst.service.MiscService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MiscServiceImpl implements MiscService {
    final int MAX_WORDS = 5;
    final int MAX_LETTERS = 8;

    @Override
    public String randomString() {
        var numWords = randomInt(MAX_WORDS);

        StringBuilder sb = new StringBuilder();

        sb.append(randomWord(MAX_LETTERS));

        while (numWords-- > 0)
            sb.append(" ").append(randomWord(MAX_LETTERS));

        return sb.toString();
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
