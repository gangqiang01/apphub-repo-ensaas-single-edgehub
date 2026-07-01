package com.m2m.management.service;

import com.m2m.management.entity.RepoExe;
import com.m2m.management.entity.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRepoExeService {

    List<RepoExe> getAllByPage(String keywords, int currentPage, int limit, Storage storage, String tenantId);
    List<RepoExe> getAllByTypeAndPage(String type, String keywords, int currentPage, int limit, Storage storage, String tenantId);
    List<RepoExe> getAll();
    List<RepoExe> getAll(Storage storage);
    List<RepoExe> getByType(String type);
    List<RepoExe> getByType(String type, Storage storage);
    List<RepoExe> getByTypes(List<String> types);
    List<RepoExe> getByTypes(List<String> types, Storage storage);
    List<RepoExe> getInTypesAndPage(List<String> types, String keywords, int cuttentPage, int limit, Storage storage);
    RepoExe get(long reid);


    RepoExe get(String projectname, String version, Storage storage);
    RepoExe getByOrg(String projectname, String version,String org, Storage storage);
    RepoExe get(String filename, String projectname, String version, Storage storage);
    RepoExe get(Storage storage, String filename);


    boolean add(RepoExe repoExe);

    boolean update(long reid, RepoExe repoexe);

    boolean update(long reid, String presentation);

    boolean delete(long reid);


    long count(String keywords, Storage storage);
    long count(String type, String keywords, Storage storage);
    long countByTenantId(String type, String keywords, Storage storage, String tenantId);
    long countInTypes(List<String> types, String keywords, Storage storage);
    long count();
    long countByType(String type, Storage storage);
    long count(Storage storage);
    boolean deleteRepoExe(String versionPath, String projectPath, String apkPath);
    boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file);
    boolean deleteAll();
    boolean deleteRepoExeByOrg(String org);
    List<RepoExe>  getAllByOrg(String org);
}
