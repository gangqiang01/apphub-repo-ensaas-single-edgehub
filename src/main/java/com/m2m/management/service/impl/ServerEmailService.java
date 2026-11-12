package com.m2m.management.service.impl;

import com.m2m.management.entity.ServerEmail;
import com.m2m.management.repository.IServerEmailRepostory;
import com.m2m.management.service.IServerEmailService;
import com.m2m.management.utils.AesGcmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.security.GeneralSecurityException;

@Service
@Slf4j
public class ServerEmailService implements IServerEmailService {

    @Autowired
    IServerEmailRepostory serverEmailRepostory;

    @Override
    public boolean add(ServerEmail serverEmail) {
        try{
            encryptPassword(serverEmail);
            serverEmailRepostory.save(serverEmail);
            return true;
        }catch(Exception e){
            log.error("Failed to save email server configuration", e);
            return false;
        }
    }

    @Override
    public ServerEmail getById(long id) {
        try{
            ServerEmail serverEmail = serverEmailRepostory.findById(id);
            return migrateLegacyPassword(serverEmail);
        }catch(Exception e){
            log.error("Failed to load email server configuration", e);
            throw new IllegalStateException("Failed to load email server configuration", e);
        }
    }

    @Override
    public List<ServerEmail> getAll() {
        try{
            List<ServerEmail> serverEmailList = serverEmailRepostory.findAll();
            if (serverEmailList != null) {
                for (ServerEmail serverEmail : serverEmailList) {
                    migrateLegacyPassword(serverEmail);
                }
            }
            return serverEmailList;
        }catch(Exception e){
            log.error("Failed to load email server configurations", e);
            throw new IllegalStateException("Failed to load email server configurations", e);
        }
    }

    @Override
    public boolean update(ServerEmail serverEmail) {
        try{
            encryptPassword(serverEmail);
            serverEmailRepostory.save(serverEmail);
            return true;
        }catch(Exception e){
            log.error("Failed to update email server configuration", e);
            return false;
        }
    }

    private void encryptPassword(ServerEmail serverEmail) throws GeneralSecurityException {
        serverEmail.storedPassword(AesGcmUtil.encryptIfNeeded(serverEmail.storedPassword()));
    }

    private ServerEmail migrateLegacyPassword(ServerEmail serverEmail) throws GeneralSecurityException {
        if (serverEmail == null) {
            return null;
        }
        String oldPassword = serverEmail.storedPassword();
        String encryptedPassword = AesGcmUtil.encryptIfNeeded(oldPassword);
        if (!equals(oldPassword, encryptedPassword)) {
            serverEmail.storedPassword(encryptedPassword);
            serverEmailRepostory.save(serverEmail);
        }
        return serverEmail;
    }

    private boolean equals(String first, String second) {
        return first == null ? second == null : first.equals(second);
    }
}
