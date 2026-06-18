package com.m2m.management.repository;

import com.m2m.management.entity.Repo;
import com.m2m.management.entity.RepoApp;
import com.m2m.management.entity.Storage;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IRepoAppsRepository extends JpaRepository<RepoApp, Long> {

    List<RepoApp> findByStorageAndOrgAndFilenameContaining(Storage storage, String org,  String keyword, Pageable pageable);
    List<RepoApp> findByOrg(String org);
    List<RepoApp> findByFilenameContaining(String keyword);
    List<RepoApp> findByPkgnameAndVersionname(String pkgname, String versionname);
    List<RepoApp> findByStorageAndPkgnameAndVersionname(Storage storage, String pkgname, String versionname);
    List<RepoApp> findByStorageAndPkgnameAndVersionnameAndFilename(Storage storage, String pkgname, String versionname, String filename);
    List<RepoApp> findByPkgname(String pkgname);
    List<RepoApp> findByPkgnameAndStorage(String pkgname, Storage storage);
    //name 查询
    List<RepoApp> findByFilename(String name);
    List<RepoApp> findByStorage(Storage storage);

    List<RepoApp> findByPkgnameAndFilenameContaining(String pkgname, String filename);
    List<RepoApp> findByStorageAndPkgnameAndFilenameContaining(Storage storage, String pkgname, String filename);
    long countByStorageAndFilenameContaining(Storage storage, String filename);

    long countByStorage(Storage storage);
    void deleteByOrg(String org);
}
