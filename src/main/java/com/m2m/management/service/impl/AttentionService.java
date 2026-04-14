package com.m2m.management.service.impl;

import com.m2m.management.entity.Attention;
import com.m2m.management.entity.User;
import com.m2m.management.repository.IAttentionRepository;
import com.m2m.management.service.IAttentionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class AttentionService implements IAttentionService {

    @Autowired
    private IAttentionRepository attentionRepository;

    @Override
    public List<Attention> getAll() {
        try{
            List<Attention> attentions = attentionRepository.findAll();
            return attentions;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Attention> getAttentionByTypeAndUser(String type, User user) {
        try{
            List<Attention> attentions = attentionRepository.findByTypeAndUser(type, user);
            return attentions;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Attention> getAttentionByNameAndType(String name, String type) {
        try{
            List<Attention> attentions = attentionRepository.findByNameAndType(name, type);
            return attentions;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(Attention attention) {
        try{
            attentionRepository.save(attention);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Attention> getAttentionByNameAndTypeAndUser(String name, String type, User user) {
        try{
            List<Attention> attentionList = attentionRepository.findByNameAndTypeAndUser(name, type, user);
            return attentionList;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteByAid(long aid) {
        try{
            attentionRepository.deleteById(aid);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
