package org.lingg.learn.zookeeper;

import org.apache.zookeeper.*;
import java.util.concurrent.CountDownLatch;

public class ZKBase {
    static final String CONNECT_ADDR = "192.168.181.129:2181,192.168.181.129:2182,192.168.181.129:2183";
    static final int SESSION_OUTTIME = 5000;
    static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                if (Event.KeeperState.SyncConnected == state) {
                    if (Event.EventType.None == type) {
                        connectedSemaphore.countDown();//建立连接，程序继续向下
                        System.out.println("zk 建立连接");
                    }
                }
            }
        });

        System.out.println("等待 zk 建立连接。。。。");
        //阻塞等待
        connectedSemaphore.await();

        String s = zk.create("/testRoot", "testData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(s);

        System.err.println(new String(zk.getData("/testRoot", false, null)));

        Thread.sleep(2000L);



        zk.close();


    }
}
