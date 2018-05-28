package org.lingg.learn.redisInAction.mytest;

public class VarargsTest {

    public static void main(String[] args) {
        test("hello","world","welcome","haha");
    }

    public static  void test(String... args){
        System.out.println(args.length);
        System.out.println(args[1]);
        System.out.println(args.getClass());
        for(String i : args){
            System.out.println(i);
        }
    }
}
