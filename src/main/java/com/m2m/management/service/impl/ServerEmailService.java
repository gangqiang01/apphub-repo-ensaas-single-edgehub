package com.m2m.management.service.impl;

import com.m2m.management.entity.ServerEmail;
import com.m2m.management.repository.IServerEmailRepostory;
import com.m2m.management.service.IServerEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class ServerEmailService implements IServerEmailService {

    @Autowired
    IServerEmailRepostory serverEmailRepostory;

    @Override
    public boolean add(ServerEmail serverEmail) {
        try{
            serverEmailRepostory.save(serverEmail);
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
    public ServerEmail getById(long id) {
        try{
            ServerEmail serverEmail = serverEmailRepostory.findById(id);
            return serverEmail;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ServerEmail> getAll() {
        try{
            List<ServerEmail> serverEmailList = serverEmailRepostory.findAll();
            return serverEmailList;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(ServerEmail serverEmail) {
        try{
            serverEmailRepostory.save(serverEmail);
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
