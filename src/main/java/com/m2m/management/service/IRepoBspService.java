package com.m2m.management.service;

import com.m2m.management.entity.RepoBsp;
import com.m2m.management.entity.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRepoBspService {
    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 4:29 PM
     *
     * @Param: keywords
     * @Param: currentPage
     * @Param: limit
     * @return java.util.List<com.m2m.management.entity.RepoBsp>
     */
    List<RepoBsp> getAllByPage(String keywords, int currentPage, int limit, Storage storage);
    List<RepoBsp> getAllByOsAndPage(String os, String keywords, int currentPage, int limit, Storage storage);
    List<RepoBsp> getAll();
    List<RepoBsp> getAll(Storage storage);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 5:22 PM
     *
     * @Param: boardname
     * @return java.util.List<com.m2m.management.entity.RepoBsp>
     */
    List<RepoBsp> get(String boardname);

    List<RepoBsp> get(String boardname, Storage storage);

    List<RepoBsp> getByOs(String os);

    List<RepoBsp> getByOs(String os, Storage storage);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 5/7/19 9:21 AM
     *
     * @Param: boardname
     * @Param: versionname
     * @return java.util.List<com.m2m.management.entity.RepoBsp>
     */
    List<RepoBsp> get(String boardname, String versionname, Storage storage);
    List<RepoBsp> get(String boardname, String versionname, Storage storage, String os);


    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:36 PM
     *
     * @Param: rbid
     * @return RepoBsp
     */
    RepoBsp get(long rbid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:37 PM
     *
     * @Param: repoBsp
     * @return boolean
     */
    boolean add(RepoBsp repoBsp);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:37 PM
     *
     * @Param: repoBsp
     * @return boolean
     */
    boolean update(long rbid, RepoBsp repoBsp);

    boolean update(long rbid, String presentation);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:38 PM
     *
     * @Param: rbid
     * @return boolean
     */
    boolean delete(long rbid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 5:13 PM
     *
     * @Param: boardPath
     * @Param: versionname
     * @return boolean
     */
    boolean deleteRepoBsp(String boardPath, String versionname);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 5/14/19 11:13 AM
     *
     * @Param:
     * @return long
     */
    long count();
    long count(Storage storage);
    long count(String keywords, Storage storage);
    long count(String os, String keywords, Storage storage);
    long countByOs(String os, Storage storage);
    boolean uploadWithBlock(String tname, String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file);
    boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file);
    boolean deleteAll();
}
