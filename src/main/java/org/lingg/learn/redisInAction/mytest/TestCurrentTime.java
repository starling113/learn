package org.lingg.learn.redisInAction.mytest;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestCurrentTime {

    @Test
    public void test1(){
        long now = System.currentTimeMillis();
        System.out.println("now = " + now);
        System.out.println("now = " + Instant.now().toEpochMilli());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        System.out.println(fmt.format(LocalDateTime.now()));
    }
}
