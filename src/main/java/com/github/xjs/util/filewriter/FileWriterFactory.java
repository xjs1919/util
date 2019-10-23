package com.github.xjs.util.filewriter;

import java.io.File;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @Author xujs@mamcharge.com
 * @Date 2019/10/12 14:52
 **/
public class FileWriterFactory {

    private FileWriterFactory() {
    }

    private static ConcurrentHashMap<String, FileWriter> FileWriterCache = new ConcurrentHashMap<String, FileWriter>();

    public static FileWriter initFileWriter(File file){
        return initFileWriter(file, false, null);
    }

    public static FileWriter initFileWriter(File file, boolean append){
        return initFileWriter(file, append, null);
    }

    public static FileWriter initFileWriter(File file, FileWriter.ElementProcessor extractor){
        return initFileWriter(file, false, extractor);
    }

    public static FileWriter initFileWriter(File file, boolean append, FileWriter.ElementProcessor extractor) {
        if(file == null){
            return null;
        }
        String filePath = file.getAbsolutePath();
        FileWriter fw = FileWriterCache.get(filePath);
        if (fw != null) {
            return fw;
        }
        fw = new FileWriter(file, append, extractor);
        FileWriterCache.putIfAbsent(filePath, fw);
        fw = FileWriterCache.get(filePath);
        fw.start();
        return fw;
    }

    public static FileWriter getFileWriter(File file){
        if(file == null){
            return null;
        }
        return FileWriterCache.get(file.getAbsolutePath());
    }

    public static void stop(File file){
        if(file == null){
            return;
        }
        FileWriter fileWriter = getFileWriter(file);
        stop(fileWriter);
    }

    public static void stop(FileWriter fileWriter){
        if(fileWriter == null){
            return;
        }
        fileWriter.stop();
    }

    public static void stop() {
        for (Map.Entry<String, FileWriter> entry : FileWriterCache.entrySet()) {
            entry.getValue().stop();
        }
    }

    public static void main(String[] args) throws Exception {
        FileWriter fw1 = FileWriterFactory.initFileWriter(new File("d:\\log1.txt"));
        FileWriter fw2 = FileWriterFactory.initFileWriter(new File("d:\\log2.txt"));
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
