package com.m2m.management.repository;

import com.m2m.management.entity.Repo;
import com.m2m.management.entity.RepoBsp;
import com.m2m.management.entity.RepoType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRepoTypeRepository extends JpaRepository<RepoType, Long> {
    List<RepoType> findByTypenameContaining(String keyword, Pageable pageable);
    List<RepoType> findByTypenameContaining(String keyword);
    //name 查询
    List<RepoType> findByTypename(String name);
}
