package com.m2m.management.service.impl;

import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.entity.RepoExe;
import com.m2m.management.entity.RepoLinuxPkg;
import com.m2m.management.entity.Storage;
import com.m2m.management.repository.IRepoLinuxPkgRepository;
import com.m2m.management.service.IRepoLinuxPkgService;
import com.m2m.management.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.m2m.management.utils.UploadUtils.*;

/**
 * @date ：Created in 8/17/20 2:26 PM
 * @description：repo linux package service
 */
@Slf4j
@Service
public class RepoLinuxPkgService implements IRepoLinuxPkgService {
    @Autowired
    private IRepoLinuxPkgRepository repoLinuxPkgRepository;

    @Override
    public List<RepoLinuxPkg> getAllByPage(String keywords, String type, int currentPage, int limit, Storage storage, String tenantId) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorageAndOrgAndTypeAndProductnameContaining(storage, tenantId, type, keywords, pageable);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getAll(String type) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByType(type);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getAll(String type, Storage storage) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByTypeAndStorage(type, storage);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getAll() {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findAll();
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getAll(Storage storage) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorage(storage);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getAll(List<String> types) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByTypeIn(types);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getAll(List<String> types, Storage storage) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorageAndTypeIn(storage, types);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getAllByPage(String keywords, int currentPage, int limit, Storage storage, String tenantId) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorageAndOrgAndProductnameContaining(storage,tenantId, keywords, pageable);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoLinuxPkg> getInTypesAndPage(List<String> types, String keywords, int cuttentPage, int limit, Storage storage) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(cuttentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorageAndTypeInAndProductnameContaining(storage, types, keywords, pageable);
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoLinuxPkg get(long rlid) {
        try{
            Optional<RepoLinuxPkg> opRepoLinuxPkgs = repoLinuxPkgRepository.findById(rlid);
            RepoLinuxPkg repoLinuxPkg = opRepoLinuxPkgs.get();
            return repoLinuxPkg;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public RepoLinuxPkg get(String productname, String version, String type, Storage storage) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorageAndProductnameAndVersionAndType(storage, productname, version, type);
            if(repoLinuxPkg.size() > 0){
                return repoLinuxPkg.get(0);
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
    public RepoLinuxPkg getByTenantId(String productname, String version, String type, String tenantId, Storage storage) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorageAndProductnameAndVersionAndTypeAndOrg(storage, productname, version, type, tenantId);
            if(repoLinuxPkg.size() > 0){
                return repoLinuxPkg.get(0);
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
    public RepoLinuxPkg get(Storage storage, String filename) {
        try{
            List<RepoLinuxPkg> repoLinuxPkg = repoLinuxPkgRepository.findByStorageAndFilename(storage, filename);
            if(repoLinuxPkg.size() > 0){
                return repoLinuxPkg.get(0);
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
    public boolean add(RepoLinuxPkg repoLinuxPkg) {
        try{
            repoLinuxPkgRepository.save(repoLinuxPkg);
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
    public boolean update(long rlid, RepoLinuxPkg repoLinuxPkg) {
        try{
            RepoLinuxPkg crepoLinuxPkg = repoLinuxPkgRepository.findById(rlid).get();
            crepoLinuxPkg.setDescription(repoLinuxPkg.getDescription());
            crepoLinuxPkg.setProductname(repoLinuxPkg.getProductname());
            crepoLinuxPkg.setTs(new Date().getTime());
            repoLinuxPkgRepository.save(crepoLinuxPkg);
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
    public boolean update(long rlid, String presentation) {
        try{
            RepoLinuxPkg crepoLinuxPkg = repoLinuxPkgRepository.findById(rlid).get();
            crepoLinuxPkg.setPresentation(presentation);
            repoLinuxPkgRepository.save(crepoLinuxPkg);
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
    public boolean delete(long rlid) {
        try{

            repoLinuxPkgRepository.deleteById(rlid);
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
    public boolean deleteByProductname(String productname, String type){
        try{

            repoLinuxPkgRepository.deleteByProductnameAndType(productname, type);
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
    public long count(String keywords, String type, Storage storage){
        try{
            long count = 0;
            count = repoLinuxPkgRepository.countByStorageAndTypeAndProductnameContaining(storage, type, keywords);
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
            count = repoLinuxPkgRepository.countByStorageAndOrgAndTypeAndProductnameContaining(storage, tenantId, type, keywords);
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
            long count = 0;
            count = repoLinuxPkgRepository.count();
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
            long count = 0;
            count = repoLinuxPkgRepository.countByStorage(storage);
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
    public long countByType(String type) {
        try{
            long count = repoLinuxPkgRepository.countByType(type);
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
            long count = repoLinuxPkgRepository.countByTypeAndStorage(type, storage);
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
    public long count(String keywords, Storage storage) {
        try{
            long count = repoLinuxPkgRepository.countByStorageAndProductnameContaining(storage, keywords);
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
    public long count(List<String> types) {
        try{
            long count = repoLinuxPkgRepository.countByTypeIn(types);
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
            long count = repoLinuxPkgRepository.countByStorageAndTypeInAndProductnameContaining(storage, types, keywords);
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
    public boolean deleteRepoLinuxPkg(String versionPath, String productPath, String pkgPath){
        boolean isDelDir = false;
        if(FileUtil.isOnlyChildDir(productPath)){
            if(FileUtil.isOnlyChildApk(versionPath)){
                isDelDir = FileUtil.delDir(productPath);
            }else{
                isDelDir = FileUtil.delFile(pkgPath);
            }

        }else{
            if(FileUtil.isOnlyChildApk(versionPath)){
                isDelDir = FileUtil.delDir(versionPath);
            }else{
                isDelDir = FileUtil.delFile(pkgPath);
            }
        }
        return isDelDir;
    }

    @Override
    public boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file) {
        try {
            getFileName(md5, chunks);
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
            repoLinuxPkgRepository.deleteAll();
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
    public boolean deleteRepoPkgByOrg(String org){
        try{
            repoLinuxPkgRepository.deleteByOrg(org);
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
    public List<RepoLinuxPkg> getAllByOrg(String tenantId) {
        try{
            List<RepoLinuxPkg> repoLinuxPkgs = repoLinuxPkgRepository.findByOrg(tenantId);
            return repoLinuxPkgs;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
