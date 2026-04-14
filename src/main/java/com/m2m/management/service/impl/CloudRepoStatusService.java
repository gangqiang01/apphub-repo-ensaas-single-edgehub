package com.m2m.management.service.impl;

import com.m2m.management.entity.CloudRepoStatus;
import com.m2m.management.entity.Storage;
import com.m2m.management.repository.ICloudRepoStatusRepository;
import com.m2m.management.repository.IRepoAppsRepository;
import com.m2m.management.service.ICloudRepoStatusService;
import com.m2m.management.service.IRepoAppService;
import com.m2m.management.utils.StatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @date ：Created in 9/3/20 1:19 PM
 * @description：cloud repo service
 */

@Service
@Slf4j
public class CloudRepoStatusService implements ICloudRepoStatusService {

    @Autowired
    private ICloudRepoStatusRepository cloudRepoStatusRepository;


    @Override
    public List<CloudRepoStatus> getAllByPage(String type, String keywords, int currentPage, int limit) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<CloudRepoStatus> cloudRepoStatus = cloudRepoStatusRepository.findByTypeAndPkgnameContaining(type, keywords, pageable);
            return cloudRepoStatus;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CloudRepoStatus> getAllByPage(Storage storage, String type, String keywords, int currentPage, int limit) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<CloudRepoStatus> cloudRepoStatus = cloudRepoStatusRepository.findByStorageAndTypeAndPkgnameContaining(storage, type, keywords, pageable);
            return cloudRepoStatus;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CloudRepoStatus> getAllByType(String type) {
        try{
            CloudRepoStatus cloudRepoStatus = null;
            List<CloudRepoStatus> cloudRepoStatuses = cloudRepoStatusRepository.findByType(type);
            if(cloudRepoStatuses.size()> 0){
                cloudRepoStatus = cloudRepoStatuses.get(0);
            }
            return cloudRepoStatuses;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CloudRepoStatus getByDpname(String dpname, String type, String pkgname, String version) {
        try{
            log.info(String.format("%s#%s#%s#%s", dpname, type, pkgname, version));
            CloudRepoStatus cloudRepoStatus = null;
            List<CloudRepoStatus> cloudRepoStatuses = cloudRepoStatusRepository.findByDpnameAndTypeAndPkgnameAndVersion(dpname, type, pkgname, version);
            if(cloudRepoStatuses.size()> 0){
                cloudRepoStatus = cloudRepoStatuses.get(0);
            }
            return cloudRepoStatus;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CloudRepoStatus getByDpname(Storage storage, String dpname, String type, String pkgname, String version) {
        try{
            log.info(String.format("%s#%s#%s#%s#%s", storage, dpname, type, pkgname, version));
            CloudRepoStatus cloudRepoStatus = null;
            List<CloudRepoStatus> cloudRepoStatuses = cloudRepoStatusRepository.findByStorageAndDpnameAndTypeAndPkgnameAndVersion(storage, dpname, type, pkgname, version);
            if(cloudRepoStatuses.size()> 0){
                cloudRepoStatus = cloudRepoStatuses.get(0);
            }
            return cloudRepoStatus;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isRunning(String type, String pkgname, String version) {
        try{
            List<CloudRepoStatus> cloudRepoStatuses = cloudRepoStatusRepository.findByTypeAndPkgnameAndVersionAndStatus(type, pkgname, version, StatusType.BUILDING.ordinal());
            if(cloudRepoStatuses != null&& cloudRepoStatuses.size()> 0)
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isRunning(Storage storage, String type, String pkgname, String version) {
        try{
            List<CloudRepoStatus> cloudRepoStatuses = cloudRepoStatusRepository.findByStorageAndTypeAndPkgnameAndVersionAndStatus(storage, type, pkgname, version, StatusType.BUILDING.ordinal());
            if(cloudRepoStatuses != null && cloudRepoStatuses.size()> 0)
                return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isRunning(Storage storage, String type, String filename) {
        try{
            List<CloudRepoStatus> cloudRepoStatuses = cloudRepoStatusRepository.findByStorageAndTypeAndPkgnameAndStatus(storage, type, filename, StatusType.BUILDING.ordinal());
            if(cloudRepoStatuses != null&& cloudRepoStatuses.size()> 0)
                return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean add(CloudRepoStatus cloudRepoStatus) {
        try{
            cloudRepoStatus.setTs(new Date().getTime());
            cloudRepoStatusRepository.save(cloudRepoStatus);
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
    public boolean update(CloudRepoStatus cloudRepoStatus) {
        try{
            cloudRepoStatusRepository.save(cloudRepoStatus);
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
    public boolean delete(long cid) {
        try{
            cloudRepoStatusRepository.deleteById(cid);
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
    public boolean deleteAll() {
        try{
            cloudRepoStatusRepository.deleteAll();
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
    public boolean deleteAll(Storage storage) {
        try{
            cloudRepoStatusRepository.deleteByStorage(storage);
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
    public boolean deleteByType(String type) {
        try{

            cloudRepoStatusRepository.deleteByType(type);
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
    public boolean deleteByType(Storage storage, String type) {
        try{

            cloudRepoStatusRepository.deleteByStorageAndType(storage, type);
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
    public long count(String type, String keywords) {
        try{
            long count = cloudRepoStatusRepository.countByTypeAndPkgnameContaining(type, keywords);
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
    public long count(Storage storage, String type, String keywords) {
        try{
            long count = cloudRepoStatusRepository.countByStorageAndTypeAndPkgnameContaining(storage, type, keywords);
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
