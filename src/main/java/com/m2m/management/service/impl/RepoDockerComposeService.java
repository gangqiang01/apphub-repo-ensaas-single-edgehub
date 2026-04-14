package com.m2m.management.service.impl;

import com.m2m.management.entity.RepoDockerCompose;
import com.m2m.management.entity.Storage;
import com.m2m.management.repository.IRepoDockerComposeRepository;
import com.m2m.management.service.IRepoDockerComposeService;
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
/**
 * @date ：Created in 5/11/20 6:28 PM
 * @description：repo docker compose
 */
@Service
@Slf4j
public class RepoDockerComposeService implements IRepoDockerComposeService {

    @Autowired
    private IRepoDockerComposeRepository repoDockerComposeRepository;

    @Override
    public List<RepoDockerCompose> getAllByPage(String keywords, int currentPage, int limit, Storage storage) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoDockerCompose> repoDockerComposes = repoDockerComposeRepository.findByStorageAndTagContaining(storage, keywords, pageable);
            return repoDockerComposes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoDockerCompose> getAll() {
        try{
            List<RepoDockerCompose> repoDockerComposes = repoDockerComposeRepository.findAll();
            return repoDockerComposes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoDockerCompose> getByType(String type) {
        try{
            List<RepoDockerCompose> repoDockerComposes = repoDockerComposeRepository.findByType(type);
            return repoDockerComposes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoDockerCompose> getAllByType(String type, Storage storage) {
        try{
            List<RepoDockerCompose> repoDockerComposes = repoDockerComposeRepository.findByTypeAndStorage(type, storage);
            return repoDockerComposes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoDockerCompose> getAll(Storage storage) {
        try{
            List<RepoDockerCompose> repoDockerComposes = repoDockerComposeRepository.findByStorage(storage);
            return repoDockerComposes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoDockerCompose get(long rdcid) {
        try{
            Optional<RepoDockerCompose> opRepoDockerComposes = repoDockerComposeRepository.findById(rdcid);
            RepoDockerCompose repoApp = opRepoDockerComposes.get();
            return repoApp;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public RepoDockerCompose get(String name, String version, Storage storage) {
        try{
//            log.info(tag);
            List<RepoDockerCompose> repoDockerComposes = repoDockerComposeRepository.findByStorageAndTagAndVersion(storage, name, version);
//            log.info(""+repoDockerComposes.size());
            if(repoDockerComposes.size() > 0){
                return repoDockerComposes.get(0);
            }
            return null;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(RepoDockerCompose repoDockerCompose) {
        try{
            repoDockerComposeRepository.save(repoDockerCompose);
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
    public boolean update(long rdcid, RepoDockerCompose repoDockerCompose) {
        try{
            RepoDockerCompose crepoDockerCompose = repoDockerComposeRepository.findById(rdcid).get();
            crepoDockerCompose.setDescription(repoDockerCompose.getDescription());
            crepoDockerCompose.setTag(repoDockerCompose.getTag());
            crepoDockerCompose.setTs(new Date().getTime());
            repoDockerComposeRepository.save(crepoDockerCompose);
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
    public boolean delete(long rdcid) {
        try{

            repoDockerComposeRepository.deleteById(rdcid);
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
    public boolean deleteRepoDockerCompose(String tag) {
        try{

            repoDockerComposeRepository.deleteByTag(tag);
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
    public long count(String keywords, Storage storage) {
        try{
            long count = 0;
            count = repoDockerComposeRepository.countByStorageAndTagContaining(storage, keywords);
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
    public long countByType(String type, Storage storage) {
        try{
            long count = 0;
            count = repoDockerComposeRepository.countByTypeAndStorage(type, storage);
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
            long count = repoDockerComposeRepository.count();
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
    public long count(Storage storage) {
        try{
            long count = repoDockerComposeRepository.countByStorage(storage);
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
    public boolean deleteAll() {
        try{
            repoDockerComposeRepository.deleteAll();
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
