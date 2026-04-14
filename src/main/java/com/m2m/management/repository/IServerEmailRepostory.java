package com.m2m.management.repository;

import com.m2m.management.entity.ServerEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IServerEmailRepostory extends JpaRepository<ServerEmail, Long> {

    ServerEmail findById(long id);


}
