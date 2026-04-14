package com.m2m.management.repository;

import com.m2m.management.entity.RepoDocker;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface IRepoDockerRepository extends JpaRepository<RepoDocker, Long> {
    List<RepoDocker> findByContainerContaining(String keyword, Pageable pageable);
    List<RepoDocker> findByImageAndVersion(String image, String version);
    @Transactional
    void deleteByImageAndVersion(String image, String version);

    long countByContainerContaining(String keyword);
}
