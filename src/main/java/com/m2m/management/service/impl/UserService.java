/**
 * @author ：gangqiang
 * @date ：Created in 4/28/19 3:46 PM
 * @description：get user class
 * @modified By：
 * @version: 1.0$
 */
package com.m2m.management.service.impl;

import com.m2m.management.entity.User;
import com.m2m.management.repository.IUserRepository;
import com.m2m.management.service.IUserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author ：gangqiang
 * @date ：Created in 4/28/19 3:54 PM
 * @description：action user db table
 */
@Service
@Slf4j
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public List<User> getAll(String keywords, int currentPage, int limit) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.ASC, "ts");
            List<User> users = userRepository.findByNameContaining(keywords, pageable);
            return users;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<User> getAll() {
        try{

            List<User> users = userRepository.findAll();
            return users;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User get(long uid) {
        try{
            Optional<User> opuser = userRepository.findById(uid);
            User user = opuser.get();
            return user;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User get(String username) {
        try{
            User user = userRepository.findByName(username);
            return user;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> get(int role) {
        try{
            List<User> users = userRepository.findByRole(role);
            return users;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(User user) {
        try{
            userRepository.save(user);
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
    public boolean update(User user) {
        try{
            user.setTs(new Date().getTime());
            userRepository.save(user);
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
    public boolean delete(long uid) {
        try{
            userRepository.deleteById(uid);
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
    public long count() {
        try{
            long count = userRepository.count();
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
    public long count(String keywords) {
        long count = 0;
        try{
            if(keywords == "" || keywords == null){
                count = userRepository.count();
            }else {
                count = userRepository.countByNameContaining(keywords);
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
}
