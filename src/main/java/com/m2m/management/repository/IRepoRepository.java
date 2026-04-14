package com.m2m.management.repository;

import com.m2m.management.entity.Repo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findByReponameContaining(String keyword, Pageable pageable);
    List<Repo> findByReponameContaining(String keyword);

    //name 查询
    List<Repo> findByReponame(String name);
    long countByReponameContaining(String keyword);
}
