package org.chusete.reactorpatterns.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WordsLoadTest {

    @Autowired
    WordsLoad words;

    @Test
    void getRandomWord() {
        String word = words.getRandomWord();

        Assertions.assertNotNull(word);
        Assertions.assertTrue(word.length() > 0);
    }
}