package com.m2m.management.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.StringUtils;
import com.m2m.management.configuration.CORSFilter;
import com.m2m.management.constant.UserRole;
import com.m2m.management.former.Response;
import com.m2m.management.restful.SsoService;
import com.m2m.management.service.IUserService;
import com.m2m.management.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @date ：Created in 4/12/21 2:45 PM
 * @description：sso controller
 */

@RestController
@Slf4j
public class SsoController {
    @Autowired
    IUserService userService;

    SsoService ssoService = new SsoService();
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ResponseEntity<Void> post_1_createUser(@RequestBody JSONObject jsonObject, HttpServletRequest req){
        String name = jsonObject.getString("name");
        String role = jsonObject.getString("role");
        if(StringUtils.isNullOrEmpty(name)|| StringUtils.isNullOrEmpty(role)){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = userService.get(name);
        if(user != null){
            return new ResponseEntity(Response.error("User already exist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String token = req.getHeader(CORSFilter.headerName);
        String createInfo = ssoService.updateUserScope(name, token);
        JSONObject createInfoObject = JSONObject.parseObject(createInfo);
        if(createInfo == null|| createInfoObject.containsKey("error")){
            return new ResponseEntity(Response.error("Create sso user error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User nuser = new User();
        nuser.setName(name);
        nuser.setRole(Integer.valueOf(role));
        nuser.setTs(new Date().getTime());


        if(userService.add(nuser)){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Create user error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reSendEmail", method = RequestMethod.POST)
    public ResponseEntity<Void>  post_1_reSendEmail(@RequestBody JSONObject jsonObject, HttpServletRequest req){
        String email = jsonObject.getString("email");
        if(email == null || email.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SsoService ssoService = new SsoService();
        try{
            String resultJson = ssoService.reSendEmail(req.getHeader(CORSFilter.headerName), email);
            if(resultJson != null){
                JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
                if(!resultJsonObject.containsKey("error")){
                    return new ResponseEntity(Response.success(), HttpStatus.OK);
                }else{
                    log.error("[ssoService.reSendEmail]"+resultJson);
                }
            }else{
                log.error("[ssoService.reSendEmail]"+resultJson);
            }
            return new ResponseEntity(Response.error("Resend Eamil error"), HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(Response.error("Resend Eamil error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public ResponseEntity<Void>  post_1_forgetPassword(@RequestBody JSONObject jsonObject, HttpServletRequest req){
        String email = jsonObject.getString("email");
        if(email == null || email.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SsoService ssoService = new SsoService();
        try{
            String resultJson = ssoService.forgetPassword(req.getHeader(CORSFilter.headerName), email);
            if(resultJson != null){
                JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
                if(!resultJsonObject.containsKey("error")){
                    return new ResponseEntity(Response.success(), HttpStatus.OK);
                }else{
                    log.error("[ssoService.forgetPassword]"+resultJson);
                }
            }else{
                log.error("[ssoService.forgetPassword]"+resultJson);
            }
            return new ResponseEntity(Response.error("Send email error"), HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(Response.error("Send email error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ResponseEntity<Void>  post_1_resetPassword(@RequestBody JSONObject jsonObject, HttpServletRequest req){
        String email = jsonObject.getString("email");
        String acvitationCode = jsonObject.getString("activationCode");
        String newPassword = jsonObject.getString("newPassword");
        String confirmNewPassword = jsonObject.getString("confirmNewPassword");
        String mode = jsonObject.getString("mode");
        if(email == null || email.equals("")||
                acvitationCode == null || acvitationCode.equals("")||
                newPassword ==  null || newPassword.equals("")||
                confirmNewPassword == null || confirmNewPassword.equals("")||
                mode == null|| mode.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(!newPassword.equals(confirmNewPassword)){
            return new ResponseEntity(Response.error("New password and confirmation password are inconsistent"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SsoService ssoService = new SsoService();
        try{
            String resultJson = ssoService.resetPassword(req.getHeader(CORSFilter.headerName), email, acvitationCode, newPassword, mode);
            JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
            if(resultJson == null|| resultJsonObject.containsKey("error")){
                return new ResponseEntity(Response.error("Reset Password error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(Response.error("Reset Password error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public ResponseEntity<Void> put_1_resetPasswd(@RequestBody JSONObject jsonObject, HttpServletRequest req){
        String name = jsonObject.getString("name");
        String oldpasswd = jsonObject.getString("oldpasswd");
        String newpasswd = jsonObject.getString("newpasswd");
        if(name == null || name.equals("") || oldpasswd == null || oldpasswd.equals("") || newpasswd == null || newpasswd.equals("")){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SsoService ssoService = new SsoService();
        try{
            // change pwd in wispaas sso
            JSONObject ssoChangePwdBody = new JSONObject();
            ssoChangePwdBody.put("username", name);
            ssoChangePwdBody.put("new_password", newpasswd);
            ssoChangePwdBody.put("password", oldpasswd);
            String resultJson = ssoService.changePassword(req.getHeader(CORSFilter.headerName), ssoChangePwdBody);
            if(resultJson == null || JSONObject.parseObject(resultJson).containsKey("error")){
                return new ResponseEntity(Response.error("Change sso password error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }catch (Exception e){
            log.error("[ssoService.CreateUser]"+e.getMessage());
            return new ResponseEntity(Response.error("Change sso password error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/byname/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRepoAppsById(@PathVariable("name") String name, HttpServletRequest req){
        User user = userService.get(name);
        if(user == null){
            return new ResponseEntity(Response.error("Parameter error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        long uid = user.getUid();
        SsoService ssoService = new SsoService();
        String token = req.getHeader(CORSFilter.headerName);
        String createInfo = ssoService.removeUserScope(name, token);
        JSONObject createInfoObject = JSONObject.parseObject(createInfo);
        if(createInfo == null|| createInfoObject.containsKey("error")){
            return new ResponseEntity(Response.error("Delete user error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JSONObject result = new JSONObject();
        boolean isSuccess = userService.delete(uid);
        if(isSuccess){
            return new ResponseEntity(Response.success(), HttpStatus.OK);
        }else{
            return new ResponseEntity(Response.error("Delete user error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



}
