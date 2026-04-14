package com.m2m.management.service;

import com.m2m.management.entity.ServerEmail;

import java.util.List;

public interface IServerEmailService {

    boolean add(ServerEmail serverEmail);

    ServerEmail getById(long id);

    List<ServerEmail> getAll();

    boolean update(ServerEmail serverEmail);
}
