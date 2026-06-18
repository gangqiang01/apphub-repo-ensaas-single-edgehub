package com.m2m.management.service;

import com.m2m.management.entity.RepoExe;
import com.m2m.management.entity.RepoLinuxPkg;
import com.m2m.management.entity.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRepoLinuxPkgService {
    List<RepoLinuxPkg> getAllByPage(String keywords, String type, int currentPage, int limit, Storage storage, String tenantId);
    List<RepoLinuxPkg>getAll(String type);
    List<RepoLinuxPkg>getAll(String type, Storage storage);
    List<RepoLinuxPkg>getAll();
    List<RepoLinuxPkg>getAll(Storage storage);
    List<RepoLinuxPkg>getAll(List<String> types);
    List<RepoLinuxPkg>getAll(List<String> types, Storage storage);
    List<RepoLinuxPkg> getAllByPage(String keywords, int currentPage, int limit, Storage storage, String tenantId);
    List<RepoLinuxPkg> getInTypesAndPage(List<String> types, String keywords, int cuttentPage, int limit, Storage storage);


    RepoLinuxPkg get(long rlid);

    RepoLinuxPkg get(String productname, String version, String type, Storage storage);
    RepoLinuxPkg get(Storage storage, String filename);

    boolean add(RepoLinuxPkg repoLinuxPkg);

    boolean update(long rlid, RepoLinuxPkg repoLinuxPkg);

    boolean update(long rlid, String presentation);

    boolean delete(long rlid);

    boolean deleteByProductname(String name, String type);

    long count(String keywords, String type, Storage storage);
    long count();
    long count(Storage storage);
    long count(String keywords, Storage storage);
    long countByType(String type);
    long countByType(String type, Storage storage);
    long count(List<String> types);
    long countInTypes(List<String> types, String keywords, Storage storage);
    boolean deleteRepoLinuxPkg(String versionPath, String productPath, String pkgPath);
    boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file);
    boolean deleteAll();
    boolean deleteRepoPkgByOrg(String org);
    public List<RepoLinuxPkg> getAllByOrg(String tenantId);
}
