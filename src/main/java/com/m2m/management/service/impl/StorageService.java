package com.m2m.management.service.impl;

import com.m2m.management.constant.UserRole;
import com.m2m.management.entity.Storage;
import com.m2m.management.entity.User;
import com.m2m.management.repository.IStorageRepository;
import com.m2m.management.service.IStorageService;
import com.m2m.management.service.IUserService;
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
            return storages;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Storage> getAll() {
        try{
            List<Storage> storages = storageRepository.findAll();
            return storages;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Storage get(long sid) {
        try{
            Optional<Storage> opstorage = storageRepository.findById(sid);
            Storage storage = opstorage.get();
            return storage;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Storage getByType(String type) {
        try{
            Storage storage = storageRepository.findByType(type);
            return storage;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Storage get(String blobbucket) {
        try{
            Storage storage = storageRepository.findByBlobbucket(blobbucket);
            return storage;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public  long count(String bloburl, String blobbucket, String blobaccesskey, String type) {
        try{
            long count = 0;
            count = storageRepository.countByBloburlAndBlobbucketAndBlobaccesskeyAndType(bloburl, blobbucket, blobaccesskey, type);
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
    public  long count(String blobbucket, String blobaccesskey, String type) {
        try{
            long count = 0;
            count = storageRepository.countByBlobbucketAndBlobaccesskeyAndType(blobbucket, blobaccesskey, type);
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
    public Storage get(String bloburl, String blobbucket, String blobaccesskey, String type) {
        try{
            Storage storage = storageRepository.findByBloburlAndBlobbucketAndBlobaccesskeyAndType(bloburl, blobbucket, blobaccesskey, type);
            return storage;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Storage get(String blobbucket, String blobaccesskey, String type) {
        try{
            Storage storage = storageRepository.findByBlobbucketAndBlobaccesskeyAndType(blobbucket, blobaccesskey, type);
            return storage;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
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
                oldStorage.setChoose(0);
                storageRepository.save(oldStorage);
            }
            Storage newStorage = storageRepository.findById(sid).get();
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
                return storage;
            }
        }catch(NoSuchElementException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Storage storage) {
        try{
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

}
