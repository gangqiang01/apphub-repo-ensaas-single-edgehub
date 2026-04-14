package com.m2m.management.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlUtils {
    private static final Logger LOG = LoggerFactory.getLogger(YamlUtils.class);

    private final String SERVICE_TAG = "services";
    private final String DOCKER_TAG = "docker";
    private final String IMAGE_TAG = "image";
    private final String CONTAINER_NAME_TAG = "container_name";



    private String dockerVersion = null;
    private String container_name = null;


    public YamlUtils() {

    }

    public Map<String, String> parseYamlForDockerTag(String yamlFile) throws FileNotFoundException {
        int m = 0, n = 0, j = 0, k = 0;
        Map<String, String> map = new LinkedHashMap<String, String>();;


        Yaml yaml = new Yaml();
        File dumpFile = new File(yamlFile);//"docker-compose.yaml"

        Map<String, String> topMap = (Map<String, String>) yaml.load(new FileInputStream(dumpFile));

        Iterator topIter = topMap.entrySet().iterator();
        while (topIter.hasNext()) {
            Map.Entry topEntry = (Map.Entry) topIter.next();
            String topKey = (String) topEntry.getKey();
            Object topVal = topEntry.getValue();

            if (topKey.equals(SERVICE_TAG)) {
                Map<String, String> dockerImageTagMap = (Map<String, String>) topVal;
//LOG.info(dockerImageTagMap.toString());

                for (String dockerImageTagKey : dockerImageTagMap.keySet()) {
                    map.put(DOCKER_TAG + (++m), dockerImageTagKey);
                }

                /*Iterator dockerImageNameAndVersionIter = dockerImageTagMap.entrySet().iterator();

                while (dockerImageNameAndVersionIter.hasNext()) {
                    Map.Entry dockerImageNameAndVersionEntry = (Map.Entry) dockerImageNameAndVersionIter.next();

                    Object dockerImageNameAndVersionVal = dockerImageNameAndVersionEntry.getValue();

                    Map<String, String> dockerImageNameAndVersionMap = (Map<String, String>) dockerImageNameAndVersionVal;
//LOG.info(dockerImageNameAndVersionMap.toString());
                    //for (String dockerImageNameAndVersion : dockerImageNameAndVersionMap.keySet()) {
                    //    if (dockerImageNameAndVersion.equals(IMAGE_TAG) || dockerImageNameAndVersion.equals(CONTAINER_NAME_TAG)) {

                            *//*if (dockerImageNameAndVersionMap.get(dockerImageNameAndVersion).contains(":")) {
                                String beforeDockerVersionStr = dockerImageNameAndVersionMap.get(dockerImageNameAndVersion).substring(0, dockerImageNameAndVersionMap.get(dockerImageNameAndVersion).indexOf(":"));
                                dockerVersion = dockerImageNameAndVersionMap.get(dockerImageNameAndVersion).substring(beforeDockerVersionStr.length() + 1);
                            } else {
                                dockerVersion = "latest";
                            }*//*

                            for (Map.Entry<String, String> entry : map.entrySet()) {
                               //if (n == m) {
                                   map.put(entry.getKey(), dockerImageNameAndVersionMap.toString());


                            *//*if (dockerImageNameAndVersion.equals(IMAGE_TAG)) {
                                //LOG.info(dockerImageNameAndVersionMap.get(dockerImageNameAndVersion));
                                map.put(IMAGE_TAG + n, dockerImageNameAndVersionMap.get(dockerImageNameAndVersion));
                            }

                            if (dockerImageNameAndVersion.equals(CONTAINER_NAME_TAG)) {
                                //LOG.info(dockerImageNameAndVersionMap.get(dockerImageNameAndVersion));
                                map.put(CONTAINER_NAME_TAG + n, dockerImageNameAndVersionMap.get(dockerImageNameAndVersion));
                            }*//*

                            //map.put(IMAGE_TAG, dockerImageNameAndVersionMap.get(dockerImageNameAndVersion));
                            //map.put(, dockerVersion);
                                //}
                                m++;
                            }
                            m = 0;
                            n++;
                        }*/

                        /*if (dockerImageNameAndVersion.equals(CONTAINER_NAME_TAG)) {

                            container_name = dockerImageNameAndVersionMap.get(dockerImageNameAndVersion);

                            for(Map.Entry<String, String> entry : map.entrySet()){
                                if(j == k) {
                                    map.put(container_name, map.get(entry.getKey()));
                                    map.remove(entry.getKey());
                                }
                                k++;
                            }
                            k=0;
                            j++;
                        }*/
                //}
                //}
            }
        }
        return map;
    }

    public Map<String, String> parseYamlForDockerDetails(String yamlFile, String details) throws FileNotFoundException, ClassCastException {
        int m = 0, n = 0, j = 0, k = 0;
        Map<String, String> map = new LinkedHashMap<String, String>();;

        Yaml yaml = new Yaml();
        File dumpFile = new File(yamlFile);//"docker-compose.yaml"

        Map<String, String> topMap = (Map<String, String>) yaml.load(new FileInputStream(dumpFile));

        Iterator topIter = topMap.entrySet().iterator();
        while (topIter.hasNext()) {
            Map.Entry topEntry = (Map.Entry) topIter.next();
            String topKey = (String) topEntry.getKey();
            Object topVal = topEntry.getValue();

            if (topKey.equals(SERVICE_TAG)) {
                Map<String, String> dockerImageTagMap = (Map<String, String>) topVal;
                Iterator dockerImageNameAndVersionIter = dockerImageTagMap.entrySet().iterator();

                while (dockerImageNameAndVersionIter.hasNext()) {
                    Map.Entry dockerImageNameAndVersionEntry = (Map.Entry) dockerImageNameAndVersionIter.next();

                    Object dockerImageNameAndVersionVal = dockerImageNameAndVersionEntry.getValue();

                    Map<String, String> dockerImageNameAndVersionMap = (Map<String, String>) dockerImageNameAndVersionVal;
                    String detailValue="";
                    for (String dockerImageNameAndVersion : dockerImageNameAndVersionMap.keySet()) {
                        if (dockerImageNameAndVersion.equals(details)) {
                            detailValue = dockerImageNameAndVersionMap.get(dockerImageNameAndVersion);
                        }
                    }
                    ++n;
                    map.put(details + n, detailValue);

                }
            }
        }
        return map;
    }

    public void printYamlMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            LOG.info("key: " + entry.getKey() + " value: " + entry.getValue());
        }
    }
}