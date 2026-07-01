package com.m2m.management.service.impl;

import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.entity.RepoApp;
import com.m2m.management.entity.RepoExe;

import com.m2m.management.entity.Storage;
import com.m2m.management.repository.IRepoExeRepository;
import com.m2m.management.service.IRepoExeService;
import com.m2m.management.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
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

import static com.m2m.management.utils.UploadUtils.*;

/**
 * @author ：gangqiang
 * @date ：Created in 4/28/19 3:56 PM
 * @description：action repoapp db table
 */
@Service
@Slf4j
public class RepoExeService implements IRepoExeService {
    private String pathSeparate = File.separator;

    @Autowired
    private IRepoExeRepository repoExeRepository;


    @Override
    public List<RepoExe> getAllByPage(String keywords, int currentPage, int limit, Storage storage, String tenantId) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndOrgAndProjectnameContaining(storage, tenantId, keywords, pageable);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getAllByTypeAndPage(String type, String keywords, int currentPage, int limit, Storage storage, String tenantId) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndOrgAndTypeAndProjectnameContaining(storage, tenantId, type, keywords, pageable);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getAll() {
        try{
            List<RepoExe> repoExes = repoExeRepository.findAll();
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getAll(Storage storage) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByStorage(storage);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getByType(String type) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByType(type);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getByType(String type, Storage storage) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByTypeAndStorage(type, storage);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getByTypes(List<String> types) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByTypeIn(types);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getByTypes(List<String> types, Storage storage) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndTypeIn(storage, types);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoExe> getInTypesAndPage(List<String> types, String keywords, int cuttentPage, int limit, Storage storage) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(cuttentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndTypeInAndProjectnameContaining(storage, types, keywords, pageable);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoExe get(long reid) {
        try{
            Optional<RepoExe> opRepoExes = repoExeRepository.findById(reid);
            RepoExe repoApp = opRepoExes.get();
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
    public RepoExe get(String projectname, String verison, Storage storage) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndProjectnameAndVersion(storage, projectname, verison);
            if(repoExes.size() > 0){
                return repoExes.get(0);
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
    public RepoExe getByOrg(String projectname, String version, String org, Storage storage) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndProjectnameAndVersionAndOrg(storage, projectname, version, org);
            if(repoExes.size() > 0){
                return repoExes.get(0);
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
    public RepoExe get(String filename, String projectname, String version, Storage storage) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndFilenameAndProjectnameAndVersion(storage, filename, projectname, version);
            if(repoExes.size() > 0){
                return repoExes.get(0);
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
    public RepoExe get(Storage storage, String filename) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByStorageAndFilename(storage, filename);
            if(repoExes.size() > 0){
                return repoExes.get(0);
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
    public boolean add(RepoExe repoExe) {
        try{
            repoExeRepository.save(repoExe);
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
    public boolean update(long reid, RepoExe repoExe) {
        try{
            RepoExe crepoExe = repoExeRepository.findById(reid).get();
            crepoExe.setDescription(repoExe.getDescription());
            crepoExe.setProjectname(repoExe.getProjectname());
            crepoExe.setTs(new Date().getTime());
            repoExeRepository.save(crepoExe);
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
    public boolean update(long reid, String presentation) {
        try{
            RepoExe crepoExe = repoExeRepository.findById(reid).get();
            crepoExe.setPresentation(presentation);
            repoExeRepository.save(crepoExe);
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
    public boolean delete(long reid) {
        try{

            repoExeRepository.deleteById(reid);
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
    public long countByType(String type, Storage storage) {
        try{
            long count = 0;
            count = repoExeRepository.countByTypeAndStorage(type, storage);
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
    public long count(String keywords, Storage storage){
        try{
            long count = 0;
            count = repoExeRepository.countByStorageAndProjectnameContaining(storage, keywords);
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
    public long count(String type, String keywords, Storage storage) {
        try{
            long count = 0;
            count = repoExeRepository.countByStorageAndTypeAndProjectnameContaining(storage, type, keywords);
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
    public long countByTenantId(String type, String keywords, Storage storage, String tenantId) {
        try{
            long count = 0;
            count = repoExeRepository.countByStorageAndOrgAndTypeAndProjectnameContaining(storage,tenantId,  type, keywords);
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
    public long countInTypes(List<String> types, String keywords, Storage storage) {
        try{
            long count = 0;
            count = repoExeRepository.countByStorageAndTypeInAndProjectnameContaining(storage, types, keywords);
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
            long count = repoExeRepository.count();
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
            long count = repoExeRepository.countByStorage(storage);
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
    public boolean deleteRepoExe(String versionPath, String projectPath, String apkPath){
        boolean isDelDir = false;
        if(FileUtil.isOnlyChildDir(projectPath)){
            if(FileUtil.isOnlyChildApk(versionPath)){
                isDelDir = FileUtil.delDir(projectPath);
            }else{
                isDelDir = FileUtil.delFile(apkPath);
            }

        }else{
            if(FileUtil.isOnlyChildApk(versionPath)){
                isDelDir = FileUtil.delDir(versionPath);
            }else{
                isDelDir = FileUtil.delFile(apkPath);
            }
        }
        return isDelDir;
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
            repoExeRepository.deleteAll();
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
    public boolean deleteRepoExeByOrg(String org){
        try{

            repoExeRepository.deleteByOrg(org);
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
    public List<RepoExe> getAllByOrg(String tenantId) {
        try{
            List<RepoExe> repoExes = repoExeRepository.findByOrg(tenantId);
            return repoExes;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
