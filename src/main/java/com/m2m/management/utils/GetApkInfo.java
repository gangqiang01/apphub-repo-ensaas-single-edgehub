package com.m2m.management.utils;

import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.dom4j.Namespace;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GetApkInfo {
    private static final Namespace NS = Namespace.get("http://schemas.android.com/apk/res/android");

    public static Map<String, Object> readApk(File apkUrl) {
        Map<String, Object> resMap = new HashMap<>();
        try {
            ApkFile apkFile = new ApkFile(apkUrl);
            ApkMeta apkMeta = apkFile.getApkMeta();
            if(apkFile != null){
                byte[] iconDatas = apkFile.getIconFile().getData();
                resMap.put("filename", apkMeta.getName());
                resMap.put("pkgname", apkMeta.getPackageName());
                resMap.put("versioncode", apkMeta.getVersionCode());
                resMap.put("versionname", apkMeta.getVersionName());
                resMap.put("icon", iconDatas);
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return resMap;
    }
}
