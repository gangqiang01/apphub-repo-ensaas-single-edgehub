package com.m2m.management.repository;

import com.m2m.management.entity.RepoExe;
import com.m2m.management.entity.RepoLinuxPkg;
import com.m2m.management.entity.RepoLinuxPkg;
import com.m2m.management.entity.Storage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface IRepoLinuxPkgRepository extends JpaRepository<RepoLinuxPkg, Long> {
    List<RepoLinuxPkg> findByStorageAndOrgAndTypeAndProductnameContaining(Storage storage, String org, String type, String keyword, Pageable pageable);
    List<RepoLinuxPkg> findByStorageAndOrgAndProductnameContaining(Storage storage, String org, String keyword, Pageable pageable);
    List<RepoLinuxPkg> findByType(String type);
    List<RepoLinuxPkg> findByStorage(Storage storage);
    List<RepoLinuxPkg> findByTypeAndStorage(String type, Storage storage);
    List<RepoLinuxPkg> findByStorageAndProductnameAndVersionAndType(Storage storage, String productname, String version, String type);
    List<RepoLinuxPkg> findByStorageAndProductnameAndVersionAndTypeAndOrg(Storage storage, String productname, String version, String type, String org);
    List<RepoLinuxPkg> findByStorageAndTypeInAndProductnameContaining(Storage storage, Collection types, String keyword, Pageable pageable);
    List<RepoLinuxPkg> findByStorageAndFilename(Storage storage, String filename);
    long countByStorageAndTypeAndProductnameContaining(Storage storage, String type, String keyword);
    long countByStorageAndOrgAndTypeAndProductnameContaining(Storage storage, String org, String type, String keyword);
    long countByStorageAndTypeInAndProductnameContaining(Storage storage, Collection types, String keyword);
    long countByStorageAndProductnameContaining(Storage storage, String keyword);
    long countByType(String type);
    long countByStorage(Storage storage);
    long countByTypeAndStorage(String type, Storage storage);

    @Transactional
    void deleteByProductnameAndType(String type, String productname);
    List<RepoLinuxPkg> findByTypeIn(Collection types);
    List<RepoLinuxPkg> findByStorageAndTypeIn(Storage storage, Collection types);
    long countByTypeIn(Collection types);
    void deleteByOrg(String org);
    List<RepoLinuxPkg> findByOrg(String org);
}
