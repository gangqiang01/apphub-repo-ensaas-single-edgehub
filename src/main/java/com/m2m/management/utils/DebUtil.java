package com.m2m.management.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @date ：Created in 8/17/20 3:46 PM
 * @description：parse deb pkg
 */
public class DebUtil {

    static public Map<String, String> getDebInfo(String path){
        String cmdString  = String.format("%s %s", "dpkg-deb  -f", path);
        String[] cmdLine = new String[]{
                "/bin/bash",
                "-c",
                cmdString
        };
        System.out.println(cmdString);
        Map<String, String> result = executeShellOneLine(cmdLine);
        return result;
    }

    private static Map<String, String> executeShellOneLine(String[] cmdLine) {
        Map<String, String> debInfo = new HashMap<>();
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmdLine);

            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()), 7777);
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()), 7777);
            String line = null;
            while ((null != (line = stdout.readLine())) || (null != (line = stderr.readLine()))) {
                //retString += line + "\n";
//                retString += line;
                System.out.println(line);
                String[] infoArray = line.split(":", 2);
                if(infoArray[0].trim().equalsIgnoreCase("Version")){
                    debInfo.put("version", infoArray[1].trim());
                }
                if(infoArray[0].trim().equalsIgnoreCase("Package")){
                    debInfo.put("package", infoArray[1].trim());
                }
                if(infoArray[0].trim().equalsIgnoreCase("Architecture")){
                    debInfo.put("architecture", infoArray[1].trim());
                }
            }
            return debInfo;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
