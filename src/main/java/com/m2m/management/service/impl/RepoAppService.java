package com.m2m.management.service.impl;

import com.m2m.management.entity.RepoApp;
import com.m2m.management.entity.Storage;
import com.m2m.management.repository.IRepoAppsRepository;
import com.m2m.management.service.IRepoAppService;
import com.m2m.management.utils.FileUtil;
import com.m2m.management.configuration.UploadConfig;

import lombok.extern.slf4j.Slf4j;

import static com.m2m.management.utils.UploadUtils.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author ：gangqiang
 * @date ：Created in 4/28/19 3:56 PM
 * @description：action repoapp db table
 */
@Service
@Slf4j
public class RepoAppService implements IRepoAppService {
    private String pathSeparate = File.separator;

    @Autowired
    private IRepoAppsRepository repoAppsRepository;

    @Override
    public List<RepoApp> getAllByPage(String keywords, int currentPage, int limit, Storage storage, String tenantId) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoApp> repoApps = repoAppsRepository.findByStorageAndOrgAndFilenameContaining(storage, tenantId, keywords, pageable);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> getAll(Storage storage) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByStorage(storage);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> getAllByOrg(String tenantId) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByOrg(tenantId);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> getAll() {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findAll();
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoApp get(long raid) {
        try{
            Optional<RepoApp> opRepoApps = repoAppsRepository.findById(raid);
            RepoApp repoApp = opRepoApps.get();
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
    public List<RepoApp> get(String packagename) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByPkgname(packagename);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> get(String packagename, Storage storage) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByPkgnameAndStorage(packagename, storage);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> getByPackageAndFilenameContain(String packagename, String filename) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByPkgnameAndFilenameContaining(packagename, filename);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> getByPackageAndStorageAndFilenameContain(String packagename, String filename, Storage storage) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByStorageAndPkgnameAndFilenameContaining(storage, packagename, filename);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> get(String packagename, String versionname) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByPkgnameAndVersionname(packagename, versionname);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> get(Storage storage, String packagename, String versionname) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByStorageAndPkgnameAndVersionname(storage, packagename, versionname);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoApp> get(String packagename, String versionname, String filename, Storage storage) {
        try{
            List<RepoApp> repoApps = repoAppsRepository.findByStorageAndPkgnameAndVersionnameAndFilename(storage, packagename, versionname, filename);
            return repoApps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(RepoApp repoApp) {
        try{
            repoAppsRepository.save(repoApp);
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
    public boolean update(long raid, RepoApp repoApp) {
        try{
            RepoApp crepoApp = repoAppsRepository.findById(raid).get();
            crepoApp.setWebsit(repoApp.getWebsit());
            crepoApp.setSummary(repoApp.getSummary());
            crepoApp.setLicense(repoApp.getLicense());
            crepoApp.setDescription(repoApp.getDescription());
            crepoApp.setTs(new Date().getTime());
            repoAppsRepository.save(crepoApp);
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
    public boolean update(long raid, String presentation) {
        try{
            RepoApp crepoApp = repoAppsRepository.findById(raid).get();
            crepoApp.setPresentation(presentation);
            repoAppsRepository.save(crepoApp);
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
    public boolean delete(long raid) {
        try{

            repoAppsRepository.deleteById(raid);
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
    public boolean deleteRepoAppByOrg(String org){
        try{

            repoAppsRepository.deleteByOrg(org);
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
            if(keywords == null ||keywords.equals("")){
                count = repoAppsRepository.countByStorage(storage);
            }else{
                count = repoAppsRepository.countByStorageAndFilenameContaining(storage, keywords);
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
    public long count(Storage storage) {
        try{
            long count = repoAppsRepository.countByStorage(storage);
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
            long count = repoAppsRepository.count();
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
    public boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file) {
        try {
            String fileName = getFileName(md5, chunks);
            FileUtil.writeWithBlok(UploadConfig.path + "/" + md5 + "/" + name, size, file.getInputStream(), file.getSize(), chunks, chunk);
            addChunk(md5, chunk);
            if (isUploaded(md5)) {
                removeKey(md5);
            }
            return true;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAll() {
        try{
            repoAppsRepository.deleteAll();
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
