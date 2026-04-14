package com.m2m.management.service.impl;

import com.m2m.management.entity.Repo;
import com.m2m.management.repository.IRepoRepository;
import com.m2m.management.service.IRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author ：gangqiang
 * @date ：Created in 4/28/19 3:54 PM
 * @description：action repo db table
 */
@Service
@Slf4j
public class RepoService implements IRepoService {

    @Autowired
    private IRepoRepository repoRepository;

    @Override
    public List<Repo> getAll(String keywords, int currentPage, int limit) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<Repo> repos = repoRepository.findByReponameContaining(keywords, pageable);
            return repos;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Repo get(String reponame) {
        try{
            Repo repo = null;
            List<Repo> repos = repoRepository.findByReponame(reponame);

            if(repos != null && repos.size()>0){
                repo = repos.get(0);
            }

            return repo;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Repo get(long rid) {
        try{
            Optional<Repo> oprepo = repoRepository.findById(rid);
            Repo repo = oprepo.get();
            return repo;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(Repo repo) {
        try{
            repoRepository.save(repo);
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
    public boolean update(long rid, Repo repo) {
        try{
            Optional<Repo> oprp = repoRepository.findById(rid);
            Repo rp = oprp.get();
            rp.setDescription(repo.getDescription());
            rp.setRepoType(repo.getRepoType());
            rp.setTs(new Date().getTime());
            repoRepository.save(rp);
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
    public boolean delete(long rid) {
        try{
            repoRepository.deleteById(rid);
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
            if(keywords == null ||keywords.equals("")){
                count = repoRepository.count();
            }else{
                count = repoRepository.countByReponameContaining(keywords);
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
            long count = repoRepository.count();
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
