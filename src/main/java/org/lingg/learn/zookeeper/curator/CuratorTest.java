package org.lingg.learn.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//https://www.jianshu.com/p/70151fc0ef5d

public class CuratorTest {
    static final String connectionInfo = "192.168.181.129:2181,192.168.181.129:2182,192.168.181.129:2183";

    public static void main(String[] args) throws Exception {
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//        CuratorFramework client = CuratorFrameworkFactory.newClient(
//                connectionInfo, 5000, 3000, retryPolicy);


        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(connectionInfo)
                        .sessionTimeoutMs(50000)
                        .connectionTimeoutMs(50000)
                        .retryPolicy(retryPolicy)
                        .namespace("base")
                        .build();
        client.start();

//        client.create().forPath("/path");
//        client.create().forPath("/path/p2","init2".getBytes());
//        client.create().withMode(CreateMode.EPHEMERAL).forPath("/path");
//        client.create().withMode(CreateMode.EPHEMERAL).forPath("/path","init".getBytes());

//        client.delete().deletingChildrenIfNeeded().forPath("/path");

//        client.setData().forPath("/path","data".getBytes());

//        client.inTransaction().check().forPath("/path")
//                .and()
//                .create().withMode(CreateMode.EPHEMERAL).forPath("/path","data".getBytes())
//                .and()
//                .setData().withVersion(10086).forPath("/path","data2".getBytes())
//                .and()
//                .commit();

        //一个异步创建节点的例子
        Executor executor = Executors.newFixedThreadPool(2);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println(String.format("eventType:%s,resultCode:%s", curatorEvent.getType(), curatorEvent.getResultCode()));
                }, executor)
                .forPath("/path");


    }
}
