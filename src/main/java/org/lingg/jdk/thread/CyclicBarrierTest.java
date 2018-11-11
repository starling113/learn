package org.lingg.jdk.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest {
    static CyclicBarrier endLine = new CyclicBarrier(5+1);

    @Test
    public void test() throws Exception{
        long start = System.currentTimeMillis();
        for(int i=1; i<=5; i++){
            int finalI = i;
            new Thread(()->{

                try {
                    Thread.sleep(1000 * finalI);
                    endLine.await();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }).start();
        }

        endLine.await();
        long end = System.currentTimeMillis();
        System.out.println("all use : "+ (end - start));
    }
}
