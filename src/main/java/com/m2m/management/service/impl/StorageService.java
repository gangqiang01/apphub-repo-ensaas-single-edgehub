package com.m2m.management.service.impl;

import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.Storage;
import com.m2m.management.entity.User;
import com.m2m.management.repository.IStorageRepository;
import com.m2m.management.service.IStorageService;
import com.m2m.management.service.IUserService;
import com.m2m.management.utils.AesGcmUtil;
import com.m2m.management.utils.S3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Date;
import java.security.GeneralSecurityException;

@Service
@Slf4j
public class StorageService implements IStorageService {

    @Autowired
    private IStorageRepository storageRepository;

    @Autowired
    private IUserService userService;

    @Override
    public List<Storage> getAll(String keywords, int currentPage, int limit) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<Storage> storages = storageRepository.findByBlobbucketContaining(keywords, pageable);
            migrateLegacyCredentials(storages);
            return storages;
        }catch(Exception e){
            log.error("Failed to load storage page or migrate credentials", e);
            throw new IllegalStateException("Failed to load storage page or migrate credentials", e);
        }
    }

    @Override
    public List<Storage> getAll() {
        try{
            List<Storage> storages = storageRepository.findAll();
            migrateLegacyCredentials(storages);
            return storages;
        }catch(Exception e){
            log.error("Failed to load storage configurations or migrate credentials", e);
            throw new IllegalStateException("Failed to load storage configurations or migrate credentials", e);
        }
    }

    @Override
    public Storage get(long sid) {
        try{
            Optional<Storage> opstorage = storageRepository.findById(sid);
            return migrateLegacyCredentials(opstorage.orElse(null));
        }catch(Exception e){
            log.error("Failed to load storage configuration or migrate credentials, sid={}", sid, e);
            throw new IllegalStateException("Failed to load storage configuration or migrate credentials", e);
        }
    }

    @Override
    public Storage getByType(String type) {
        try{
            Storage storage = storageRepository.findByType(type);
            return migrateLegacyCredentials(storage);
        }catch(Exception e){
            log.error("Failed to load storage by type or migrate credentials, type={}", type, e);
            throw new IllegalStateException("Failed to load storage by type or migrate credentials", e);
        }
    }

    @Override
    public Storage get(String blobbucket) {
        try{
            Storage storage = storageRepository.findByBlobbucket(blobbucket);
            return migrateLegacyCredentials(storage);
        }catch(Exception e){
            log.error("Failed to load storage by bucket or migrate credentials", e);
            throw new IllegalStateException("Failed to load storage by bucket or migrate credentials", e);
        }
    }

    @Override
    public  long count(String bloburl, String blobbucket, String blobaccesskey, String type) {
        try{
            List<Storage> storages = storageRepository.findAllByBloburlAndBlobbucketAndType(
                    bloburl, blobbucket, type);
            migrateLegacyCredentials(storages);
            return countByAccessKey(storages, blobaccesskey);
        }catch(Exception e){
            log.error("Failed to count matching storage credentials", e);
            throw new IllegalStateException("Failed to count matching storage credentials", e);
        }
    }

    @Override
    public  long count(String blobbucket, String blobaccesskey, String type) {
        try{
            List<Storage> storages = storageRepository.findAllByBlobbucketAndType(blobbucket, type);
            migrateLegacyCredentials(storages);
            return countByAccessKey(storages, blobaccesskey);
        }catch(Exception e){
            log.error("Failed to count matching storage credentials", e);
            throw new IllegalStateException("Failed to count matching storage credentials", e);
        }
    }

    @Override
    public Storage get(String bloburl, String blobbucket, String blobaccesskey, String type) {
        try{
            List<Storage> storages = storageRepository.findAllByBloburlAndBlobbucketAndType(
                    bloburl, blobbucket, type);
            migrateLegacyCredentials(storages);
            return findByAccessKey(storages, blobaccesskey);
        }catch(Exception e){
            log.error("Failed to find storage by credentials", e);
            throw new IllegalStateException("Failed to find storage by credentials", e);
        }
    }

    @Override
    public Storage get(String blobbucket, String blobaccesskey, String type) {
        try{
            List<Storage> storages = storageRepository.findAllByBlobbucketAndType(blobbucket, type);
            migrateLegacyCredentials(storages);
            return findByAccessKey(storages, blobaccesskey);
        }catch(Exception e){
            log.error("Failed to find storage by credentials", e);
            throw new IllegalStateException("Failed to find storage by credentials", e);
        }
    }

    @Override
    public long count(String keywords) {
        try{
            long count = 0;
            if(keywords == null ||keywords.equals("")) {
                count = storageRepository.count();
            }else{
                count = storageRepository.countByBlobbucketContaining(keywords);
            }
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long count() {
        try{
            long count = storageRepository.count();
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean add(Storage storage) {
        try{
            storage.setTs(new Date().getTime());
            encryptCredentials(storage);
            storageRepository.save(storage);
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
    public boolean update(Long sid, Storage storage) {
        try{
            Storage oldStorage = storageRepository.findByChoose(1);
            if(oldStorage != null){
                migrateLegacyCredentials(oldStorage);
                oldStorage.setChoose(0);
                storageRepository.save(oldStorage);
            }
            Storage newStorage = storageRepository.findById(sid).get();
            migrateLegacyCredentials(newStorage);
            newStorage.setChoose(1);
            storageRepository.save(newStorage);
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
    public Storage getByChoose(int choose) {
        try{
            Storage storage = storageRepository.findByChoose(choose);
            if(storage != null){
                return migrateLegacyCredentials(storage);
            }
        }catch(Exception e){
            log.error("Failed to load selected storage or migrate credentials", e);
            throw new IllegalStateException("Failed to load selected storage or migrate credentials", e);
        }
        return null;
    }

    @Override
    public boolean update(Storage storage) {
        try{
            encryptCredentials(storage);
            storageRepository.save(storage);
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
    public boolean delete(long sid) {
        try{
            storageRepository.deleteById(sid);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void encryptCredentials(Storage storage) throws GeneralSecurityException {
        storage.storedBlobaccesskey(
                AesGcmUtil.encryptIfNeeded(storage.storedBlobaccesskey()));
        storage.storedBlobsecretkey(
                AesGcmUtil.encryptIfNeeded(storage.storedBlobsecretkey()));
    }

    private Storage migrateLegacyCredentials(Storage storage) throws GeneralSecurityException {
        if (storage == null) {
            return null;
        }
        String oldAccessKey = storage.storedBlobaccesskey();
        String oldSecretKey = storage.storedBlobsecretkey();
        String encryptedAccessKey = AesGcmUtil.encryptIfNeeded(oldAccessKey);
        String encryptedSecretKey = AesGcmUtil.encryptIfNeeded(oldSecretKey);
        boolean changed = !equals(oldAccessKey, encryptedAccessKey)
                || !equals(oldSecretKey, encryptedSecretKey);
        if (changed) {
            storage.storedBlobaccesskey(encryptedAccessKey);
            storage.storedBlobsecretkey(encryptedSecretKey);
            storageRepository.save(storage);
        }
        return storage;
    }

    private void migrateLegacyCredentials(List<Storage> storages) throws GeneralSecurityException {
        if (storages == null) {
            return;
        }
        for (Storage storage : storages) {
            migrateLegacyCredentials(storage);
        }
    }

    private boolean equals(String first, String second) {
        return first == null ? second == null : first.equals(second);
    }

    private long countByAccessKey(List<Storage> storages, String accessKey) {
        long count = 0;
        if (storages != null) {
            for (Storage storage : storages) {
                if (equals(storage.getBlobaccesskey(), accessKey)) {
                    count++;
                }
            }
        }
        return count;
    }

    private Storage findByAccessKey(List<Storage> storages, String accessKey) {
        if (storages != null) {
            for (Storage storage : storages) {
                if (equals(storage.getBlobaccesskey(), accessKey)) {
                    return storage;
                }
            }
        }
        return null;
    }

}
