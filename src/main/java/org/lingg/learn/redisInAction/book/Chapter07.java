package org.lingg.learn.redisInAction.book;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.lingg.learn.redisInAction.book.util.ReflectionUtils;
import redis.clients.jedis.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.Query;
import javax.naming.directory.SearchResult;

public class Chapter07 {
    private static final Pattern QUERY_RE = Pattern.compile("[+-]?[a-z0-9']{2,}");
    private static final Pattern WORDS_RE = Pattern.compile("[a-z0-9']{2,}");
    private static final Set<String> STOP_WORDS = new HashSet<>();
    private static String CONTENT =
            "this is some random content, look at how it is indexed.";

    static {
        for (String word :
                ("able about across after all almost also am among " +
                        "an and any are as at be because been but by can " +
                        "cannot could dear did do does either else ever " +
                        "every for from get got had has have he her hers " +
                        "him his how however if in into is it its just " +
                        "least let like likely may me might most must my " +
                        "neither no nor not of off often on only or other " +
                        "our own rather said say says she should since so " +
                        "some than that the their them then there these " +
                        "they this tis to too twas us wants was we were " +
                        "what when where which while who whom why will " +
                        "with would yet you your").split(" ")) {
            STOP_WORDS.add(word);
        }
    }

    private Map<Ecpm, Double> AVERAGE_PER_1K = new HashMap<>();

    public static final void main(String[] args) {
        new Chapter07().run();
    }

    public void run() {
        Jedis conn = new Jedis(RedisConst.redisHost);
        conn.select(RedisConst.redisDbIndex);
        conn.flushDB();

        testIndexDocument(conn);
//        testSetOperations(conn);
//        testParseQuery(conn);
//        testParseAndSearch(conn);
//        testSearchWithSort(conn);
//        testSearchWithZsort(conn);
//        conn.flushDB();
//
//        testStringToScore(conn);
//        testIndexAndTargetAds(conn);
//        testIsQualifiedForJob(conn);
//        testIndexAndFindJobs(conn);
    }

    public void testIndexDocument(Jedis conn) {
        System.out.println("\n----- testIndexDocument -----");

        System.out.println("We're tokenizing some content...");
        Set<String> tokens = tokenize(CONTENT);
        System.out.println("Those tokens are: " + Arrays.toString(tokens.toArray()));
        System.out.println(tokens.size() > 0);

        String docid = "文章A";

        System.out.println("And now we are indexing that content...");
        int count = indexDocument(conn, docid, CONTENT);
        System.out.println(count == tokens.size());
        Set<String> test = new HashSet<>();
        test.add(docid);
        for (String t : tokens) {
            Set<String> members = conn.smembers("idx:" + t);
            System.out.println(test.equals(members));
        }
    }

    public void testSetOperations(Jedis conn) {
        System.out.println("\n----- testSetOperations -----");
        String docid = "文章A";
        indexDocument(conn, "文章A", "aaa aab aac about bothab1 bothab2"); // about because 忽略的单词
        indexDocument(conn, "文章B", "bba bbb bbc because bothab1 bothab2");

        Set<String> test = new HashSet<>();
        test.add("文章A");
        test.add("文章B");

        Transaction trans = conn.multi();
        // 交集sinterstore
        String id = intersect(trans, 30, new String[]{"bothab1", "bothab2"}); // 同时包含"bothab1", "bothab2"的文章
        trans.exec();
        System.out.println("交集 : " + conn.smembers("idx:" + id));
        System.out.println(test.equals(conn.smembers("idx:" + id)));

        trans = conn.multi();
        id = intersect(trans, 30, new String[]{"aab", "bbc"}); //同时包含"aab", "bbc"的文章
        trans.exec();
        System.out.println("交集 : " + conn.smembers("idx:" + id));
        System.out.println(conn.smembers("idx:" + id).isEmpty());


        trans = conn.multi();
        //并集sunionstore
        id = union(trans, 30, new String[]{"aab", "bbc", "because"});//包含aab或者bbc的文章  because属于忽略的单词
        trans.exec();
        System.out.println("并集 : " + conn.smembers("idx:" + id));
        System.out.println(test.equals(conn.smembers("idx:" + id)));


        trans = conn.multi();
        //差集sdiffstore
        id = difference(trans, 30, new String[]{"aac", "bbc"}); //aab 在，但是 bbc 不在 的文章
        trans.exec();
        System.out.println("差集 : " + conn.smembers("idx:" + id));
        System.out.println(test.equals(conn.smembers("idx:" + id)));

        trans = conn.multi();
        id = difference(trans, 30, "aaa", "aab");
        trans.exec();
        System.out.println("差集 : " + conn.smembers("idx:" + id));
        System.out.println(conn.smembers("idx:" + id).isEmpty());
    }

    public void testParseQuery(Jedis conn) {
        System.out.println("\n----- testParseQuery -----");
        String queryString = "test query without stopwords";
        Query query = parse(queryString);
        String[] words = queryString.split(" ");
        for (int i = 0; i < words.length; i++) {
            List<String> word = new ArrayList<>();
            word.add(words[i]);
            System.out.println(word.equals(query.all.get(i)));
        }
        System.out.println(query.unwanted.isEmpty());

        queryString = "test +query without -stopwords";
        query = parse(queryString);
        System.out.println("test".equals(query.all.get(0).get(0)));
        System.out.println("query".equals(query.all.get(0).get(1)));
        System.out.println("without".equals(query.all.get(1).get(0)));
        System.out.println("stopwords".equals(query.unwanted.toArray()[0]));
    }

    public void testParseAndSearch(Jedis conn) {
        System.out.println("\n----- testParseAndSearch -----");
        System.out.println("And now we are testing search...");
        indexDocument(conn, "documentA", CONTENT);

        Set<String> test = new HashSet<>();
        test.add("documentA");

        String id = parseAndSearch(conn, "content", 30);
        System.out.println("content :  " + conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content indexed random", 30);
        System.out.println("content indexed random :  " + conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content +indexed random", 30);
        System.out.println("content +indexed random :  " + conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content indexed +random", 30);
        System.out.println("content indexed +random :  " + conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content indexed -random", 30);
        System.out.println("content indexed -random :  " + conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content indexed +random", 30);
        System.out.println("content indexed +random :  " + conn.smembers("idx:" + id));

        System.out.println("Which passed!");
    }

    public void testSearchWithSort(Jedis conn) {
        System.out.println("\n----- testSearchWithSort -----");
        System.out.println("And now let's test searching with sorting...");

        indexDocument(conn, "documentA", CONTENT);
        indexDocument(conn, "documentB", CONTENT);

        HashMap<String, String> values = new HashMap<>();
        values.put("updated", "12345");
        values.put("id", "10");
        conn.hmset("kb:doc:documentA", values);

        values.put("updated", "54321");
        values.put("id", "1");
        conn.hmset("kb:doc:documentB", values);

        SearchResult result = searchAndSort(conn, "content,look", "-updated");
        System.out.println(result.results.get(0));
        System.out.println(result.results.get(1));

        System.out.println("---------------------");

        result = searchAndSort(conn, "content", "-id");
        System.out.println(result.results.get(0));
        System.out.println(result.results.get(1));

        System.out.println("Which passed!");
    }

    public void testSearchWithZsort(Jedis conn) {
        System.out.println("\n----- testSearchWithZsort -----");
        System.out.println("And now let's test searching with sorting via zset...");

        indexDocument(conn, "文章A", CONTENT);
        indexDocument(conn, "文章B", CONTENT);

        conn.zadd("idx:sort:update", 12345, "文章A");//  文档的更新时间
        conn.zadd("idx:sort:update", 54321, "文章B");
        conn.zadd("idx:sort:votes", 10, "文章A"); // 文档的投票数
        conn.zadd("idx:sort:votes", 1, "文章B");

        System.out.println("---------------------------");

        Map<String, Integer> weights = new HashMap<>();
        weights.put("update", 1);
        weights.put("vote", 0);
        SearchResult result = null;
        // 通过SortedSet集合运算，运算时控制Weight参数来控制使用哪个字段进行排序，升序降序通过zrange zrevrange 来控制
        result = searchAndZsort(conn, "content+look+random,+indexed", false, weights);
        System.out.println(result.results.get(0));
        System.out.println(result.results.get(1));

        System.out.println("---------------------------");

        weights.put("update", 0);
        weights.put("vote", 1);
        result = searchAndZsort(conn, "content", false, weights);
        System.out.println(result.results.get(0));
        System.out.println(result.results.get(1));

        System.out.println("---------------------------");
        System.out.println("Which passed!");
    }

    public void testStringToScore(Jedis conn) {
        System.out.println("\n----- testStringToScore -----");

        String[] words = "these are some words that will be sorted".split(" ");

        List<WordScore> pairs = new ArrayList<>();
        for (String word : words) {
            pairs.add(new WordScore(word, stringToScore(word)));
        }
        List<WordScore> pairs2 = new ArrayList<WordScore>(pairs);
        Collections.sort(pairs);
        Collections.sort(pairs2, new Comparator<WordScore>() {
            public int compare(WordScore o1, WordScore o2) {
                long diff = o1.score - o2.score;
                return diff < 0 ? -1 : diff > 0 ? 1 : 0;
            }
        });
        assert pairs.equals(pairs2);

        Map<Integer, Integer> lower = new HashMap<Integer, Integer>();
        lower.put(-1, -1);
        int start = (int) 'a';
        int end = (int) 'z';
        for (int i = start; i <= end; i++) {
            lower.put(i, i - start);
        }

        words = "these are some words that will be sorted".split(" ");
        pairs = new ArrayList<WordScore>();
        for (String word : words) {
            pairs.add(new WordScore(word, stringToScoreGeneric(word, lower)));
        }
        pairs2 = new ArrayList<WordScore>(pairs);
        Collections.sort(pairs);
        Collections.sort(pairs2, new Comparator<WordScore>() {
            public int compare(WordScore o1, WordScore o2) {
                long diff = o1.score - o2.score;
                return diff < 0 ? -1 : diff > 0 ? 1 : 0;
            }
        });
        assert pairs.equals(pairs2);

        Map<String, String> values = new HashMap<String, String>();
        values.put("test", "value");
        values.put("TestHashMap", "other");
        zaddString(conn, "key", values);
        assert conn.zscore("key", "test") == stringToScore("value");
        assert conn.zscore("key", "TestHashMap") == stringToScore("other");
    }

    public void testIndexAndTargetAds(Jedis conn) {
        System.out.println("\n----- testIndexAndTargetAds -----");
        indexAd(conn, "1", new String[]{"USA", "CA"}, CONTENT, Ecpm.CPC, .25);
        indexAd(conn, "2", new String[]{"USA", "VA"}, CONTENT + " wooooo", Ecpm.CPC, .125);

        String[] usa = new String[]{"USA"};
        for (int i = 0; i < 100; i++) {
            targetAds(conn, usa, CONTENT);
        }
        Pair<Long, String> result = targetAds(conn, usa, CONTENT);
        long targetId = result.getValue0();
        String adId = result.getValue1();
        System.out.println(result.getValue1());


        result = targetAds(conn, new String[]{"VA"}, "wooooo");
        System.out.println(result.getValue1());

        Iterator<Tuple> range = conn.zrangeWithScores("idx:ad:value:", 0, -1).iterator();
        System.out.println(new Tuple("2", 0.125).equals(range.next()));
        System.out.println(new Tuple("1", 0.25).equals(range.next()));

        range = conn.zrangeWithScores("ad:base_value:", 0, -1).iterator();
        System.out.println(new Tuple("2", 0.125).equals(range.next()));
        System.out.println(new Tuple("1", 0.25).equals(range.next()));

        recordClick(conn, targetId, adId, false);

        range = conn.zrangeWithScores("idx:ad:value:", 0, -1).iterator();
        System.out.println(new Tuple("2", 0.125).equals(range.next()));
        System.out.println(new Tuple("1", 2.5).equals(range.next()));

        range = conn.zrangeWithScores("ad:base_value:", 0, -1).iterator();
        System.out.println(new Tuple("2", 0.125).equals(range.next()));
        System.out.println(new Tuple("1", 0.25).equals(range.next()));
    }

    public void testIsQualifiedForJob(Jedis conn) {
        System.out.println("\n----- testIsQualifiedForJob -----");
        addJob(conn, "test", "q1", "q2", "q3");
        System.out.println(isQualified(conn, "test", "q1", "q3", "q2"));
        System.out.println(isQualified(conn, "test", "q1", "q2"));
    }

    public void testIndexAndFindJobs(Jedis conn) {
        System.out.println("\n----- testIndexAndFindJobs -----");
        indexJob(conn, "TestIdentityHashMap", "q1", "q2", "q3");
        indexJob(conn, "TestHashMap", "q1", "q3", "q4");
        indexJob(conn, "test3", "q1", "q3", "q5");

        System.out.println(findJobs(conn, "q1"));

        System.out.println(findJobs(conn, "q1", "q3", "q4"));

        System.out.println(findJobs(conn, "q1", "q3", "q5"));

        System.out.println(findJobs(conn, "q1", "q2", "q3", "q4", "q5"));
    }

    public Set<String> tokenize(String content) {
        Set<String> words = new HashSet<>();
        Matcher matcher = WORDS_RE.matcher(content);
        while (matcher.find()) {
            String word = matcher.group().trim();
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                words.add(word);
            }
        }
        return words;
    }

    public int indexDocument(Jedis conn, String docid, String content) {
        Set<String> words = tokenize(content);
        System.out.println("content 处理后 ： " + words);
        Transaction trans = conn.multi();
        for (String word : words) {
            trans.sadd("idx:" + word, docid);
            System.out.println("idx:" + word + " = " + docid);
        }
        return trans.exec().size();
    }

    private String setCommon(Transaction trans, String method, int ttl, String... items) {
        String[] keys = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            keys[i] = "idx:" + items[i];
        }

        String tempid = UUID.randomUUID().toString();
        try {
            // 将给定多个单词对应的集合进行交集计算，将计算结果存储到一个临时集合中,key 为  "idx:" + tempid   key对应的值为文章列表
            ReflectionUtils.invokeMethod(trans, method,
                    new Class[]{String.class, String[].class}, new Object[]{"idx:" + tempid, keys});

//            trans.getClass()
//                    .getDeclaredMethod(method, new Class[]{String.class, String[].class})
//                    .invoke(trans, "idx:" + id, keys); // sinterstore  命令 将keys所有集合元素保存到 idx：id中

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trans.expire("idx:" + tempid, ttl);
        return tempid;
    }

    // 查询交集
    public String intersect(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sinterstore", ttl, items);
    }

    // 并集
    public String union(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sunionstore", ttl, items);
    }

    // 差集
    public String difference(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sdiffstore", ttl, items);
    }

    private String zsetCommon(
            Transaction trans, String method, int ttl, ZParams params, String... sets) {
        String[] keys = new String[sets.length];
        for (int i = 0; i < sets.length; i++) {
            keys[i] = "idx:" + sets[i];
        }

        String id = UUID.randomUUID().toString();
        try {

            ReflectionUtils.invokeMethod(trans, method, new Class[]{String.class, ZParams.class, String[].class}, new Object[]{"idx:" + id, params, keys});

//            trans.getClass()
//                    .getDeclaredMethod(method, String.class, ZParams.class, String[].class)
//                    .invoke(trans, "idx:" + id, params, keys);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trans.expire("idx:" + id, ttl);
        return id;
    }

    public String zintersect(
            Transaction trans, int ttl, ZParams params, String... sets) {
        return zsetCommon(trans, "zinterstore", ttl, params, sets);
    }

    public String zunion(
            Transaction trans, int ttl, ZParams params, String... sets) {
        return zsetCommon(trans, "zunionstore", ttl, params, sets);
    }

    public Query parse(String queryString) {
        Query query = new Query();
        Set<String> current = new HashSet<>();
        Matcher matcher = QUERY_RE.matcher(queryString.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group().trim();
            System.err.println("word : " + word);
            char prefix = word.charAt(0);
            if (prefix == '+' || prefix == '-') {
                word = word.substring(1);
            }

            if (word.length() < 2 || STOP_WORDS.contains(word)) {
                continue;
            }

            if (prefix == '-') {
                query.unwanted.add(word);
                continue;
            }

            if (!current.isEmpty() && prefix != '+') {
                query.all.add(new ArrayList<String>(current));
                current.clear();
            }
            current.add(word);
        }

        if (!current.isEmpty()) {
            query.all.add(new ArrayList<String>(current));
        }
        return query;
    }

    public String parseAndSearch(Jedis conn, String queryString, int ttl) {
        Query query = parse(queryString);
        if (query.all.isEmpty()) {
            return null;
        }

        List<String> toIntersect = new ArrayList<>();
        // 多个条件
        for (List<String> syn : query.all) {
            if (syn.size() > 1) {// 同义词多个的话进行并集计算
                Transaction trans = conn.multi();
                // key为 idx：tempId  set的元素是进行集合运算后的结果
                toIntersect.add(union(trans, ttl, syn.toArray(new String[syn.size()])));
                trans.exec();
            } else {
                toIntersect.add(syn.get(0)); // 只有一个单词，直接使用此单词
            }
        }

        //
        String intersectResult = null;
        if (toIntersect.size() > 1) {// 查询的结果（文章列表）不止一个，进行交集计算
            Transaction trans = conn.multi();
            intersectResult = intersect(trans, ttl, toIntersect.toArray(new String[toIntersect.size()]));
            trans.exec();
        } else {// 只有一个，直接使用
            intersectResult = toIntersect.get(0);
        }

        if (!query.unwanted.isEmpty()) {
            String[] keys = query.unwanted.toArray(new String[query.unwanted.size() + 1]);
            keys[keys.length - 1] = intersectResult;
            Transaction trans = conn.multi();
            intersectResult = difference(trans, ttl, keys); // 从交集结果里移除 不需要的结果 然后返回搜索结果
            trans.exec();
        }

        return intersectResult;
    }

    @SuppressWarnings("unchecked")
    public SearchResult searchAndSort(Jedis conn, String queryString, String sort) {
        boolean desc = sort.startsWith("-");
        if (desc) {
            sort = sort.substring(1);
        }
        boolean alpha = !"updated".equals(sort) && !"id".equals(sort); // 是否使用字母排序
        // http://redisdoc.com/key/sort.html#get-by   使用外部键来排序
        String by = "kb:doc:*->" + sort;

        String id = parseAndSearch(conn, queryString, 300);

        Transaction trans = conn.multi();
        trans.scard("idx:" + id);
        SortingParams params = new SortingParams();
        if (desc) {
            params.desc();
        }
        if (alpha) {
            params.alpha();
        }
        params.by(by);
        params.limit(0, 20);
        trans.sort("idx:" + id, params);
        List<Object> results = trans.exec();

        return new SearchResult(
                id,
                ((Long) results.get(0)).longValue(),
                (List<String>) results.get(1));
    }

    @SuppressWarnings("unchecked")
    public SearchResult searchAndZsort(
            Jedis conn, String queryString, boolean desc, Map<String, Integer> weights) {
        int ttl = 300;
        int start = 0;
        int num = 20;
        String id = parseAndSearch(conn, queryString, ttl);

        int updateWeight = weights.containsKey("update") ? weights.get("update") : 1;
        int voteWeight = weights.containsKey("vote") ? weights.get("vote") : 0;

        String[] keys = new String[]{id, "sort:update", "sort:votes"};
        Transaction trans = conn.multi();
        id = zintersect(trans, ttl, new ZParams().weightsByDouble(0, updateWeight, voteWeight), keys);

        trans.zcard("idx:" + id);
        if (desc) {
            trans.zrevrange("idx:" + id, start, start + num - 1);
        } else {
            trans.zrange("idx:" + id, start, start + num - 1);
        }
        List<Object> results = trans.exec();

        return new SearchResult(
                id,
                ((Long) results.get(results.size() - 2)).longValue(),
                // Note: it's a LinkedHashSet, so it's ordered
                new ArrayList<String>((Set<String>) results.get(results.size() - 1)));
    }

    public long stringToScore(String string) {
        return stringToScore(string, false);
    }

    public long stringToScore(String string, boolean ignoreCase) {
        if (ignoreCase) {
            string = string.toLowerCase();
        }

        List<Integer> pieces = new ArrayList<>();
        for (int i = 0; i < Math.min(string.length(), 6); i++) {
            pieces.add((int) string.charAt(i));
        }
        while (pieces.size() < 6) {
            pieces.add(-1);
        }

        long score = 0;
        for (int piece : pieces) {
            score = score * 257 + piece + 1;
        }

        return score << 1 + (string.length() > 6 ? 1 : 0); // 分数*2 ，然后使用最后一位来表示字符串长度是否超过6
    }


    public long stringToScoreGeneric(String string, Map<Integer, Integer> mapping) {
        int length = (int) (52 / (Math.log(mapping.size()) / Math.log(2)));

        List<Integer> pieces = new ArrayList<Integer>();
        for (int i = 0; i < Math.min(string.length(), length); i++) {
            pieces.add((int) string.charAt(i));
        }
        while (pieces.size() < 6) {
            pieces.add(-1);
        }

        long score = 0;
        for (int piece : pieces) {
            int value = mapping.get(piece);
            score = score * mapping.size() + value + 1;
        }

        return score * 2 + (string.length() > 6 ? 1 : 0);
    }

    public long zaddString(Jedis conn, String name, Map<String, String> values) {
//        Map<Double,String> pieces = new HashMap<>(values.size());
//        for (Map.Entry<String,String> entry : values.entrySet()) {
//            pieces.put((double)stringToScore(entry.getValue()), entry.getKey());
//        }
//
//        return conn.zadd(name, pieces);

        Map<String, Double> prieces = new HashMap<>(values.size());
        for (Map.Entry<String, String> entry : values.entrySet()) {
            prieces.put(entry.getKey(), (double) stringToScore(entry.getValue()));
        }
        return conn.zadd(name, prieces);
    }

    public void indexAd(Jedis conn, String id, String[] locations, String content, Ecpm type, double value) {
        Transaction trans = conn.multi();

        for (String location : locations) {
            trans.sadd("idx:req:" + location, id);
        }

        Set<String> words = tokenize(content);
        for (String word : tokenize(content)) {
            trans.zadd("idx:" + word, 0, id);
        }

        double avg = AVERAGE_PER_1K.containsKey(type) ? AVERAGE_PER_1K.get(type) : 1;
        double rvalue = toEcpm(type, 1000, avg, value);

        trans.hset("type:", id, type.name().toLowerCase());
        trans.zadd("idx:ad:value:", rvalue, id);
        trans.zadd("ad:base_value:", value, id);
        for (String word : words) {
            trans.sadd("terms:" + id, word);
        }
        trans.exec();
    }

    public double toEcpm(Ecpm type, double views, double avg, double value) {
        switch (type) {
            case CPC:
            case CPA:
                return 1000. * value * avg / views;
            case CPM:
                return value;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public Pair<Long, String> targetAds(
            Jedis conn, String[] locations, String content) {
        Transaction trans = conn.multi();

        String matchedAds = matchLocation(trans, locations);

        String baseEcpm = zintersect(
                trans, 30, new ZParams().weightsByDouble(0, 1), matchedAds, "ad:value:");

        Pair<Set<String>, String> result = finishScoring(trans, matchedAds, baseEcpm, content);

        trans.incr("ads:served:");
        trans.zrevrange("idx:" + result.getValue1(), 0, 0);

        List<Object> response = trans.exec();
        long targetId = (Long) response.get(response.size() - 2);
        Set<String> targetedAds = (Set<String>) response.get(response.size() - 1);

        if (targetedAds.size() == 0) {
            return new Pair<Long, String>(null, null);
        }

        String adId = targetedAds.iterator().next();
        recordTargetingResult(conn, targetId, adId, result.getValue0());

        return new Pair<Long, String>(targetId, adId);
    }

    public String matchLocation(Transaction trans, String[] locations) {
        String[] required = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            required[i] = "req:" + locations[i];
        }
        return union(trans, 300, required);
    }

    public Pair<Set<String>, String> finishScoring(
            Transaction trans, String matched, String base, String content) {
        Map<String, Integer> bonusEcpm = new HashMap<>();
        Set<String> words = tokenize(content);
        for (String word : words) {
            String wordBonus = zintersect(trans, 30, new ZParams().weightsByDouble(0, 1), matched, word);
            bonusEcpm.put(wordBonus, 1);
        }

        if (bonusEcpm.size() > 0) {

            String[] keys = new String[bonusEcpm.size()];
            double[] weights = new double[bonusEcpm.size()];
            int index = 0;
            for (Map.Entry<String, Integer> bonus : bonusEcpm.entrySet()) {
                keys[index] = bonus.getKey();
                weights[index] = bonus.getValue();
                index++;
            }

            ZParams minParams = new ZParams().aggregate(ZParams.Aggregate.MIN).weightsByDouble(weights);
            String minimum = zunion(trans, 30, minParams, keys);

            ZParams maxParams = new ZParams().aggregate(ZParams.Aggregate.MAX).weightsByDouble(weights);
            String maximum = zunion(trans, 30, maxParams, keys);

            String result = zunion(
                    trans, 30, new ZParams().weightsByDouble(2, 1, 1), base, minimum, maximum);
            return new Pair<Set<String>, String>(words, result);
        }
        return new Pair<Set<String>, String>(words, base);
    }

    public void recordTargetingResult(
            Jedis conn, long targetId, String adId, Set<String> words) {
        Set<String> terms = conn.smembers("terms:" + adId);
        String type = conn.hget("type:", adId);

        Transaction trans = conn.multi();
        terms.addAll(words);
        if (terms.size() > 0) {
            String matchedKey = "terms:matched:" + targetId;
            for (String term : terms) {
                trans.sadd(matchedKey, term);
            }
            trans.expire(matchedKey, 900);
        }

        trans.incr("type:" + type + ":views:");
        for (String term : terms) {
            trans.zincrby("views:" + adId, 1, term);
        }
        trans.zincrby("views:" + adId, 1, "");

        List<Object> response = trans.exec();
        double views = (Double) response.get(response.size() - 1);
        if ((views % 100) == 0) {
            updateCpms(conn, adId);
        }
    }

    @SuppressWarnings("unchecked")
    public void updateCpms(Jedis conn, String adId) {
        Transaction trans = conn.multi();
        trans.hget("type:", adId);
        trans.zscore("ad:base_value:", adId);
        trans.smembers("terms:" + adId);
        List<Object> response = trans.exec();
        String type = (String) response.get(0);
        Double baseValue = (Double) response.get(1);
        Set<String> words = (Set<String>) response.get(2);

        String which = "clicks";
        Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());
        if (Ecpm.CPA.equals(ecpm)) {
            which = "actions";
        }

        trans = conn.multi();
        trans.get("type:" + type + ":views:");
        trans.get("type:" + type + ':' + which);
        response = trans.exec();
        String typeViews = (String) response.get(0);
        String typeClicks = (String) response.get(1);

        AVERAGE_PER_1K.put(ecpm,
                1000. *
                        Integer.valueOf(typeClicks != null ? typeClicks : "1") /
                        Integer.valueOf(typeViews != null ? typeViews : "1"));

        if (Ecpm.CPM.equals(ecpm)) {
            return;
        }

        String viewKey = "views:" + adId;
        String clickKey = which + ':' + adId;

        trans = conn.multi();
        trans.zscore(viewKey, "");
        trans.zscore(clickKey, "");
        response = trans.exec();
        Double adViews = (Double) response.get(0);
        Double adClicks = (Double) response.get(1);

        double adEcpm = 0;
        if (adClicks == null || adClicks < 1) {
            Double score = conn.zscore("idx:ad:value:", adId);
            adEcpm = score != null ? score.doubleValue() : 0;
        } else {
            adEcpm = toEcpm(
                    ecpm,
                    adViews != null ? adViews.doubleValue() : 1,
                    adClicks != null ? adClicks.doubleValue() : 0,
                    baseValue);
            conn.zadd("idx:ad:value:", adEcpm, adId);
        }
        for (String word : words) {
            trans = conn.multi();
            trans.zscore(viewKey, word);
            trans.zscore(clickKey, word);
            response = trans.exec();
            Double views = (Double) response.get(0);
            Double clicks = (Double) response.get(1);

            if (clicks == null || clicks < 1) {
                continue;
            }

            double wordEcpm = toEcpm(
                    ecpm,
                    views != null ? views.doubleValue() : 1,
                    clicks != null ? clicks.doubleValue() : 0,
                    baseValue);
            double bonus = wordEcpm - adEcpm;
            conn.zadd("idx:" + word, bonus, adId);
        }
    }

    public void recordClick(Jedis conn, long targetId, String adId, boolean action) {
        String type = conn.hget("type:", adId);
        Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());

        String clickKey = "clicks:" + adId;
        String matchKey = "terms:matched:" + targetId;
        Set<String> matched = conn.smembers(matchKey);
        matched.add("");

        Transaction trans = conn.multi();
        if (Ecpm.CPA.equals(ecpm)) {
            trans.expire(matchKey, 900);
            if (action) {
                clickKey = "actions:" + adId;
            }
        }

        if (action && Ecpm.CPA.equals(ecpm)) {
            trans.incr("type:" + type + ":actions:");
        } else {
            trans.incr("type:" + type + ":clicks:");
        }

        for (String word : matched) {
            trans.zincrby(clickKey, 1, word);
        }
        trans.exec();

        updateCpms(conn, adId);
    }

    public void addJob(Jedis conn, String jobId, String... requiredSkills) {
        conn.sadd("job:" + jobId, requiredSkills);
    }

    @SuppressWarnings("unchecked")
    public boolean isQualified(Jedis conn, String jobId, String... candidateSkills) {
        String temp = UUID.randomUUID().toString();
        Transaction trans = conn.multi();
        for (String skill : candidateSkills) {
            trans.sadd(temp, skill);
        }
        trans.expire(temp, 5);

        // 返回一个集合的全部成员，该集合是所有给定集合之间的差集
        trans.sdiff("job:" + jobId, temp); // "job:" + jobId 集合中有，但是temp集合中没有   即为缺少的技能

        List<Object> response = trans.exec();
        Set<String> diff = (Set<String>) response.get(response.size() - 1);
        return diff.size() == 0;
    }

    public void indexJob(Jedis conn, String jobId, String... skills) {
        Transaction trans = conn.multi();
        Set<String> unique = new HashSet<String>();
        for (String skill : skills) {
            trans.sadd("idx:skill:" + skill, jobId);
            unique.add(skill);
        }
        trans.zadd("idx:jobs:req", unique.size(), jobId);
        trans.exec();
    }

    public Set<String> findJobs(Jedis conn, String... candidateSkills) {
        String[] keys = new String[candidateSkills.length];
        double[] weights = new double[candidateSkills.length];
        for (int i = 0; i < candidateSkills.length; i++) {
            keys[i] = "skill:" + candidateSkills[i];
            weights[i] = 1.0;
        }

        Transaction trans = conn.multi();
        String jobScores = zunion(trans, 30, new ZParams().weightsByDouble(weights), keys);
        String finalResult = zintersect(trans, 30, new ZParams().weightsByDouble(-1, 1), jobScores, "jobs:req");
        trans.exec();

        return conn.zrangeByScore("idx:" + finalResult, 0, 0);
    }

    public enum Ecpm {
        CPC, CPA, CPM
    }

    public class Query {
        public final List<List<String>> all = new ArrayList<>();
        public final Set<String> unwanted = new HashSet<>();
    }

    public class SearchResult {
        public final String id;
        public final long total;
        public final List<String> results;

        public SearchResult(String id, long total, List<String> results) {
            this.id = id;
            this.total = total;
            this.results = results;
        }
    }

    public class WordScore
            implements Comparable<WordScore> {
        public final String word;
        public final long score;

        public WordScore(String word, long score) {
            this.word = word;
            this.score = score;
        }

        public boolean equals(Object other) {
            if (!(other instanceof WordScore)) {
                return false;
            }
            WordScore t2 = (WordScore) other;
            return this.word.equals(t2.word) && this.score == t2.score;
        }

        @Override
        public int compareTo(WordScore other) {
            if (this.word.equals(other.word)) {
                long diff = this.score - other.score;
                return diff < 0 ? -1 : diff > 0 ? 1 : 0;
            }
            return this.word.compareTo(other.word);
        }

        public String toString() {
            return word + '=' + score;
        }
    }
}
