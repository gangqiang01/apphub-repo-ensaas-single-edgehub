package com.m2m.management.repository;


import com.m2m.management.entity.Storage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IStorageRepository  extends JpaRepository<Storage, Long> {

    List<Storage> findByBlobbucketContaining(String keywords, Pageable pageable);

    long countByBlobbucketContaining(String keywords);

    Storage  findByChoose(int choose);

    Storage findByBlobbucket(String blobbucket);

    Storage findByType(String type);

    long countByBloburlAndBlobbucketAndBlobaccesskeyAndType(String bloburl, String blobbucket, String blobaccesskey, String type);

    long countByBlobbucketAndBlobaccesskeyAndType(String blobbucket, String blobaccesskey, String type);

    Storage findByBloburlAndBlobbucketAndBlobaccesskeyAndType(String bloburl, String blobbucket, String blobaccesskey, String type);
    Storage findByBlobbucketAndBlobaccesskeyAndType(String blobbucket, String blobaccesskey, String type);

}
