package com.m2m.management.service;

import com.m2m.management.entity.Repo;
import com.m2m.management.entity.RepoApp;

import java.util.List;

public interface IRepoService {

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/29/19 9:11 AM
     *
     * @Param: keywords
     * @Param: currentPage
     * @Param: limit
     * @return java.util.List<com.m2m.management.entity.Repo>
     */
    List<Repo> getAll(String keywords, int currentPage, int limit);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/29/19 9:17 AM
     *
     * @Param: reponame
     * @return com.m2m.management.entity.Repo
     */
    Repo get(String reponame);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:34 PM
     *
     * @Param: rid
     * @return Repo
     */

    Repo get(long rid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:35 PM
     *
     * @Param: repo
     * @return boolean
     */
    boolean add(Repo repo);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:35 PM
     *
     * @Param: repo
     * @return boolean
     */
    boolean update(long rid, Repo repo);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:35 PM
     *
     * @Param: rid
     * @return boolean
     */
    boolean delete(long rid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 5/14/19 11:13 AM
     *
     * @Param:
     * @return long
     */
    long count();

    long count(String keywords);



}
