package com.m2m.management.service;

import com.m2m.management.entity.CloudRepoStatus;
import com.m2m.management.entity.Storage;

import java.util.List;

public interface ICloudRepoStatusService {

    List<CloudRepoStatus> getAllByPage(String type, String keywords, int currentPage, int limit);
    List<CloudRepoStatus> getAllByPage(Storage storage, String type, String keywords, int currentPage, int limit);
    List<CloudRepoStatus> getAllByType(String type);
    CloudRepoStatus getByDpname(String dpname, String type, String pkgname, String version);
    CloudRepoStatus getByDpname(Storage storage,  String dpname, String type, String pkgname, String version);
    boolean isRunning(String type, String pkgname, String version);
    boolean isRunning(Storage storage, String type, String pkgname, String version);
    boolean isRunning(Storage storage, String type, String filename);
    boolean add(CloudRepoStatus cloudRepoStatus);

    boolean update(CloudRepoStatus cloudRepoStatus);
    boolean delete(long cid);
    boolean deleteAll();
    boolean deleteAll(Storage storage);
    boolean deleteByType(String type);
    boolean deleteByType(Storage storage, String type);

    long count(String type, String keywords);
    long count(Storage storage, String type, String keywords);
}
