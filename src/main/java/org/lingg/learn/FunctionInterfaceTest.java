package org.lingg.learn;

@FunctionalInterface
public interface FunctionInterfaceTest {

    public void test();

    public default void test2(){
        System.out.println("TestHashMap");
    }
}
