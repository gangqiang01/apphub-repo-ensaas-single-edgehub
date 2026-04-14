package com.m2m.management.utils;

import com.m2m.management.pecoff4j.PE;
import com.m2m.management.pecoff4j.ResourceDirectory;
import com.m2m.management.pecoff4j.ResourceEntry;
import com.m2m.management.pecoff4j.constant.ResourceType;
import com.m2m.management.pecoff4j.io.PEParser;
import com.m2m.management.pecoff4j.io.ResourceParser;
import com.m2m.management.pecoff4j.resources.StringFileInfo;
import com.m2m.management.pecoff4j.resources.StringTable;
import com.m2m.management.pecoff4j.resources.VersionInfo;
import com.m2m.management.pecoff4j.util.IconExtractor;
import com.m2m.management.pecoff4j.util.ResourceHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @date ：Created in 6/8/20 2:34 PM
 * @description：
 */
public class ExeUtil {
    //iconname　must be ico suff
    public static boolean saveIcon(String filepath, String outDir, String iconname)
    {
        File file = new File(filepath);
        if(!file.exists()){
            System.out.println("exe not exist");
            return false;
        }
        File outdir = new File(outDir);
        if(!outdir.exists()) outdir.mkdirs();
        if(new File(outDir+"/"+iconname).exists()){
            new File(outDir+"/"+iconname).delete();
        }

        try{
            IconExtractor.extract(new File(filepath), new File(outDir), iconname);
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;

    }

//    CompanyName = Microsoft Corporation
//    FileDescription = Notepad
//    FileVersion = 6.1.7600.16385 (win7_rtm.090713-1255)
//    InternalName = Notepad
//    LegalCopyright = © Microsoft Corporation. All rights reserved.
//    OriginalFilename = NOTEPAD.EXE
//    ProductName = Microsoft® Windows® Operating System
//    ProductVersion = 6.1.7600.16385
    public static Map<String, String> getInfo(String path) throws IOException {
        PE pe = PEParser.parse(path);
        ResourceDirectory rd = pe.getImageData().getResourceTable();
        Map<String, String> exeMap = new HashMap<>();
        ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
        for (int i = 0; i < entries.length; i++) {
            byte[] data = entries[i].getData();
            VersionInfo version = ResourceParser.readVersionInfo(data);

            StringFileInfo strings = version.getStringFileInfo();
            StringTable table = strings.getTable(0);
            for (int j = 0; j < table.getCount(); j++) {
                String key = table.getString(j).getKey();
                String value = table.getString(j).getValue();
                System.out.println(key+"#"+value);
                exeMap.put(key, value);
            }
        }
        return exeMap;
    }

}
