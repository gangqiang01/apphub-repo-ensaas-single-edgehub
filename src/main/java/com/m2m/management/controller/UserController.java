package com.m2m.management.controller;

import com.alibaba.fastjson.JSONObject;
import com.m2m.management.configuration.CORSFilter;
import com.m2m.management.entity.User;
import com.m2m.management.restful.SsoService;
import com.m2m.management.service.*;
import com.m2m.management.former.Response;
import com.m2m.management.utils.S3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;



@RestController
@Slf4j
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    IRepoFileService repoFileService;

    @Autowired
    IRepoBspService repoBspService;

    @Autowired
    IRepoAppService repoAppService;

    @Autowired
    IRepoLinuxPkgService repoLinuxPkgService;

    @Autowired
    IRepoDockerComposeService repoDockerComposeService;

    @Autowired
    IRepoExeService repoExeService;

    @Autowired
    IRepoDockerService repoDockerService;


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUsers(
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {
        List<User> users = userService.getAll(keywords, currentpage-1, limit);
        long count  = userService.count(keywords);
        if(users != null){
            return new ResponseEntity(Response.success(users, count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/byname", method = RequestMethod.GET)
    public ResponseEntity<User> getUserByName(@RequestParam(name = "username") String username) {
        User user = userService.get(username);
        if(user != null){
            return new ResponseEntity(Response.success(user), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/count", method = RequestMethod.GET)
    public ResponseEntity<Object> count() {
        long count = userService.count();
        if(count != 0){
            return new ResponseEntity(Response.success(count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User user = userService.get(id);
        if(user != null){
            return new ResponseEntity(Response.success(user), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        log.info("Creating User " + user.getName());
        user.setPasswd(DigestUtils.md5DigestAsHex(user.getPasswd().getBytes()));
        user.setTs(new Date().getTime());
        if(userService.add(user)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/user/byname", method = RequestMethod.POST)
    public ResponseEntity<Void> updateUser( @RequestBody JSONObject json) {
        String name = json.getString("username");
        String role = json.getString("role");
        if(name == null || name.equals("")  || role == null || role.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = userService.get(name);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        user.setRole(Integer.valueOf(role));
        if(userService.update(user)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser( @RequestBody User user) {
        if(userService.update(user)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long uid, HttpServletRequest req) {

        User user = userService.get(uid);
        if(user == null){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SsoService ssoService = new SsoService();
        String token = req.getHeader(CORSFilter.headerName);
        String createInfo = ssoService.removeUserScope(user.getName(), token);
        JSONObject createInfoObject = JSONObject.parseObject(createInfo);
        if(createInfo == null|| createInfoObject.containsKey("error")){
            return new ResponseEntity(Response.error("Delete user error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        boolean isSuccess = userService.delete(uid);
        if(isSuccess){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Delete user error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/user/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAll(){
        S3Client.clearInstance();
        repoAppService.deleteAll();
        repoBspService.deleteAll();
        repoFileService.deleteAll();
        repoDockerComposeService.deleteAll();
        repoLinuxPkgService.deleteAll();
        repoExeService.deleteAll();
        repoDockerService.deleteAll();
        return new ResponseEntity(Response.success(), HttpStatus.OK);
    }

}
