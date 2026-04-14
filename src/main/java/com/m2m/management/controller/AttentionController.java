package com.m2m.management.controller;

import antlr.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.m2m.management.entity.*;
import com.m2m.management.former.Response;
import com.m2m.management.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
public class AttentionController {

    @Autowired
    private IRepoAppService repoAppService;

    @Autowired
    private IRepoBspService repoBspService;

    @Autowired
    private IRepoLinuxPkgService repoLinuxPkgService;

    @Autowired
    private IRepoExeService repoExeService;

    @Autowired
    private IRepoDockerComposeService repoDockerComposeService;

    @Autowired
    private IRepoFileService repoFileService;

    @Autowired
    private IAttentionService attentionService;

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/attention/getApps", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getApps(
            @RequestParam(name="type") String type,
            @RequestParam(name="username") String username) {
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<RepoApp> repoApps = repoAppService.getAll();
        List<Attention> attentions = attentionService.getAttentionByTypeAndUser(type, user);
        LinkedHashSet<Object> result = new LinkedHashSet<>();
        if(repoApps != null && repoApps.size() > 0){
            for(int i = 0; i < repoApps.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", repoApps.get(i).getPkgname());
                object.put("filename", "");
                boolean flag = true;
                if(attentions != null && attentions.size() > 0){
                    for(int j = 0; j <attentions.size(); j ++){
                        if(repoApps.get(i).getPkgname().equals(attentions.get(j).getName())){
                            flag = false;
                            break;
                        }else{
                            object.put("isAttention", false);
                        }
                    }
                }else {
                    object.put("isAttention", false);
                }
                if(flag){
                    result.add(object);
                }
            }
        }
        if(attentions != null && attentions.size() > 0){
            for(int i = 0; i <attentions.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", attentions.get(i).getName());
                object.put("isAttention", true);
                object.put("filename", attentions.get(i).getFilename());
                result.add(object);
            }
        }
        LinkedHashSet<Object> res = new LinkedHashSet<>();
        Iterator<Object> it = result.iterator();
        while(it.hasNext()){
            JSONObject object = (JSONObject)it.next();
            for(int j = 0; j < repoApps.size(); j++){
                if (object.get("name").equals(repoApps.get(j).getPkgname())){
                    if(object.get("filename").equals("")){
                        object.put("filename", repoApps.get(j).getFilename());
                        break;
                    }
                }
            }
            res.add(object);
        }

        return new ResponseEntity(Response.success(res, res.size()), HttpStatus.OK);
    }

    @RequestMapping(value = "/attention/getOs", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getAndroidOs(
            @RequestParam(name="type") String type,
            @RequestParam(name="username") String username) {
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<RepoBsp> repoBsps = repoBspService.getByOs(type);

        List<Attention> attentions = attentionService.getAttentionByTypeAndUser(type, user);
        LinkedHashSet<Object> result = new LinkedHashSet<>();
        if(repoBsps != null && repoBsps.size() > 0){
            for(int i = 0; i < repoBsps.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", repoBsps.get(i).getBoardname());
                if(attentions != null && attentions.size() > 0){
                    for(int j = 0; j <attentions.size(); j ++){
                        if(repoBsps.get(i).getBoardname().equals(attentions.get(j).getName())){
                            object.put("isAttention", true);
                            break;
                        }else{
                            object.put("isAttention", false);
                        }

                    }
                }else {
                    object.put("isAttention", false);
                }
                result.add(object);
            }
        }
        if(attentions != null && attentions.size() > 0){
            for(int i = 0; i <attentions.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", attentions.get(i).getName());
                object.put("isAttention", true);
                result.add(object);
            }
        }
        return new ResponseEntity(Response.success(result, result.size()), HttpStatus.OK);
    }

    @RequestMapping(value = "/attention/getLinuxPkg", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getLinuxPkg(
            @RequestParam(name="type") String type,
            @RequestParam(name="username") String username) {
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<RepoLinuxPkg> repoLinuxPkgs = repoLinuxPkgService.getAll(type);

        List<Attention> attentions = attentionService.getAttentionByTypeAndUser(type, user);
        LinkedHashSet<Object> result = new LinkedHashSet<>();
        if(repoLinuxPkgs != null && repoLinuxPkgs.size() > 0){
            for(int i = 0; i < repoLinuxPkgs.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", repoLinuxPkgs.get(i).getProductname());
                if(attentions != null && attentions.size() > 0){
                    for(int j = 0; j <attentions.size(); j ++){
                        if(repoLinuxPkgs.get(i).getProductname().equals(attentions.get(j).getName())){
                            object.put("isAttention", true);
                            break;
                        }else{
                            object.put("isAttention", false);
                        }

                    }
                }else {
                    object.put("isAttention", false);
                }
                result.add(object);
            }
        }
        if(attentions != null && attentions.size() > 0){
            for(int i = 0; i <attentions.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", attentions.get(i).getName());
                object.put("isAttention", true);
                result.add(object);
            }
        }
        return new ResponseEntity(Response.success(result, result.size()), HttpStatus.OK);
    }

    @RequestMapping(value = "/attention/getWindowsPkg", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getWindowsPkg(
            @RequestParam(name="type") String type,
            @RequestParam(name="username") String username) {
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<RepoExe> repoExes = repoExeService.getByType(type);
        List<Attention> attentions = attentionService.getAttentionByTypeAndUser(type, user);
        LinkedHashSet<Object> result = new LinkedHashSet<>();
        if(repoExes != null && repoExes.size() > 0){
            for(int i = 0; i < repoExes.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", repoExes.get(i).getProjectname());
                if(attentions != null && attentions.size() > 0){
                    for(int j = 0; j <attentions.size(); j ++){
                        if(repoExes.get(i).getProjectname().equals(attentions.get(j).getName())){
                            object.put("isAttention", true);
                            break;
                        }else{
                            object.put("isAttention", false);
                        }

                    }
                }else {
                    object.put("isAttention", false);
                }
                result.add(object);
            }
        }
        if(attentions != null && attentions.size() > 0){
            for(int i = 0; i <attentions.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", attentions.get(i).getName());
                object.put("isAttention", true);
                result.add(object);
            }
        }
        return new ResponseEntity(Response.success(result, result.size()), HttpStatus.OK);
    }

    @RequestMapping(value = "/attention/getDocker", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getDocker(
            @RequestParam(name="type") String type,
            @RequestParam(name="username") String username) {
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<RepoDockerCompose> repoDockerComposes = repoDockerComposeService.getByType(type);
        List<Attention> attentions = attentionService.getAttentionByTypeAndUser(type, user);
        LinkedHashSet<Object> result = new LinkedHashSet<>();
        if(repoDockerComposes != null && repoDockerComposes.size() > 0){
            for(int i = 0; i < repoDockerComposes.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", repoDockerComposes.get(i).getTag());
                if(attentions != null && attentions.size() > 0){
                    for(int j = 0; j <attentions.size(); j ++){
                        if(repoDockerComposes.get(i).getTag().equals(attentions.get(j).getName())){
                            object.put("isAttention", true);
                            break;
                        }else{
                            object.put("isAttention", false);
                        }

                    }
                }else {
                    object.put("isAttention", false);
                }
                result.add(object);
            }
        }
        if(attentions != null && attentions.size() > 0){
            for(int i = 0; i <attentions.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", attentions.get(i).getName());
                object.put("isAttention", true);
                result.add(object);
            }
        }
        return new ResponseEntity(Response.success(result, result.size()), HttpStatus.OK);
    }

    @RequestMapping(value = "/attention/getFile", method = RequestMethod.GET)
    public ResponseEntity<List<RepoApp>> getFile(
            @RequestParam(name="type") String type,
            @RequestParam(name="username") String username) {
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<RepoFile> repoFiles = repoFileService.get(type);

        List<Attention> attentions = attentionService.getAttentionByTypeAndUser(type, user);
        LinkedHashSet<Object> result = new LinkedHashSet<>();
        if(repoFiles != null && repoFiles.size() > 0){
            for(int i = 0; i < repoFiles.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", repoFiles.get(i).getFilename());
                if(attentions != null && attentions.size() > 0){
                    for(int j = 0; j <attentions.size(); j ++){
                        if(repoFiles.get(i).getFilename().equals(attentions.get(j).getName())){
                            object.put("isAttention", true);
                            break;
                        }else{
                            object.put("isAttention", false);
                        }

                    }
                }else {
                    object.put("isAttention", false);
                }
                result.add(object);
            }
        }
        if(attentions != null && attentions.size() > 0){
            for(int i = 0; i <attentions.size(); i ++){
                JSONObject object = new JSONObject();
                object.put("name", attentions.get(i).getName());
                object.put("isAttention", true);
                result.add(object);
            }
        }
        return new ResponseEntity(Response.success(result, result.size()), HttpStatus.OK);
    }

    @RequestMapping(value = "/addAttention", method = RequestMethod.POST)
    public ResponseEntity<Void> createAttention(@RequestBody JSONObject json) throws Exception {
        String name = json.getString("name");
        String isAttention = json.getString("isAttention");
        String username = json.getString("username");
        String type = json.getString("type");
        String filename = json.getString("filename");
        if(name == null || name.equals("") || isAttention == null || isAttention.equals("") || username == null || username.equals("")
        || type ==null || type.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Attention attention = new Attention();
        if (type.equals("app")){
            attention.setFilename(filename);
        }
        attention.setName(name);
        attention.setType(type);
        attention.setUser(user);
        List<Attention> attentions = attentionService.getAttentionByNameAndTypeAndUser(name, type, user);
        if(attentions != null && attentions.size()>0){
            return new ResponseEntity(Response.error("Attention already is exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(attentionService.add(attention)){
            return new ResponseEntity(Response.success(attention), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Insert attention to db error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/deleteAttention", method = RequestMethod.POST)
    public ResponseEntity<Void> deleteAttention(@RequestBody JSONObject json) {
        String name = json.getString("name");
        String isAttention = json.getString("isAttention");
        String username = json.getString("username");
        String type = json.getString("type");
        if(name == null || name.equals("") || isAttention == null || isAttention.equals("") || username == null || username.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User user = userService.get(username);
        if (user == null){
            return new ResponseEntity(Response.error("User name error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Attention> attentions = attentionService.getAttentionByNameAndTypeAndUser(name, type, user);
        if(attentions != null && attentions.size()>0){
            if(attentionService.deleteByAid(attentions.get(0).getAid())){
                return new ResponseEntity(Response.success(), HttpStatus.OK);
            }else{
                return new ResponseEntity(Response.error("Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity(Response.error("Attention is not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}