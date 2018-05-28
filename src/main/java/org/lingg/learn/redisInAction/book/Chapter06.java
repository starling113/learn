package org.lingg.learn.redisInAction.book;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Chapter06 {
    private static final String VALID_CHARACTERS = "`abcdefghijklmnopqrstuvwxyz{";

    public static void main(String[] args) throws Exception {
        new Chapter06().run();
    }

    public void run() throws Exception {
        Jedis conn = new Jedis(RedisConst.redisHost);
        conn.select(RedisConst.redisDbIndex);

//        testAddUpdateContact(conn);
//        testAddressBookAutocomplete(conn);
//        testDistributedLocking(conn);// 分布式锁
//        testCountingSemaphore(conn);// 信号量
//        testDelayedTasks(conn);
//        testMultiRecipientMessaging(conn); // 多客户端接收消息
        testFileDistribution(conn); // 文件分发
    }

    public void testAddUpdateContact(Jedis conn) {
        System.out.println("\n----- testAddUpdateContact -----");
        conn.del("recent:user");

        System.out.println("Let's add a few contacts...");
        for (int i = 0; i < 10; i++) {
            addUpdateContact(conn, "user", "contact-" + ((int) Math.floor(i / 3)) + '-' + i);
        }
        System.out.println("Current recently contacted contacts");
        List<String> contacts = conn.lrange("recent:user", 0, -1);
        for (String contact : contacts) {
            System.out.println("  " + contact);
        }
        assert contacts.size() >= 10;
        System.out.println();

        System.out.println("Let's pull one of the older ones up to the front");
        addUpdateContact(conn, "user", "contact-1-4");
        contacts = conn.lrange("recent:user", 0, 2);// 取前3个
        System.out.println("New top-3 contacts:");
        for (String contact : contacts) {
            System.out.println("  " + contact);
        }
        assert "contact-1-4".equals(contacts.get(0));
        System.out.println();

        System.out.println("Let's remove a contact...");
        removeContact(conn, "user", "contact-2-6");
        contacts = conn.lrange("recent:user", 0, -1);
        System.out.println("New contacts:");
        for (String contact : contacts) {
            System.out.println("  " + contact);
        }
        assert contacts.size() >= 9;
        System.out.println();

        System.out.println("And let's finally autocomplete on ");
        List<String> all = conn.lrange("recent:user", 0, -1);
        contacts = fetchAutocompleteList(conn, "user", "contact-");
        System.out.println("all.equals(contacts) : " + all.equals(contacts));
        List<String> equiv = new ArrayList<>();
        for (String contact : all) {
            if (contact.startsWith("contact-2-")) {
                equiv.add(contact);
            }
        }
        contacts = fetchAutocompleteList(conn, "user", "contact-2-"); // 获取“contact-2-”开头的所有人
        Collections.sort(equiv); // contact-2-XXX
        Collections.sort(contacts);
        System.out.println(equiv.equals(contacts));
        conn.del("recent:user");
    }

    public void testAddressBookAutocomplete(Jedis conn) {
        System.out.println("\n----- testAddressBookAutocomplete -----");
        conn.del("members:test");
        System.out.println("the start/end range of 'abc' is: " + Arrays.toString(findPrefixRange("abc")));
        System.out.println();

        System.out.println("Let's add a few people to the guild");
        for (String name : new String[]{"jeff", "jenny", "jack", "jennifer"}) {
            joinGuild(conn, "test", name);
        }
        System.out.println();
        System.out.println("now let's try to find users with names starting with 'je':");
        Set<String> r = autocompleteOnPrefix(conn, "test", "je");
        System.out.println("prefix je : " + r);
        System.out.println(r.size() == 3);

        System.out.println("jeff just left to join a different guild...");
        leaveGuild(conn, "test", "jeff");
        r = autocompleteOnPrefix(conn, "test", "je");
        System.out.println(r);
        System.out.println(r.size() == 2);
        conn.del("members:test");
    }

    public void testDistributedLocking(Jedis conn) throws InterruptedException {
        System.out.println("\n----- testDistributedLocking 分布式锁 -----");
        conn.del("lock:testlock");
        System.out.println("Getting an initial lock...");
        System.out.println("第一次获得锁：" + acquireLockWithTimeout(conn, "testlock", 1000, 1000));
        System.out.println("Got it!");
        System.out.println("Trying to get it again without releasing the first one...");
        System.out.println("10毫秒后获得锁（前一个锁还没释放）：" + acquireLockWithTimeout(conn, "testlock", 10, 1000));
        System.out.println("Failed to get it!");
        System.out.println();

        System.out.println("Waiting for the lock to timeout...");
        Thread.sleep(2000);
        System.out.println("Getting the lock again...");
        String lockId = acquireLockWithTimeout(conn, "testlock", 1000, 1000);
        System.out.println("等2秒再次获得锁 lockId = " + lockId);
        System.out.println("Got it!");
        System.out.println("Releasing the lock...");
        System.out.println("释放锁" + lockId + " 结果：" + releaseLock(conn, "testlock", lockId));
        System.out.println("Released it...");
        System.out.println();

        System.out.println("Acquiring it again...");
        System.out.println("再次获得锁 lockId = " + acquireLockWithTimeout(conn, "testlock", 1000, 1000));
        System.out.println("Got it!");
        conn.del("lock:testlock");
    }

    public void testCountingSemaphore(Jedis conn) throws InterruptedException {
        System.out.println("\n----- testCountingSemaphore 信号量-----");
        conn.del("testsem", "testsem:owner", "testsem:counter");
        System.out.println("Getting 3 initial semaphores with a limit of 3...");
        for (int i = 0; i < 3; i++) {
            System.out.println("获取信号量：" + acquireFairSemaphore(conn, "testsem", 3, 1000));
        }
        System.out.println("Done!");
        System.out.println("Getting one more that should fail...");
        System.err.println("获取信号量：" + acquireFairSemaphore(conn, "testsem", 3, 1000));
        System.out.println("Couldn't get it!");
        System.out.println();

        System.out.println("等待信号量都超时，Lets's wait for some of them to time out");
        Thread.sleep(2000);
        System.out.println("获取一个信号量Can we get one?");
        String id = acquireFairSemaphore(conn, "testsem", 3, 1000);
        System.out.println("获取信号量：" + id);
        System.out.println("Got one!");
        System.out.println("Let's release it...");
        System.out.println("释放信号量：" + releaseFairSemaphore(conn, "testsem", id));
        System.out.println("Released!");
        System.out.println();
        System.out.println("And let's make sure we can get 3 more!");
        for (int i = 0; i < 3; i++) {
            System.out.println("获取信号量：" + acquireFairSemaphore(conn, "testsem", 3, 1000));
        }
        System.out.println("We got them!");
        conn.del("testsem", "testsem:owner", "testsem:counter");
    }

    public void testDelayedTasks(Jedis conn) throws InterruptedException {
        System.out.println("\n----- testDelayedTasks -----");
        conn.del("queue:tqueue", "delayed:");
        System.out.println("Let's start some regular and delayed tasks...");
        for (long delay : new long[]{0, 500, 0, 1500}) {
            System.out.println(executeLater(conn, "tqueue", "testfn", new ArrayList<String>(), delay));
        }
        long r = conn.llen("queue:tqueue");
        System.out.println("How many non-delayed tasks are there (should be 2)? " + r);
        System.out.println("立刻执行任务数：" + r);
        System.out.println();

        System.out.println("Let's start up a thread to bring those delayed tasks back...");
        PollQueueThread thread = new PollQueueThread();
        thread.start();
        System.out.println("Started.");
        System.out.println("Let's wait for those tasks to be prepared...");
        Thread.sleep(2000);
        thread.quit();
        thread.join();
        r = conn.llen("queue:tqueue");
        System.out.println("Waiting is over, how many tasks do we have (should be 4)? " + r);
        System.out.println("结束等待，还有任务数：" + r);
        conn.del("queue:tqueue", "delayed:");
    }

    public void testMultiRecipientMessaging(Jedis conn) {
        System.out.println("\n----- testMultiRecipientMessaging -----");
        conn.del("ids:chat:", "msgs:1", "ids:1", "seen:joe", "seen:jeff", "seen:jenny");

        System.out.println("让我们与一些收件人创建一个新的聊天会话 Let's create a new chat session with some recipients...");
        Set<String> recipients = new HashSet<>(); // 收件人
        recipients.add("jeff");
        recipients.add("jenny");
        String chatId = createChat(conn, "joe", recipients, "message 1");
        System.out.println("Now let's send a few messages...");
        for (int i = 2; i < 5; i++) {
            sendMessage(conn, chatId, "joe", "message " + i);
        }
        System.out.println();

        System.out.println("And let's get the messages that are waiting for jeff and jenny...");
        List<ChatMessages> r1 = fetchPendingMessages(conn, "jeff");  // 获取等待消息
        List<ChatMessages> r2 = fetchPendingMessages(conn, "jenny");
        System.out.println("They are the same? " + r1.equals(r2));
        assert r1.equals(r2);
        System.out.println("Those messages are:");
        for (ChatMessages chat : r1) {
            System.out.println("  chatId: " + chat.chatId);
            System.out.println("    messages:");
            for (Map<String, Object> message : chat.messages) {
                System.out.println("      " + message);
            }
        }

        conn.del("ids:chat:", "msgs:1", "ids:1", "seen:joe", "seen:jeff", "seen:jenny");
    }

    public void testFileDistribution(Jedis conn) throws InterruptedException, IOException {
        System.out.println("\n----- testFileDistribution -----");
        String[] keys = conn.keys("test:*").toArray(new String[0]);
        if (keys.length > 0) {
            conn.del(keys);
        }
        conn.del(
                "msgs:test:",
                "seen:0",
                "seen:source",
                "ids:test:",
                "chat:test:");

        System.out.println("Creating some temporary 'log' files...");
        File f1 = File.createTempFile("temp_redis_1_", ".txt");
        f1.deleteOnExit();
        Writer writer = new FileWriter(f1);
        writer.write("one line\n");
        writer.close();

        File f2 = File.createTempFile("temp_redis_2_", ".txt");
        f2.deleteOnExit();
        writer = new FileWriter(f2);
        for (int i = 0; i < 100; i++) {
            writer.write("many lines " + i + '\n');
        }
        writer.close();

        File f3 = File.createTempFile("temp_redis_3_", ".txt.gz");
        f3.deleteOnExit();
        writer = new OutputStreamWriter(
                new GZIPOutputStream(
                        new FileOutputStream(f3)));
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            writer.write("random line " + Long.toHexString(random.nextLong()) + '\n');
        }
        writer.close();

        long size = f3.length();
        System.out.println("Done.");
        System.out.println();
        System.out.println("Starting up a thread to copy logs to redis...");
        File path = f1.getParentFile();
        CopyLogsThread thread = new CopyLogsThread(path, "test:", 1, size);
        thread.start();

        System.out.println("Let's pause to let some logs get copied to Redis...");
        Thread.sleep(250);
        System.out.println();
        System.out.println("Okay, the logs should be ready. Let's process them!");

        System.out.println("Files should have 1, 100, and 1000 lines");
        TestCallback callback = new TestCallback();
        processLogsFromRedis(conn, "0", callback);
        System.out.println(Arrays.toString(callback.counts.toArray(new Integer[0])));
        assert callback.counts.get(0) == 1;
        assert callback.counts.get(1) == 100;
        assert callback.counts.get(2) == 1000;

        System.out.println();
        System.out.println("Let's wait for the copy thread to finish cleaning up...");
        thread.join();
        System.out.println("Done cleaning out Redis!");

        keys = conn.keys("test:*").toArray(new String[0]);
        if (keys.length > 0) {
            conn.del(keys);
        }
        conn.del(
                "msgs:test:",
                "seen:0",
                "seen:source",
                "ids:test:",
                "chat:test:");
    }

    public void addUpdateContact(Jedis conn, String user, String contact) {
        String acList = "recent:" + user;
        Transaction trans = conn.multi();
        trans.lrem(acList, 0, contact); // count = 0 : 移除表中所有与 value 相等的值
        trans.lpush(acList, contact);
        trans.ltrim(acList, 0, 99);
        trans.exec();
    }

    public void removeContact(Jedis conn, String user, String contact) {
        conn.lrem("recent:" + user, 0, contact);
    }

    public List<String> fetchAutocompleteList(Jedis conn, String user, String prefix) {
        List<String> candidates = conn.lrange("recent:" + user, 0, -1);
        List<String> matches = new ArrayList<String>();
        for (String candidate : candidates) {
            if (candidate.toLowerCase().startsWith(prefix)) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    public String[] findPrefixRange(String prefix) {
        int posn = VALID_CHARACTERS.indexOf(prefix.charAt(prefix.length() - 1));
        char suffix = VALID_CHARACTERS.charAt(posn > 0 ? posn - 1 : 0);
        String start = prefix.substring(0, prefix.length() - 1) + suffix + '{';
        String end = prefix + '{';
        System.err.println("findPrefixRange \t start : " + start + "  \t\t  end : " + end);
        return new String[]{start, end};
    }

    public void joinGuild(Jedis conn, String guild, String user) {
        conn.zadd("members:" + guild, 0, user);
    }

    public void leaveGuild(Jedis conn, String guild, String user) {
        conn.zrem("members:" + guild, user);
    }

    @SuppressWarnings("unchecked")
    public Set<String> autocompleteOnPrefix(Jedis conn, String guild, String prefix) {
        String[] range = findPrefixRange(prefix);
        String start = range[0];
        String end = range[1];
        String identifier = UUID.randomUUID().toString();
        start += identifier;
        end += identifier;
        String zsetName = "members:" + guild;

        conn.zadd(zsetName, 0, start);
        conn.zadd(zsetName, 0, end);

        Set<String> items = null;
        while (true) {
            conn.watch(zsetName);// watch members:test
            int sindex = conn.zrank(zsetName, start).intValue(); // 返回有序集 key 中成员 member 的排名
            int eindex = conn.zrank(zsetName, end).intValue();
            int erange = Math.min(sindex + 9, eindex - 2); // 最多获取10个 记录
            // 开始和结束的标志，有可能在有序列表的最前面两个，此时erange的值为-1，进行区间检查会查出所有值
            if (erange == -1) {
                items = new HashSet<>();
                conn.unwatch();
                break;
            }

            Transaction trans = conn.multi();
            trans.zrem(zsetName, start);// 移除掉临时插入的开始结束标志
            trans.zrem(zsetName, end);
            trans.zrange(zsetName, sindex, erange);// 查符合条件的记录，最多10条
            List<Object> results = trans.exec();
            if (results != null) {
                items = (Set<String>) results.get(results.size() - 1);
                break;
            }
        }

        for (Iterator<String> iterator = items.iterator(); iterator.hasNext(); ) {
            if (iterator.next().indexOf('{') != -1) {
                iterator.remove();
            }
        }
        return items;
    }

    public String acquireLock(Jedis conn, String lockName) {
        return acquireLock(conn, lockName, 10000);
    }

    public String acquireLock(Jedis conn, String lockName, long acquireTimeout) {
        String identifier = UUID.randomUUID().toString();

        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            if (conn.setnx("lock:" + lockName, identifier) == 1) {
                return identifier;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        return null;
    }

    // 带失效时间的锁

    /**
     * @param conn
     * @param lockName
     * @param acquireTimeout 请求超时时间，毫秒数，超过此时间还未获得锁，直接返回
     * @param lockTimeout    锁存活时间，毫秒数
     * @return
     */
    public String acquireLockWithTimeout(Jedis conn, String lockName, long acquireTimeout, long lockTimeout) {
        String identifier = UUID.randomUUID().toString();
        String lockKey = "lock:" + lockName;
        int lockExpire = (int) (lockTimeout / 1000);  // 过期秒数

        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            if (conn.setnx(lockKey, identifier) == 1) {// 设置成功
                conn.expire(lockKey, lockExpire);
                return identifier;
            }
            if (conn.ttl(lockKey) == -1) { // 当 key 存在但没有设置剩余生存时间时，返回 -1     当 key 不存在时，返回 -2
                conn.expire(lockKey, lockExpire);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        // null indicates that the lock was not acquired
        return null;
    }

    public boolean releaseLock(Jedis conn, String lockName, String identifier) {
        String lockKey = "lock:" + lockName;

        while (true) {
            conn.watch(lockKey);
            if (identifier.equals(conn.get(lockKey))) {
                Transaction trans = conn.multi();
                trans.del(lockKey);
                List<Object> results = trans.exec();
                if (results == null) {
                    continue;
                }
                return true;
            }

            conn.unwatch();
            break;
        }

        return false;
    }

    /**
     * 限流的目的是通过对并发访问/请求进行限速或者一个时间窗口内的的请求进行限速来保护系统，一旦达到限制速率则可以拒绝服务。
     * <p>
     * Nginx接入层限流
     * 按照一定的规则如帐号、IP、系统调用逻辑等在Nginx层面做限流
     * 业务应用系统限流
     * 通过业务代码控制流量这个流量可以被称为信号量，可以理解成是一种锁，它可以限制一项资源最多能同时被多少进程访问。
     * <p>
     * https://www.cnblogs.com/strugglesdd/p/7773418.html
     * <p>
     * 不要使用时间戳作为信号量的排序分数，因为在分布式环境中，各个节点的时间差的原因，会出现不公平信号量的现象
     *
     * @param conn
     * @param semname 信号量名称
     * @param limit   限制的个数
     * @param timeout
     * @return
     */
    public String acquireFairSemaphore(Jedis conn, String semname, int limit, long timeout) {
        String identifier = UUID.randomUUID().toString();
        String testsemOwnerZSET = semname + ":owner";// 将计数器的值作为分值，记录到信号量拥有者集合中
        String testsemCounterString = semname + ":counter"; // 计数器，确保最先对计数器自增的客户端可以获得信号量

        long now = System.currentTimeMillis();
        Transaction trans = conn.multi();

        // 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
        trans.zremrangeByScore(semname, "-inf", String.valueOf(now - timeout));// -inf 表示的是负无穷大
        ZParams params = new ZParams();
//        params.weights(1, 0);
        params.weightsByDouble(1, 0);

        // semname 已经移除了无用的数据，将testsemOwnerZSET, semname进行交集计算，移除testsemOwnerZSET中无用元素
        trans.zinterstore(testsemOwnerZSET, params, testsemOwnerZSET, semname);// 删除超时的信号量（ 计算给定的一个或多个有序集的交集）
        trans.incr(testsemCounterString);// 计数器自增
        List<Object> results = trans.exec();
        int counter = ((Long) results.get(results.size() - 1)).intValue();

        trans = conn.multi();
        trans.zadd(semname, now, identifier);// 当前客户端的时间戳做为分值
        trans.zadd(testsemOwnerZSET, counter, identifier);
        trans.zrank(testsemOwnerZSET, identifier); // 返回identifier的排名，通过检查排名，判断客户端是否取得了信号量
        results = trans.exec();
        int result = ((Long) results.get(results.size() - 1)).intValue(); //identifier的排名
        if (result < limit) { // identifier的排名 没有超过限定的数量
            return identifier;
        }

        //没有获得信号量，清理无用数据
        trans = conn.multi();
        trans.zrem(semname, identifier);
        trans.zrem(testsemOwnerZSET, identifier);
        trans.exec();
        return null;
    }

    // 释放信号量
    public boolean releaseFairSemaphore(Jedis conn, String semname, String identifier) {
        Transaction trans = conn.multi();
        trans.zrem(semname, identifier);
        trans.zrem(semname + ":owner", identifier);
        List<Object> results = trans.exec();
        return (Long) results.get(results.size() - 1) == 1;
    }

    public String executeLater(Jedis conn, String queue, String name, List<String> args, long delay) {
        Gson gson = new Gson();
        String identifier = UUID.randomUUID().toString();
        String itemArgs = gson.toJson(args);
        String item = gson.toJson(new String[]{identifier, queue, name, itemArgs});
        if (delay > 0) {
            conn.zadd("delayed:", System.currentTimeMillis() + delay, item);
        } else {
            conn.rpush("queue:" + queue, item);
        }
        return identifier;
    }

    public String createChat(Jedis conn, String sender, Set<String> recipients, String message) {
        String chatId = String.valueOf(conn.incr("ids:chat:"));
        return createChat(conn, sender, recipients, message, chatId);
    }

    /**
     *
     * @param conn
     * @param sender  发件人
     * @param recipients 收件人列表
     * @param message
     * @param chatId   群组id
     * @return
     */
    public String createChat(Jedis conn, String sender, Set<String> recipients, String message, String chatId) {
        recipients.add(sender);

        Transaction trans = conn.multi();
        for (String recipient : recipients) {
            trans.zadd("chat:" + chatId, 0, recipient); // 记录某个群组，有哪些人参加了，score表示此人最近读取群组里哪条消息
            trans.zadd("seen:" + recipient, 0, chatId); // 记录每个人参加了什么群组，score表示最新查看了此群组的哪条消息
        }
        trans.exec();

        return sendMessage(conn, chatId, sender, message);
    }

    public String sendMessage(Jedis conn, String chatId, String sender, String message) {
        String identifier = acquireLock(conn, "chat:" + chatId);// 获得锁 "lock:chat:"+ chatId
        if (identifier == null) {
            throw new RuntimeException("Couldn't get the lock");
        }
        try {
            long messageId = conn.incr("ids:" + chatId);

            HashMap<String, Object> values = new HashMap<String, Object>();
            values.put("id", messageId);
            values.put("ts", System.currentTimeMillis());
            values.put("sender", sender);
            values.put("message", message);
            String packed = new Gson().toJson(values);

            conn.zadd("msgs:" + chatId, messageId, packed);

        } finally {
            releaseLock(conn, "chat:" + chatId, identifier);
        }
        return chatId;
    }

    // 获取等待消息
    @SuppressWarnings("unchecked")
    public List<ChatMessages> fetchPendingMessages(Jedis conn, String recipient) {
        Set<Tuple> seenSet = conn.zrangeWithScores("seen:" + recipient, 0, -1);
        List<Tuple> seenList = new ArrayList<>(seenSet);

        Transaction trans = conn.multi();
        for (Tuple tuple : seenList) {
            String chatId = tuple.getElement();
            int seenId = (int) tuple.getScore();
            trans.zrangeByScore("msgs:" + chatId, String.valueOf(seenId + 1), "inf");
        }
        List<Object> results = trans.exec();

        Gson gson = new Gson();
        Iterator<Tuple> seenIterator = seenList.iterator();
        Iterator<Object> resultsIterator = results.iterator();

        List<ChatMessages> chatMessages = new ArrayList<>();
        List<Object[]> seenUpdates = new ArrayList<>();
        List<Object[]> msgRemoves = new ArrayList<>();
        while (seenIterator.hasNext()) {
            Tuple seen = seenIterator.next();
            Set<String> messageStrings = (Set<String>) resultsIterator.next();
            if (messageStrings.size() == 0) {
                continue;
            }

            int seenId = 0;
            String chatId = seen.getElement();
            List<Map<String, Object>> messages = new ArrayList<>();
            for (String messageJson : messageStrings) {
                Map<String, Object> message = (Map<String, Object>) gson.fromJson(
                        messageJson, new TypeToken<Map<String, Object>>() {
                        }.getType());
                int messageId = ((Double) message.get("id")).intValue();
                if (messageId > seenId) {
                    seenId = messageId;
                }
                message.put("id", messageId);
                messages.add(message);
            }

            conn.zadd("chat:" + chatId, seenId, recipient);
            seenUpdates.add(new Object[]{"seen:" + recipient, seenId, chatId});

            Set<Tuple> minIdSet = conn.zrangeWithScores("chat:" + chatId, 0, 0);
            if (minIdSet.size() > 0) {
                msgRemoves.add(new Object[]{
                        "msgs:" + chatId, minIdSet.iterator().next().getScore()});
            }
            chatMessages.add(new ChatMessages(chatId, messages));
        }

        trans = conn.multi();
        for (Object[] seenUpdate : seenUpdates) {
            trans.zadd(
                    (String) seenUpdate[0],
                    (Integer) seenUpdate[1],
                    (String) seenUpdate[2]);
        }
        for (Object[] msgRemove : msgRemoves) {
            trans.zremrangeByScore(
                    (String) msgRemove[0], 0, ((Double) msgRemove[1]).intValue());
        }
        trans.exec();

        return chatMessages;
    }

    public void processLogsFromRedis(Jedis conn, String id, Callback callback) throws InterruptedException, IOException {
        while (true) {
            List<ChatMessages> fdata = fetchPendingMessages(conn, id);

            for (ChatMessages messages : fdata) {
                for (Map<String, Object> message : messages.messages) {
                    String logFile = (String) message.get("message");

                    if (":done".equals(logFile)) {
                        return;
                    }
                    if (logFile == null || logFile.length() == 0) {
                        continue;
                    }

                    InputStream in = new RedisInputStream(
                            conn, messages.chatId + logFile);
                    if (logFile.endsWith(".gz")) {
                        in = new GZIPInputStream(in);
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    try {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            callback.callback(line);
                        }
                        callback.callback(null);
                    } finally {
                        reader.close();
                    }

                    conn.incr(messages.chatId + logFile + ":done");
                }
            }

            if (fdata.size() == 0) {
                Thread.sleep(100);
            }
        }
    }

    public interface Callback {
        void callback(String line);
    }

    public class TestCallback implements Callback {
        public List<Integer> counts = new ArrayList<Integer>();
        private int index;

        public void callback(String line) {
            if (line == null) {
                index++;
                return;
            }
            while (counts.size() == index) {
                counts.add(0);
            }
            counts.set(index, counts.get(index) + 1);
        }
    }

    public class RedisInputStream extends InputStream {
        private Jedis conn;
        private String key;
        private int pos;

        public RedisInputStream(Jedis conn, String key) {
            this.conn = conn;
            this.key = key;
        }

        @Override
        public int available() throws IOException {
            long len = conn.strlen(key);
            return (int) (len - pos);
        }

        @Override
        public int read() throws IOException {
            byte[] block = conn.substr(key.getBytes(), pos, pos);
            if (block == null || block.length == 0) {
                return -1;
            }
            pos++;
            return (int) (block[0] & 0xff);
        }

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            byte[] block = conn.substr(key.getBytes(), pos, pos + (len - off - 1));
            if (block == null || block.length == 0) {
                return -1;
            }
            System.arraycopy(block, 0, buf, off, block.length);
            pos += block.length;
            return block.length;
        }

        @Override
        public void close() {
            // no-op
        }
    }

    public class ChatMessages {
        public String chatId;
        public List<Map<String, Object>> messages;

        public ChatMessages(String chatId, List<Map<String, Object>> messages) {
            this.chatId = chatId;
            this.messages = messages;
        }

        public boolean equals(Object other) {
            if (!(other instanceof ChatMessages)) {
                return false;
            }
            ChatMessages otherCm = (ChatMessages) other;
            return chatId.equals(otherCm.chatId) &&
                    messages.equals(otherCm.messages);
        }
    }

    public class PollQueueThread extends Thread {
        private Jedis conn;
        private boolean quit;
        private Gson gson = new Gson();

        public PollQueueThread() {
            this.conn = new Jedis(RedisConst.redisHost);
            this.conn.select(RedisConst.redisDbIndex);
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
                Set<Tuple> items = conn.zrangeWithScores("delayed:", 0, 0); // 第一个任务
                Tuple item = items.size() > 0 ? items.iterator().next() : null;
                if (item == null || item.getScore() > System.currentTimeMillis()) {
                    try {
                        sleep(10);
                    } catch (InterruptedException ie) {
                        Thread.interrupted();
                    }
                    continue;
                }

                String json = item.getElement();
                String[] values = gson.fromJson(json, String[].class);
                String identifier = values[0];
                String queue = values[1];

                String locked = acquireLock(conn, identifier);
                if (locked == null) {
                    continue;
                }

                if (conn.zrem("delayed:", json) == 1) {
                    conn.rpush("queue:" + queue, json);
                }

                releaseLock(conn, identifier, locked);
            }
        }
    }


    @Test
    public void testCreateChat(){
        Deque<File> waiting = new ArrayDeque<>();
        long bytesInRedis = 0;

        Set<String> recipients = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            recipients.add(String.valueOf(i));
        }
        createChat(new Jedis(RedisConst.redisHost), "source", recipients, "", "test:");
    }

    public class CopyLogsThread extends Thread {
        private Jedis conn;
        private File path;
        private String channel;
        private int count;
        private long limit;

        public CopyLogsThread(File path, String channel, int count, long limit) {
            this.conn = new Jedis(RedisConst.redisHost);
            this.conn.select(RedisConst.redisDbIndex);
            this.path = path;
            this.channel = channel;
            this.count = count;
            this.limit = limit;
        }

        public void run() {
            Deque<File> waiting = new ArrayDeque<>();
            long bytesInRedis = 0;

            Set<String> recipients = new HashSet<>();
            for (int i = 0; i < count; i++) {
                recipients.add(String.valueOf(i));
            }
            createChat(conn, "source", recipients, "", channel);
            File[] logFiles = path.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("temp_redis");
                }
            });
            Arrays.sort(logFiles);
            for (File logFile : logFiles) {
                long fsize = logFile.length();
                while ((bytesInRedis + fsize) > limit) {
                    long cleaned = clean(waiting, count);
                    if (cleaned != 0) {
                        bytesInRedis -= cleaned;
                    } else {
                        try {
                            sleep(250);
                        } catch (InterruptedException ie) {
                            Thread.interrupted();
                        }
                    }
                }

                BufferedInputStream in = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(logFile));
                    int read = 0;
                    byte[] buffer = new byte[8192];
                    while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                        if (buffer.length != read) {
                            byte[] bytes = new byte[read];
                            System.arraycopy(buffer, 0, bytes, 0, read);
                            conn.append((channel + logFile).getBytes(), bytes);
                        } else {
                            conn.append((channel + logFile).getBytes(), buffer);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    throw new RuntimeException(ioe);
                } finally {
                    try {
                        in.close();
                    } catch (Exception ignore) {
                    }
                }

                sendMessage(conn, channel, "source", logFile.toString());

                bytesInRedis += fsize;
                waiting.addLast(logFile);
            }

            sendMessage(conn, channel, "source", ":done");

            while (waiting.size() > 0) {
                long cleaned = clean(waiting, count);
                if (cleaned != 0) {
                    bytesInRedis -= cleaned;
                } else {
                    try {
                        sleep(250);
                    } catch (InterruptedException ie) {
                        Thread.interrupted();
                    }
                }
            }
        }

        private long clean(Deque<File> waiting, int count) {
            if (waiting.size() == 0) {
                return 0;
            }
            File w0 = waiting.getFirst();
            if (String.valueOf(count).equals(conn.get(channel + w0 + ":done"))) {
                conn.del(channel + w0, channel + w0 + ":done");
                return waiting.removeFirst().length();
            }
            return 0;
        }
    }
}
