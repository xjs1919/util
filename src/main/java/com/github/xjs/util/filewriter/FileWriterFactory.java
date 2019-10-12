package com.github.xjs.util.filewriter;

import java.io.File;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @Description
 * @Author xujs@mamcharge.com
 * @Date 2019/10/12 14:52
 **/
public class FileWriterFactory {

    private FileWriterFactory() {
    }

    private static ConcurrentHashMap<String, FileWriter> FileWriterCache = new ConcurrentHashMap<String, FileWriter>();

    public static FileWriter getFileWriter(File file, FileWriter.ElementProcessor extractor) {
        String filePath = file.getAbsolutePath();
        FileWriter fw = FileWriterCache.get(filePath);
        if (fw != null) {
            return fw;
        }
        fw = new FileWriter(file, extractor);
        FileWriterCache.putIfAbsent(filePath, fw);
        return FileWriterCache.get(filePath);
    }

    public static void stop() {
        for (Map.Entry<String, FileWriter> entry : FileWriterCache.entrySet()) {
            entry.getValue().stop();
        }
    }

    public static void main(String[] args) throws Exception {
        FileWriter fw1 = FileWriterFactory.getFileWriter(new File("d:\\log1.txt"), null);
        FileWriter fw2 = FileWriterFactory.getFileWriter(new File("d:\\log2.txt"), null);
        Thread[] ts = new Thread[10];
        CountDownLatch latch = new CountDownLatch(ts.length);
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        boolean bol = new Random().nextBoolean();
                        if (bol) {
                            fw1.write("element" + j);
                        } else {
                            fw2.write("element" + j);
                        }
                    }
                    latch.countDown();
                }
            });
        }
        for (int i = 0; i < ts.length; i++) {
            ts[i].start();
        }
        latch.await();
        System.out.println("over");
        FileWriterFactory.stop();
    }
}
