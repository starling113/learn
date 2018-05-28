package org.lingg.learn.redisInAction.mytest;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

public class TestFile {

    @Test
    public void testFile() throws Exception{
        File f1 = File.createTempFile("temp_redis_1_", ".txt");
        f1.deleteOnExit();
        Writer writer = new FileWriter(f1);
        writer.write("one line\n");
        writer.close();

        File f2 = File.createTempFile("temp_redis_2_", ".txt");
     //   f2.deleteOnExit();
        writer = new FileWriter(f2);
        for (int i = 0; i < 100; i++) {
            writer.write("many lines " + i + '\n');
        }
        writer.close();

        File f3 = File.createTempFile("temp_redis_3_", ".txt.gz");
     //   f3.deleteOnExit();
        writer = new OutputStreamWriter(
                new GZIPOutputStream(
                        new FileOutputStream(f3)));
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            writer.write("random line " + Long.toHexString(random.nextLong()) + '\n');
        }
        writer.close();
    }
}
