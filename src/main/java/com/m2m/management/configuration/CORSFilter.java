package com.m2m.management.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2m.management.restful.SsoService;
import com.m2m.management.former.Response;

import com.m2m.management.service.impl.UserService;
import com.m2m.management.utils.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;

//@Component
@WebFilter(filterName = "CORSFilter")
@Slf4j
public class CORSFilter implements Filter {
    @Autowired
    private ObjectMapper mObjectMapper;

    @Autowired
    UserService userService;

    public static final String headerName = "accesstoken";
    public static final String srpTokenName = "Srptoken";
    private final String SEPARATOR = "#";
    private final String SRPNAME = "astore";
    private final long TIMEOUT = 60;//1min
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String token = request.getHeader(headerName);
        String srpToken = request.getHeader(srpTokenName);
        boolean isFilter = false;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers",
                "x-requested-with, " +
                        "Content-Type, accesstoken, Authorization,timeout");
//        log.info(request.getRequestURI());
        if(
            request.getMethod().equals("OPTIONS")||
            request.getRequestURI().equals("/login")||
            request.getRequestURI().startsWith("/static")||
            request.getRequestURI().startsWith("/getAppIcon")||
            request.getRequestURI().startsWith("/getExeIcon")||
            request.getRequestURI().equals("/")||
            request.getRequestURI().indexOf("/bigFile") != -1||
                    request.getRequestURI().indexOf("/downloadApp")>-1||
                    request.getRequestURI().indexOf("/getApp")>-1||
                    request.getRequestURI().indexOf("/configMap/time")>-1||
                    request.getRequestURI().indexOf("/configMap/customTheme")>-1||
                    request.getRequestURI().startsWith("/configMap/verifyServerId")||
                    request.getRequestURI().indexOf("/product")>-1||
			request.getRequestURI().startsWith("/reSendEmail")||
			request.getRequestURI().startsWith("/forgetPassword")||
            request.getRequestURI().startsWith("/resetPassword")||
                    isValid(request, token)||
                    isSrpToken(srpToken)
        ){
            chain.doFilter(req, res);
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().format(JSONObject.toJSONString(Response.error("illegal user"))).flush();
            return;
        }
    }

    private boolean isValid(HttpServletRequest req, String token){
        try{
            HttpSession session = req.getSession();
            SsoService ssoService = new SsoService();
             String tokenUser = ssoService.getTokenUser(token);
            JSONObject tokenUserJSONObject = JSONObject.parseObject(tokenUser);
            if (tokenUserJSONObject.containsKey("error")) {
                log.error(tokenUserJSONObject.getString("error").toString());
                return false;
            }
            String tname = tokenUserJSONObject.getString("username");
            if (tname == null) {
                log.error("token verify failed: token user= "+tokenUser);
                return false;
            }
        }catch(Exception e){
            log.info("jwtError:"+ e);
            return false;
        }

        return true;
    }

    private boolean isSrpToken(String srpToken){
        try{
            if(srpToken == null)
                return false;
            srpToken = URLDecoder.decode(srpToken, "utf-8");
            log.info("srptoken:"+srpToken);
            String result = AES.Decrypt(srpToken);
            log.info("srptoken result:"+result);
            String[] resArray = result.split(SEPARATOR);
            long nowTime = System.currentTimeMillis()/1000;
            long otime = Long.valueOf(resArray[0]).longValue();
            log.info(nowTime+"##"+otime);
            String srpname = resArray[1];

            if((nowTime-otime) < TIMEOUT && srpname.equals(SRPNAME)){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public void init(FilterConfig filterConfig) {}

    public void destroy() {}
}
