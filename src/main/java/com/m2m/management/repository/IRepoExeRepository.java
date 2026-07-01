package com.m2m.management.repository;

import com.m2m.management.entity.RepoExe;
import com.m2m.management.entity.Storage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IRepoExeRepository extends JpaRepository<RepoExe, Long> {
    List<RepoExe> findByStorageAndOrgAndProjectnameContaining(Storage storage, String org, String keyword, Pageable pageable);
    List<RepoExe> findByStorageAndOrgAndTypeAndProjectnameContaining(Storage storage, String org, String type, String keyword, Pageable pageable);
    List<RepoExe> findByStorageAndTypeInAndProjectnameContaining(Storage storage, Collection types, String keyword, Pageable pageable);
    List<RepoExe> findByStorageAndProjectnameAndVersion(Storage storage, String projectname, String version);
    List<RepoExe> findByStorageAndProjectnameAndVersionAndOrg(Storage storage, String projectname, String version, String org);
    List<RepoExe> findByStorageAndFilenameAndProjectnameAndVersion(Storage storage, String filename, String projectname, String version);
    List<RepoExe> findByStorageAndFilename(Storage storage, String filename);
    List<RepoExe> findByType(String type);
    List<RepoExe> findByTypeAndStorage(String type, Storage storage);
    List<RepoExe> findByTypeIn(Collection types);
    List<RepoExe> findByStorageAndTypeIn(Storage storage, Collection types);
    List<RepoExe> findByStorage(Storage storage);
    long countByStorageAndTypeInAndProjectnameContaining(Storage storage, Collection types, String projectname);
    long countByStorageAndProjectnameContaining(Storage storage, String filename);
    long countByStorageAndTypeAndProjectnameContaining(Storage storage, String type, String projectname);
    long countByStorageAndOrgAndTypeAndProjectnameContaining(Storage storage,String org, String type, String projectname);
    long countByStorage(Storage storage);
    long countByTypeAndStorage(String type, Storage storage);
    void deleteByOrg(String org);
    List<RepoExe> findByOrg(String org);
}
