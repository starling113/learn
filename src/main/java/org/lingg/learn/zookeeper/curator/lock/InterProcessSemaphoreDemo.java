package org.lingg.learn.zookeeper.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

//信号量—Shared Semaphore
//一个计数的信号量类似JDK的Semaphore。 JDK中Semaphore维护的一组许可(permits)，而Curator中称之为租约(Lease)。 有两种方式可以决定semaphore的最大租约数。第一种方式是用户给定path并且指定最大LeaseSize。第二种方式用户给定path并且使用SharedCountReader类。如果不使用SharedCountReader, 必须保证所有实例在多进程中使用相同的(最大)租约数量,否则有可能出现A进程中的实例持有最大租约数量为10，但是在B进程中持有的最大租约数量为20，此时租约的意义就失效了。
//
//这次调用acquire()会返回一个租约对象。 客户端必须在finally中close这些租约对象，否则这些租约会丢失掉。 但是， 但是，如果客户端session由于某种原因比如crash丢掉， 那么这些客户端持有的租约会自动close， 这样其它客户端可以继续使用这些租约。 租约还可以通过下面的方式返还：
//
public class InterProcessSemaphoreDemo {

    private static final int MAX_LEASE = 10;
    private static final String PATH = "/examples/locks";

    public static void main(String[] args) throws Exception {
        FakeLimitedResource resource = new FakeLimitedResource();
        try (TestingServer server = new TestingServer()) {

            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, PATH, MAX_LEASE);
            Collection<Lease> leases = semaphore.acquire(5);
            System.out.println("get " + leases.size() + " leases");
            Lease lease = semaphore.acquire();
            System.out.println("get another lease");

            resource.use();

            Collection<Lease> leases2 = semaphore.acquire(5, 10, TimeUnit.SECONDS);
            System.out.println("Should timeout and acquire return " + leases2);

            System.out.println("return one lease");
            semaphore.returnLease(lease);
            System.out.println("return another 5 leases");
            semaphore.returnAll(leases);
        }
    }
}

//作者：zhrowable
//链接：https://www.jianshu.com/p/70151fc0ef5d
//來源：简书
//简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。