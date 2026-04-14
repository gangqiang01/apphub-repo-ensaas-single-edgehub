package com.m2m.management.controller;

import com.m2m.management.Resource.DeployResource;
import com.m2m.management.Resource.RepoResource;
import com.m2m.management.entity.RepoDocker;
import com.m2m.management.former.Response;
import com.m2m.management.service.IRepoDockerService;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @date ：Created in 5/13/20 3:55 PM
 * @description：repo docker controller
 */
@RestController
@Slf4j
public class RepoDockerController {

    private String baseRepoPath = DeployResource.BASEDEPLOYPATH+ RepoResource.TYPE;
    private String pathSeparate = File.separator;

    @Autowired
    private IRepoDockerService repoDockerService;

    @RequestMapping(value = "/repodockers", method = RequestMethod.GET)
    public ResponseEntity<List<RepoDocker>> getRepoApps() {
        List<RepoDocker> repoDockers = repoDockerService.getAll();
        long count  = repoDockerService.count();
        if(repoDockers == null){
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            return new ResponseEntity(Response.success(repoDockers, count), HttpStatus.OK);
        }
    }
}
