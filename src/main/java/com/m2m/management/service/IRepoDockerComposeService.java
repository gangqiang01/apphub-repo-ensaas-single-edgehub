package com.m2m.management.service;

import com.m2m.management.entity.RepoDockerCompose;
import com.m2m.management.entity.Storage;

import java.util.List;

public interface IRepoDockerComposeService {
    List<RepoDockerCompose> getAllByPage(String keywords, int currentPage, int limit, Storage storage);
    List<RepoDockerCompose>getAll();
    List<RepoDockerCompose>getByType(String type);
    List<RepoDockerCompose>getAllByType(String type, Storage storage);

    List<RepoDockerCompose>getAll(Storage storage);

    RepoDockerCompose get(long rdcid);

    RepoDockerCompose get(String name, String version, Storage storage);


    boolean add(RepoDockerCompose rdcid);

    boolean update(long rdcid, RepoDockerCompose repoDockerCompose);


    boolean delete(long rdcid);

    boolean deleteRepoDockerCompose(String name);

    long count(String keywords, Storage storage);
    long countByType(String type, Storage storage);
    long count();
    long count(Storage storage);
    boolean deleteAll();
}
