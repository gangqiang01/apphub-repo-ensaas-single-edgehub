package com.m2m.management.utils;

import java.io.File;
import java.io.FileInputStream;

public class FileToByte {

    public static byte[] fileToByte(File file){
        byte[] fileBytes = null;
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);
            fileBytes = new byte[(int)file.length()];
            fis.read(fileBytes);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

}
