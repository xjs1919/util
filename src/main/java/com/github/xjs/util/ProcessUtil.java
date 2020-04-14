package com.github.xjs.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ProcessUtil {

    public static String execute(String command){
        if(OSUtil.isUnixLikeSystem()){
            return ProcessUtil.executeCommand("/bin/bash", "-c", command);
        }else{
            return ProcessUtil.executeCommand("cmd", "/c", command);
        }
    }

    private static String executeCommand(String ... command){
        ProcessBuilder pb = new ProcessBuilder();
        // 重定向错误输出流到正常输出流
        pb.redirectErrorStream(true);
        StringBuilder result = new StringBuilder();
        try {
            //执行命令
            pb.command(command);
            // 启动进程
            Process process = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line1 = null;
            while ((line1 = br.readLine()) != null) {
                result.append(line1);
            }
            //等待执行结果，0代表正常
            int ret = process.waitFor();
            LogUtil.debug(null, ()->"command:"+Arrays.toString(command)+", result:"+ret );
            if(ret != 0){
                throw new RuntimeException("command:"+Arrays.toString(command)+", result:"+ret);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("command:"+Arrays.toString(command)+",执行异常");
        }
    }
}
