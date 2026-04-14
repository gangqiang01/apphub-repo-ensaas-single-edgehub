package com.m2m.management.repository;

import com.m2m.management.entity.CloudRepoStatus;

import com.m2m.management.entity.Storage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface ICloudRepoStatusRepository extends JpaRepository<CloudRepoStatus, Long> {
    List<CloudRepoStatus> findByTypeAndPkgnameContaining(String type, String keyword, Pageable pageable);
    List<CloudRepoStatus> findByStorageAndTypeAndPkgnameContaining(Storage storage, String type, String keyword, Pageable pageable);
    List<CloudRepoStatus> findByType(String type);
    List<CloudRepoStatus> findByDpnameAndTypeAndPkgnameAndVersion(String dpname, String type, String pkgname, String version);
    List<CloudRepoStatus> findByStorageAndDpnameAndTypeAndPkgnameAndVersion(Storage storage, String dpname, String type, String pkgname, String version);
    List<CloudRepoStatus> findByTypeAndPkgnameAndVersionAndStatus(String type, String pkgname, String version, int status);
    List<CloudRepoStatus> findByTypeAndPkgnameAndVersion(String type, String pkgname, String version);
    List<CloudRepoStatus> findByStorageAndTypeAndPkgnameAndVersionAndStatus(Storage storage, String type, String pkgname, String version, int status);
    List<CloudRepoStatus> findByStorageAndTypeAndPkgnameAndStatus(Storage storage, String type, String pkgname, int status);

    //name 查询
    List<CloudRepoStatus> findByTypeAndPkgname(String type, String filename);
    List<CloudRepoStatus> findByTypeAndPkgnameAndStatus(String type, String pkgname, int status);

    @Transactional
    void deleteByType(String type);

    @Transactional
    void deleteByStorage(Storage storage);

    @Transactional
    void deleteByStorageAndType(Storage storage, String type);

    long countByTypeAndPkgnameContaining(String type, String pkgname);
    long countByStorageAndTypeAndPkgnameContaining(Storage storage, String type, String pkgname);
}
