package org.lingg.learn.zookeeper;

import org.I0Itec.zkclient.*;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKClientBase {

    static final String CONNECT_ADDR = "192.168.181.129:2181,192.168.181.129:2182,192.168.181.129:2183";
    static final int SESSION_OUTTIME = 5000;
//    static final CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR), 10000);

//        zkc.createEphemeral("/tmp");
//        zkc.createPersistent("/persistent/child1", true);
//        Thread.sleep(1000L);
//        zkc.deleteRecursive("/persistent");


//        zkc.createPersistent("/super", "super data");
//        zkc.createPersistent("/super/c1", "c1 data");
//        zkc.createPersistent("/super/c2", "c2 data");
//        List<String> list = zkc.getChildren("/super");
//        for(String p : list){
//            System.out.println(p);
//            String rp = "/super/"+p;
//            String data = zkc.readData(rp);
//            System.out.println("节点为：" + rp + " 内容为：" + data);
//        }
//        zkc.deleteRecursive("/super");


//        zkc.writeData("/super/c1", "新的c1内容");
//        System.out.println(zkc.readData("/super/c1").toString());
//        System.out.println(zkc.exists("/super/c1"));


        zkc.deleteRecursive("/super");
        zkc.subscribeChildChanges("/super", new IZkChildListener() {
            //只是监听了节点子节点的新增和删除，数据变化不监听，孙子节点也不监听
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.err.println("parentPath:"+parentPath+"   currentChilds:"+currentChilds);
            }
        });
        zkc.createPersistent("/super", "1111");
        Thread.sleep(1000L);
        zkc.createPersistent("/super/c1", "22222");
        Thread.sleep(1000L);
        zkc.createPersistent("/super/c2", "555");
        Thread.sleep(1000L);
        zkc.writeData("/super/c2","999");
        Thread.sleep(1000L);
        zkc.delete("/super/c1");
        Thread.sleep(1000L);
        zkc.createPersistent("/super/c2/d1", "3333"); //
        Thread.sleep(1000L);

        //关注data的变化
        zkc.subscribeDataChanges("/super", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {

            }
        });

        zkc.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

            }

            @Override
            public void handleNewSession() throws Exception {

            }

            @Override
            public void handleSessionEstablishmentError(Throwable error) throws Exception {

            }
        });

        zkc.close();
    }
}
