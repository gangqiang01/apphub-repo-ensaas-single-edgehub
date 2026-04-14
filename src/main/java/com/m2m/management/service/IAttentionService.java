package com.m2m.management.service;

import com.m2m.management.entity.Attention;
import com.m2m.management.entity.User;

import java.util.List;

public interface IAttentionService {

    List<Attention> getAll();

    boolean add(Attention attention);

    List<Attention> getAttentionByNameAndTypeAndUser(String name, String type, User user);

    boolean deleteByAid(long aid);

    List<Attention> getAttentionByTypeAndUser(String type, User user);

    List<Attention> getAttentionByNameAndType(String name, String type);
}
