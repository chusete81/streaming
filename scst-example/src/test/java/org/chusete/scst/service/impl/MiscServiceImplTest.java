package org.chusete.scst.service.impl;

import org.chusete.scst.service.MiscService;
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
        var s = miscService.randomString();
        Assertions.assertInstanceOf(String.class, s);
    }
}