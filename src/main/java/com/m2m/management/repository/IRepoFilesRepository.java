package com.m2m.management.repository;

import com.m2m.management.entity.RepoFile;
import com.m2m.management.entity.Storage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRepoFilesRepository extends JpaRepository<RepoFile, Long> {
    List<RepoFile> findByFilenameContaining(String keyword, Pageable pageable);
    List<RepoFile> findByStorageAndTypeAndFilenameContaining(Storage storage, String type, String keyword, Pageable pageable);
    List<RepoFile> findByFilenameContaining(String keyword);
    List<RepoFile> findByFilenameAndType(String filename, String type);
    List<RepoFile> findByFilenameAndTypeAndStorage(String filename, String type, Storage storage);
    List<RepoFile> findByType(String type);
    List<RepoFile> findByStorage(Storage storage);
    List<RepoFile> findByTypeAndStorage(String type, Storage storage);
    List<RepoFile> findByTypeAndFormatAndStorage(String type, String format, Storage storage);
    List<RepoFile> findByFilename(String filename);
    //name 查询
    List<RepoFile> findByStorageAndFilename(Storage storage, String filename);
    long countByFilenameContaining(String filename);
    long countByStorageAndTypeAndFilenameContaining(Storage storage, String type, String keyword);
    long countByStorage(Storage storage);
    long countByTypeAndStorage(String type, Storage storage);
}
