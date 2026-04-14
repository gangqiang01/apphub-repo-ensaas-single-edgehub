package com.m2m.management.repository;

import com.m2m.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserRepository extends JpaRepository<User, Long> {
    //分页模糊查询
    List<User> findByNameContaining(String keyword, Pageable pageable);
    List<User> findByNameContaining(String keyword);
    //name 查询
    User findByName(String name);
    List<User> findByRole(int role);
    long countByNameContaining(String keyword);
}
