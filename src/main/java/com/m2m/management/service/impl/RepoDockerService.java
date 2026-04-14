package com.m2m.management.service.impl;

import com.m2m.management.entity.RepoDocker;
import com.m2m.management.entity.Storage;
import com.m2m.management.repository.IRepoDockerRepository;
import com.m2m.management.service.IRepoDockerService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * @date ：Created in 5/11/20 6:27 PM
 * @description：repo docker service
 */

@Service
@Slf4j
public class RepoDockerService implements IRepoDockerService {
    @Autowired
    private IRepoDockerRepository repoDockerRepository;

    @Override
    public List<RepoDocker> getAllByPage(String keywords, int currentPage, int limit) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoDocker> repoDockerComposes = repoDockerRepository.findByContainerContaining(keywords, pageable);
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
    public List<RepoDocker> getAll() {
        try{
            List<RepoDocker> repoDockerComposes = repoDockerRepository.findAll();
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
    public RepoDocker get(long rdid) {
        try{
            Optional<RepoDocker> opRepoDockers = repoDockerRepository.findById(rdid);
            RepoDocker repoApp = opRepoDockers.get();
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
    public RepoDocker get(String image, String version) {
        try{
            List<RepoDocker> repoDockers = repoDockerRepository.findByImageAndVersion(image, version);
            if(repoDockers.size() > 0){
                return repoDockers.get(0);
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
    public boolean add(RepoDocker repoDocker) {
        try{
            repoDockerRepository.save(repoDocker);
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
    public boolean update(long rdid, RepoDocker repoDocker) {
        try{
            RepoDocker crepoDocker = repoDockerRepository.findById(rdid).get();
            crepoDocker.setContainer(repoDocker.getContainer());
            crepoDocker.setImage(repoDocker.getImage());
            crepoDocker.setVersion(repoDocker.getVersion());
            crepoDocker.setTs(new Date().getTime());
            repoDockerRepository.save(crepoDocker);
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
    public boolean delete(long rdid) {
        try{

            repoDockerRepository.deleteById(rdid);
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
    public boolean deleteRepoDocker(String image, String version) {
        try{

            repoDockerRepository.deleteByImageAndVersion(image, version);
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
    public long count(String keywords) {
        try{
            long count = 0;
            count = repoDockerRepository.countByContainerContaining(keywords);
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
            long count = repoDockerRepository.count();
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
            repoDockerRepository.deleteAll();
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
