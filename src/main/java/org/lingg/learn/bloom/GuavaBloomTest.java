package org.lingg.learn.bloom;

import org.apache.commons.io.Charsets;
import org.apache.curator.shaded.com.google.common.hash.BloomFilter;
import org.apache.curator.shaded.com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;

import java.util.*;

public class GuavaBloomTest {
    final static int insertions = 1000000;

    @Test
    public void test() {
        //fpp 允许的错误率
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), insertions, 0.001);

        Set<String> sets = new HashSet<>(insertions);
        List<String> lists = new ArrayList<>(insertions);

        for (int i = 0; i < insertions; i++) {
            String uuid = UUID.randomUUID().toString();
            bloomFilter.put(uuid);
            sets.add(uuid);
            lists.add(uuid);
        }

        int wrong = 0;
        int right = 0;

        for (int i = 0; i < 10000; i++) {
            String test = i % 100 == 0 ? lists.get(i / 100) : UUID.randomUUID().toString(); // 取10000个数，其中100个是lists里面的，其他的随机生成
            if (bloomFilter.mightContain(test)) {
                if (sets.contains(test)) {
                    right++;
                } else {
                    wrong++;
                }
            }
        }

        System.out.println("====================right: " + right); // 包含的100个肯定都是正确的
        System.out.println("====================wrong: " + wrong); // 随机生成的9900个数，有可能误判，误判概率在bloomFilter创建的时候指定，默认0.03
    }
}
