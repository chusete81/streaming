package org.chusete.reactorpatterns.service.impl;

import org.chusete.reactorpatterns.service.MiscService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MiscServiceImplTest {
    @Autowired
    MiscService miscService;

    @Test
    void randomString() {
        var s = miscService.randomPhrase();
        Assertions.assertInstanceOf(String.class, s);
    }
}