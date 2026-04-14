package com.m2m.management.service;

import com.m2m.management.entity.RepoDocker;
import com.m2m.management.entity.Storage;

import java.util.List;

public interface IRepoDockerService {

    List<RepoDocker> getAllByPage(String keywords, int currentPage, int limit);
    List<RepoDocker>getAll();


    RepoDocker get(long rdid);

    RepoDocker get(String image, String version);


    boolean add(RepoDocker rdid);

    boolean update(long rdid, RepoDocker repoDocker);

    boolean delete(long rdid);

    boolean deleteRepoDocker(String image, String version);

    long count(String keywords);
    long count();
    boolean deleteAll();
}
