package com.m2m.management.service;

import com.m2m.management.entity.RepoType;
import com.m2m.management.entity.User;

import java.util.List;

public interface IUserService {
    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/29/19 9:57 AM
     *
     * @Param: keywords
     * @Param: currentPage
     * @Param: limit
     * @return java.util.List<com.m2m.management.entity.User>
     */
    List<User> getAll(String keywords, int currentPage, int limit);
    List<User> getAll();

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:19 PM
     *
     * @Param: id
     * @return com.m2m.management.entity.User
     */
    User get(long id);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 5/5/19 4:50 PM
     *
     * @Param: username
     * @return com.m2m.management.entity.User
     */
    User get(String username);

    List<User> get(int role);


    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:20 PM
     *
     * @Param: user
     * @return boolean
     */
    boolean add(User user);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:26 PM
     *
     * @Param: user
     * @return boolean
     */
    boolean update(User user);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 4/28/19 3:26 PM
     *
     * @Param: null
     * @return boolean
     */

    boolean delete(long uid);

    /**
     * create by: gangqiang
     * description: TODO
     * create time: 5/14/19 11:11 AM
     *
     * @Param:
     * @return long
     */
    long count();

    long count(String keywords);
}
