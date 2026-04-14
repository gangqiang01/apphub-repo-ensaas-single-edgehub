package com.m2m.management.utils;

import com.amazonaws.services.s3.model.PartETag;
import java.io.File;

import com.m2m.management.configuration.UploadConfig;
import com.microsoft.azure.storage.blob.BlockEntry;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.m2m.management.utils.FileUtil.generateFileName;

/**
 * 分块上传工具类
 */
public class UploadUtils {
    /**
     * 内部类记录分块上传文件信息
     */
    private static class Value {
        String name;
        boolean[] status;

        Value(int n) {
            this.name = generateFileName();
            this.status = new boolean[n];
        }
    }

    private static Map<String, Value> chunkMap = new HashMap<>();

    public static ConcurrentHashMap<String, PartETag[]> partETags= new  ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, BlockEntry[]> blockList = new  ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> uploadIds= new  ConcurrentHashMap<>();

    public static boolean isUploaded(String key) {
        if (isExist(key)) {
            for (boolean b : chunkMap.get(key).status) {
                if (!b) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 判断文件是否有分块已上传
     * @param key
     * @return
     */
    private static boolean isExist(String key) {
        return chunkMap.containsKey(key);
    }

    /**
     * 为文件添加上传分块记录
     * @param key
     * @param chunk
     */
    public static void addChunk(String key, int chunk) {
        chunkMap.get(key).status[chunk] = true;
    }

    /**
     * 从map中删除键为key的键值对
     * @param key
     */
    public static void removeKey(String key) {
        if (isExist(key)) {
            chunkMap.remove(key);
        }
    }

    /**
     * 获取随机生成的文件名
     * @param key
     * @param chunks
     * @return
     */
    public static String getFileName(String key, int chunks) {
        if (!isExist(key)) {
            synchronized (UploadUtils.class) {
                if (!isExist(key)) {
                    chunkMap.put(key, new Value(chunks));
                }
            }
        }
        return chunkMap.get(key).name;
    }
//upload file to blob
    public static boolean uploadWithBlock(S3Client s3Client, MultipartFile mfile, String key, Integer chunk, Integer chunks, String md5) {
        try {
            if(!partETags.containsKey(md5))
                partETags.put(md5, new PartETag[chunks]);

            if(!blockList.containsKey(md5))
                blockList.put(md5, new BlockEntry[chunks]);

            System.out.println("filename:"+mfile.getOriginalFilename()+"##partEtags length:"+partETags.get(md5).length);
            getFileName(md5,  chunks);
            if (s3Client.uploadFileMulPartByChunk(mfile,  key, chunk, md5, partETags.get(md5),blockList.get(md5))){
                addChunk(md5, chunk);
                return true;
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//    save file to local
    public static boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file) {
        try {
            FileUtil.writeWithBlok(UploadConfig.path + "/" + md5 + "/" + name, size, file.getInputStream(), file.getSize(), chunks, chunk);
            return true;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
