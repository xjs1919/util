package com.github.xjs.util.filewriter;

import com.github.xjs.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用：<br/>
 * (1) FileWriter fr = new FileWriter(...)<br/>
 * (2) fr.start();<br/>
 * (3) fr.stop();
 * <p>
 * @Author xujs@mamcharge.com
 * @Date 2019/10/12 14:52
 **/
public class FileWriter {

    private static Logger LOG = LoggerFactory.getLogger(FileWriter.class);
    private AtomicBoolean start = new AtomicBoolean(false);
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    private List<String> buffer = new ArrayList<String>();
    private File file;
    private ElementProcessor processor;
    private Thread writeThread;
    private boolean append;

    public interface ElementProcessor {
        String process(String element);
    }

    public FileWriter(File file, boolean append, ElementProcessor processor){
        if(file == null){
            return;
        }
        this.file = file;
        this.processor = processor;
        this.append = append;
    }

    public void start(){
        if(this.file == null){
            return;
        }
        if(!start.compareAndSet(false, true)){
            return;
        }
        if(!append){
            if(file.exists()){
                file.delete();
            }
            try{
                file.createNewFile();
            }catch(Exception e){
                LOG.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(start.get()){
                    try{
                        String element = queue.take();
                        processOneElement(element);
                    }catch(InterruptedException e){
                        break;
                    } catch(Exception e){
                        LOG.error(e.getMessage(), e);
                    }
                }
                //处理Queue中剩余的
                List<String> remaining = new ArrayList<String>();
                queue.drainTo(remaining);
                if(remaining.size() > 0){
                    for(String element : remaining){
                        processOneElement(element);
                    }
                }
                //处理buffer中剩余的
                appendBufferToFile();
            }
        });
        writeThread.start();
    }

    public void write( String element) {
        if(!start.get()){
            return;
        }
        queue.offer(element);
    }

    public void write( List<String> elements) {
        if(!start.get()){
            return;
        }
        for(String oldKey : elements){
            queue.offer(oldKey);
        }
    }

    public void stop(){
        if(!start.get()){
            return;
        }
        start.set(false);
        writeThread.interrupt();
    }

    public File getFile(){
        return file;
    }

    public boolean appendAble(){
        return this.append;
    }

    private void appendToFile(String line) {
        buffer.add(line);
        if(buffer.size() >= 1000){
            appendBufferToFile();
        }
    }

    private void processOneElement(String element){
        if(processor == null){
            appendToFile(element);
        }else{
            String value = processor.process(element);
            if(value != null && value.length() > 0){
                appendToFile(value);
            }
        }
    }

    private void appendBufferToFile() {
        RandomAccessFile raf = null;
        try{
            raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            StringBuilder sb = new StringBuilder();
            for(String key : buffer){
                sb.append(key).append("\r\n");
            }
            byte[] bytes = sb.toString().getBytes("UTF-8");
            raf.write(bytes, 0, bytes.length);
            buffer.clear();
        }catch(Exception e){
            LOG.error("写文件出错", e);
        }finally {
            IOUtil.closeQuietly(raf);
        }
    }

    public static void main(String[] args) throws Exception{
        FileWriter fw = new FileWriter(new File("d:\\log.txt"), true, null);
        fw.write("hello");
        fw.stop();
    }

}
