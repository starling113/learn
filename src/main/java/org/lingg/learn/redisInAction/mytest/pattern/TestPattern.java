package org.lingg.learn.redisInAction.mytest.pattern;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPattern {

    @Test
    public void test1(){
        Pattern pattern = Pattern.compile("a*b");
        Matcher matcher = pattern.matcher("dacccccccccccccccb");
        boolean b = matcher.find();
        System.out.println(b);
    }
}
