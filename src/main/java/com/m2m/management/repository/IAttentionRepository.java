package com.m2m.management.repository;

import com.m2m.management.entity.Attention;
import com.m2m.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAttentionRepository extends JpaRepository<Attention, Long> {

    List<Attention> findByNameAndTypeAndUser(String name, String type, User user);

    List<Attention> findByTypeAndUser(String type, User user);

    List<Attention> findByNameAndType(String name, String type);

}
