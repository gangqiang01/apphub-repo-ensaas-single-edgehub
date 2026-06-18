package com.m2m.management.service;

import com.m2m.management.entity.RepoApp;
import com.m2m.management.entity.Storage;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import java.util.List;

public interface IRepoAppService {
    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 4:26 PM
     *
     * @Param: keywords
     * @Param: currentPage
     * @Param: limit
     * @return java.util.List<com.m2m.management.entity.RepoApp>
     */
    List<RepoApp> getAllByPage(String keywords, int currentPage, int limit, Storage storage, String tenantId);
    List<RepoApp>getAll(Storage storage);
    List<RepoApp> getAllByOrg(String tenantId);

    List<RepoApp>getAll();

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:38 PM
     *
     * @Param: raid
     * @return com.m2m.management.entity.RepoApp
     */
    RepoApp get(long raid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 5/7/19 9:22 AM
     *
     * @Param: packagename
     * @Param: versionname
     * @return java.util.List<com.m2m.management.entity.RepoApp>
     */
    List<RepoApp> get(String packagename, String versionname);

    List<RepoApp> get(Storage storage, String packagename, String versionname);

    List<RepoApp> get(String packagename);

    List<RepoApp> get(String packagename, Storage storage);

    List<RepoApp> getByPackageAndFilenameContain(String packagename, String filename);

    List<RepoApp> getByPackageAndStorageAndFilenameContain(String packagename, String filename, Storage storage);

    List<RepoApp> get(String packagename, String versionname, String filename, Storage storage);


    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:39 PM
     *
     * @Param: repoApp
     * @return boolean
     */
    boolean add(RepoApp repoApp);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:39 PM
     *
     * @Param: repoapp
     * @return boolean
     */
    boolean update(long raid, RepoApp repoapp);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:39 PM
     *
     * @Param: raid
     * @return boolean
     */
    boolean update(long raid, String presentation);

    boolean delete(long raid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 4:57 PM
     *
     * @Param: versionPath
     * @Param: pkgPath
     * @Param: versionname
     * @Param: apkPath
     * @return boolean
     */
    boolean deleteRepoAppByOrg(String org);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 5/14/19 11:13 AM
     *
     * @Param:
     * @return long
     */
    long count(String keywords, Storage storage);
    long count(Storage storage);
    long count();

    boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file);
    boolean deleteAll();
}
