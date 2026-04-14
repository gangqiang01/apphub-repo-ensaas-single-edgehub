package com.m2m.management.service;

import com.m2m.management.entity.RepoFile;
import com.m2m.management.entity.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRepoFileService {
    List<RepoFile> getAll();
    List<RepoFile> getAll(Storage storage);
    List<RepoFile> getAll(String keywords, int currentPage, int limit);
    List<RepoFile> getAllByType(String type, String keywords, int currentPage, int limit, Storage storage);
    RepoFile get(long rfid);
    RepoFile getByFilename(String filename, Storage storage);
    RepoFile getByFilenameAndType(String filename, String type, Storage storage);
    RepoFile getByFilename(String filename);
    List<RepoFile> get(String type);
    List<RepoFile> get(String type, Storage storage);
    List<RepoFile> getByTypeAndFormat(String type, String format, Storage storage);
    boolean add(RepoFile repoFile);
    boolean update(long rfid, RepoFile repoFile);
    boolean update(long rfid, String presentation);
    boolean delete(long rfid);
    long count();
    long count(Storage storage);
    long countByType(String type, Storage storage);
    long count(String keywords);
    long count(String type, String keywords, Storage storage);
    boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file);
    boolean deleteAll();
}
