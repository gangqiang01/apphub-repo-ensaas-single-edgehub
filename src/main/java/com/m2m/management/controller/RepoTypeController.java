package com.m2m.management.controller;

import com.m2m.management.entity.RepoType;

import com.m2m.management.service.IRepoTypeService;
import com.m2m.management.former.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;



@RestController
@Slf4j
public class RepoTypeController {
    
    @Autowired
    private IRepoTypeService repoTypeService;

    @RequestMapping(value = "/repotype", method = RequestMethod.GET)
    public ResponseEntity<List<RepoType>> getRepoTypeType(@RequestParam(name="keywords", required = false, defaultValue ="") String keywords,
                                                      @RequestParam(name="currentpage", required = false, defaultValue ="0") int currentpage,
                                                      @RequestParam(name="limit", required = false, defaultValue ="10") int limit) {
        List<RepoType> repotypes = repoTypeService.getAll(keywords, currentpage, limit);
        if(repotypes != null){
            return new ResponseEntity(Response.success(repotypes), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/repotype/{rtid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepoType> getRepoTypeById(@PathVariable("rtid") long rtid){
        RepoType repotype = repoTypeService.get(rtid);
        if(repotype != null){
            return new ResponseEntity(Response.success(repotype), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/type/{typename}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepoType> getRepoTypeByName(@PathVariable("typename") String typename){
        RepoType repotype = repoTypeService.get(typename);
        if(repotype != null){
            return new ResponseEntity(Response.success(repotype), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/repotype", method = RequestMethod.POST)
    public ResponseEntity<Void> createRepoType(@RequestBody RepoType repotype){
        repotype.setTs(new Date().getTime());
        if(repoTypeService.add(repotype)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/repotype/{rtid}", method = RequestMethod.POST)
    public ResponseEntity<Void> updateRepoType(@PathVariable("rtid") long rtid, @RequestBody RepoType repotype){
        if(repoTypeService.update(rtid, repotype)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/repotype/{rtid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoTypeById(@PathVariable("rtid") long rtid){
        if(repoTypeService.delete(rtid)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
