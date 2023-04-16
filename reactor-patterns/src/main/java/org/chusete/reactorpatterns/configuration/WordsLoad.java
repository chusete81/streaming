package org.chusete.reactorpatterns.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Slf4j
@Component
public class WordsLoad {
    private static String[] words;

    public WordsLoad() {
        initialize();
    }

    private void initialize() {
        log.debug("Initializing words data...");

        int wordCount = 0;
        File file = null;
        BufferedReader br = null;

        try {
            file = ResourceUtils.getFile("classpath:./palabras.txt");
            br = new BufferedReader(new FileReader(file));

            // 1st pass counting words
            String s;
            while ((s = br.readLine()) != null) {
                wordCount++;
            }
            br.close();

            words = new String[wordCount];

            // 2nd pass reading words
            int i = 0;
            br = new BufferedReader(new FileReader(file));
            while ((s = br.readLine()) != null) {
                words[i++] = s;
            }
            br.close();
        } catch (FileNotFoundException e) {
            log.error("Words file not found: ", e);
        } catch (IOException e) {
            log.error("Error reading words file: ", e);
        }

        log.debug("{} words loaded", wordCount);
    }

    public String getRandomWord() {
        return words[(int) (Math.random() * words.length)];
    }
}
