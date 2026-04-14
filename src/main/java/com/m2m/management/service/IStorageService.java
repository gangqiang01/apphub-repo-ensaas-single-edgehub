package com.m2m.management.service;

import com.m2m.management.entity.Storage;

import java.util.List;

public interface IStorageService {

    List<Storage> getAll(String keywords, int currentPage, int limit);
    List<Storage> getAll();

    long count();

    long count(String keywords);

    boolean add(Storage storage);

    boolean update(Long sid, Storage storage);

    boolean update(Storage storage);

    boolean delete(long sid);

    long count(String bloburl, String blobbucket, String blobaccesskey, String type);

    long count(String blobbucket, String blobaccesskey, String type);
    Storage get(String bloburl, String blobbucket, String blobaccesskey, String type);
    Storage get(String blobbucket, String blobaccesskey, String type);
    Storage get(long sid);

    Storage getByType(String type);

    Storage get(String blobbucket);
    Storage getByChoose(int choose);

}
