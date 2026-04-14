/**
 * @author ：gangqiang
 * @date ：Created in 4/28/19 3:49 PM
 * @description：get repoType class
 * @modified By：
 * @version: v1.0$
 */
package com.m2m.management.service.impl;

import com.m2m.management.entity.RepoType;
import com.m2m.management.repository.IRepoTypeRepository;
import com.m2m.management.service.IRepoTypeService;
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
 * @description：action repoType db table
 */
@Service
@Slf4j
public class RepoTypeService implements IRepoTypeService {

    @Autowired
    private IRepoTypeRepository repoTypeRepository;


    @Override
    public List<RepoType> getAll(String keywords, int currentPage, int limit) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoType> repoTypes = repoTypeRepository.findByTypenameContaining(keywords, pageable);
            return repoTypes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoType get(long rtid) {
        try{
            Optional<RepoType> oprepotype = repoTypeRepository.findById(rtid);
            RepoType repotype = oprepotype.get();
            return repotype;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoType get(String typename) {
        try{
            RepoType repotype = null;
            List<RepoType> repotypes = repoTypeRepository.findByTypename(typename);
            if(repotypes.size()>0) repotype = repotypes.get(0);
            return repotype;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean add(RepoType repoType) {
        try{
            repoTypeRepository.save(repoType);
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
    public boolean update(long rtid, RepoType repoType) {
        try{
            Optional<RepoType> oprt = repoTypeRepository.findById(rtid);
            RepoType rt = oprt.get();
            rt.setTypename(repoType.getTypename());
            rt.setTs(new Date().getTime());
            repoTypeRepository.save(rt);
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
    public boolean delete(long rtid) {
        try{
            repoTypeRepository.deleteById(rtid);
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
