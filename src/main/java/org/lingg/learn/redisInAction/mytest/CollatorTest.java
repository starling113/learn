package org.lingg.learn.redisInAction.mytest;

import com.google.gson.Gson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileReader;
import java.text.Collator;
import java.util.*;

public class CollatorTest {

    //https://blog.csdn.net/u013249965/article/details/52507343

//  Collator 类执行区分语言环境的 String 比较。使用此类可为自然语言文本构建搜索和排序例程。
//  Collator 是一个抽象基类。其子类实现具体的整理策略。
//  Java 平台目前提供了 RuleBasedCollator 子类，它适用于很多种语言。还可以创建其他子类，以处理更多的专门需要。
//  与其他区分语言环境的类一样，可以使用静态工厂方法 getInstance 来为给定的语言环境获得适当的 Collator 对象。
//  如果需要理解特定整理策略的细节或者需要修改策略，只需查看 Collator 的子类即可。
    @Test
    public void test1(){

        List<String> list = new ArrayList<String>();
        list.add("李方");
        list.add("郭云2");
        list.add("郭云3");
        list.add("赵飞");
        list.add("马路");
        list.add("xxx");
        list.add("xxy");
        list.add("432");

        //实例化 Collator，并获取当前语言环境，通过重写比较器 Comparator来调用 Collections.sort() 方法
        Comparator<String> comparator = new Comparator<String>() {

            public int compare(String o1, String o2) {
                Collator collator = Collator.getInstance();
                return collator.getCollationKey(o1).compareTo(
                        collator.getCollationKey(o2));
            }
        };
        Collections.sort(list, comparator);
        // 数字排在最前，英文字母其次，汉字则按照拼音进行排序
        System.out.println(list);
    }


    @Test
    public void test2(){

            Gson gson = new Gson();
            FileReader reader = null;
            try{
                reader = new FileReader(new File("c://a.csv"));
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);

                Iterator<CSVRecord> iterator = parser.iterator();
                while(iterator.hasNext()){
                    CSVRecord line = iterator.next();
                    System.out.println(line.get(0)+" "+line.get(1)+" "+line.get(2)+" "+line.get(3));
                    System.out.println(line);
                }

//                while ((line = parser.getLine()) != null){
//                    if (line.length < 4 || !Character.isDigit(line[0].charAt(0))){
//                        continue;
//                    }
//                    String cityId = line[0];
//                    String country = line[1];
//                    String region = line[2];
//                    String city = line[3];
//                    String json = gson.toJson(new String[]{city, region, country});
//                    conn.hset("cityid2city:", cityId, json);
//                }
            }catch(Exception e){
                throw new RuntimeException(e);
            }finally{
                try{
                    reader.close();
                }catch(Exception e){
                    // ignore
                }
            }

    }
}

