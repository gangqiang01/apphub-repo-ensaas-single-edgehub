package com.m2m.management.repository;

import com.m2m.management.entity.Repo;
import com.m2m.management.entity.RepoBsp;
import com.m2m.management.entity.Storage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRepoBspsRepository extends JpaRepository<RepoBsp, Long> {
    List<RepoBsp> findByStorageAndBoardnameContaining(Storage storage, String keyword, Pageable pageable);
    List<RepoBsp> findByStorageAndOsAndBoardnameContaining(Storage storage, String os, String keyword, Pageable pageable);
    List<RepoBsp> findByBoardnameContaining(String keyword);
    List<RepoBsp> findByStorageAndBoardnameAndVersionname(Storage storage, String boardname, String versionname);
    List<RepoBsp> findByStorageAndBoardnameAndVersionnameAndOs(Storage storage, String boardname, String versionname, String os);
    //name 查询
    List<RepoBsp> findByBoardname(String name);
    List<RepoBsp> findByBoardnameAndStorage(String name, Storage storage);
    List<RepoBsp> findByStorage(Storage storage);
    List<RepoBsp> findByOs(String os);
    List<RepoBsp> findByOsAndStorage(String os, Storage storage);
    long countByStorageAndBoardnameContaining(Storage storage, String keyword);
    long countByStorageAndOsAndBoardnameContaining(Storage storage, String os, String keyword);
    long countByStorageAndOs(Storage storage, String os);
    long countByStorage(Storage storage);
}