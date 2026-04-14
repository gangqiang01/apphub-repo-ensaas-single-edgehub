package com.m2m.management.service;

import com.m2m.management.entity.Repo;
import com.m2m.management.entity.RepoType;

import java.util.List;

public interface IRepoTypeService {

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/29/19 9:42 AM
     *
     * @Param: keywords
     * @Param: currentPage
     * @Param: limit
     * @return java.util.List<com.m2m.management.entity.RepoType>
     */
    List<RepoType> getAll(String keywords, int currentPage, int limit);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:27 PM
     *
     * @Param: rtid
     * @return com.m2m.management.entity.RepoType
     */
    RepoType get(long rtid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/29/19 10:01 AM
     *
     * @Param: typeName
     * @return com.m2m.management.entity.RepoType
     */

    RepoType get(String typeName);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:28 PM
     *
     * @Param: repoType
     * @return boolean
     */
    boolean add(RepoType repoType);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:28 PM
     *
     * @Param: repoType
     * @return boolean
     */
    boolean update(long rtid, RepoType repoType);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:29 PM
     *
     * @Param: rtid
     * @return boolean
     */
    boolean delete(long rtid);


}
