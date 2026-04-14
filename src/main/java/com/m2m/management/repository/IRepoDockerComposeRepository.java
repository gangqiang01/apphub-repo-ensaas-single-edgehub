package com.m2m.management.repository;


import com.m2m.management.entity.RepoDockerCompose;
import com.m2m.management.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IRepoDockerComposeRepository extends JpaRepository<RepoDockerCompose, Long> {
    List<RepoDockerCompose> findByStorageAndTagContaining(Storage storage, String keyword, Pageable pageable);

//    @Query(value="select * from RepoDockerCompose repoDockerCompose where repoDockerCompose.tag=?1",nativeQuery=true)
    List<RepoDockerCompose> findByTag(String tag);
    List<RepoDockerCompose> findByStorage(Storage storage);
    List<RepoDockerCompose> findByStorageAndTagAndVersion(Storage storage, String tag, String version);
    List<RepoDockerCompose> findByType(String type);
    List<RepoDockerCompose> findByTypeAndStorage(String type, Storage storage);

    long countByStorageAndTagContaining(Storage storage, String keyword);

    long countByTypeAndStorage(String type, Storage storage);

    long countByStorage(Storage storage);

    @Transactional
    void deleteByTag(String tag);
}

