package com.m2m.management.controller;

import com.m2m.management.Resource.DeployResource;
import com.m2m.management.Resource.RepoResource;
import com.m2m.management.entity.Repo;
import com.m2m.management.entity.RepoType;
import com.m2m.management.entity.User;
import com.m2m.management.service.IRepoService;
import com.m2m.management.service.IRepoTypeService;
import com.m2m.management.service.IUserService;

import com.m2m.management.utils.FileUtil;
import com.m2m.management.former.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class RepoController {

    @Autowired
    private IRepoService repoService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRepoTypeService repoTypeService;

    @RequestMapping(value = "/repo", method = RequestMethod.GET)
    public ResponseEntity<List<Repo>> getRepo(
            @RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
            @RequestParam(name="currentpage", required = false, defaultValue ="1") int currentpage,
            @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {
        List<Repo> repos = repoService.getAll(keywords, currentpage-1, limit);
        long count  = repoService.count(keywords);
        if(repos != null){
            return new ResponseEntity(Response.success(repos, count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repo/count", method = RequestMethod.GET)
    public ResponseEntity<Object> count() {
        long count = repoService.count();
        if(count != 0){
            return new ResponseEntity(Response.success(count), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Repo> getRepoById(@PathVariable("id") long id){
        Repo repo = repoService.get(id);
        if(repo != null){
            return new ResponseEntity(Response.success(repo), HttpStatus.OK);
        }else {
            return new ResponseEntity(Response.error("Repo is not found in db"), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/repo/{reponame}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Repo> getRepoByName(@PathVariable("reponame") String reponame){
        Repo repo = repoService.get(reponame);
        if(repo != null){
            return new ResponseEntity(Response.success(repo), HttpStatus.OK);
        }else {
            return new ResponseEntity(Response.error("Server error"), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/repo", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepo(@RequestBody Repo repo){
        Boolean isCreatApkRepo = false;
        String darkname = DigestUtils.md5DigestAsHex(repo.getReponame().getBytes());
        long uid = repo.getUid();
        long rtid = repo.getRtid();
        User u = userService.get(uid);
        RepoType repoType = repoTypeService.get(rtid);
        repo.setUser(u);
        repo.setRepoType(repoType);
        repo.setDarkname(darkname);
        repo.setTs(new Date().getTime());
        if(repoService.add(repo)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Repo name and repo type must be unique"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/repo/{rid}", method = RequestMethod.POST)
    public ResponseEntity<Void> updateRepo(@PathVariable("rid") long rid, @RequestBody Repo repo){
        if(repoService.update(rid, repo)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else {
            return new ResponseEntity(Response.error("Server error"), HttpStatus.NOT_FOUND);
        }
    }
    @RequestMapping(value = "/repo/{rid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoById(@PathVariable("rid") long rid){
        Repo repo = repoService.get(rid);
        if(repo == null){
            return new ResponseEntity(Response.error("Repo is not found in db"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
         if(repoService.delete(rid)){
             return new ResponseEntity(Response.success(), HttpStatus.OK);
         }else{
             return new ResponseEntity(Response.error("Delete repo in db error"), HttpStatus.INTERNAL_SERVER_ERROR);
         }

    }


}
