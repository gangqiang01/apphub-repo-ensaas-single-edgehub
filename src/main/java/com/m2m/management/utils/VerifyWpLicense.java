package com.m2m.management.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.StringUtils;
import com.m2m.management.restful.SsoService;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


/**
 * @date ：Created in 10/30/19 9:05 AM
 * @description：verity wispaas license
 */
public class VerifyWpLicense implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(VerifyWpLicense.class);
    private final  String BASEPARTNUMBER = System.getenv("bpn");
    private final String STARDANDPARTNUMBER = System.getenv("spn");
    private final String PARTNUMBER = System.getenv("pn");
    private String subscriptionId = null;
    private static VerifyWpLicense instance;
    private String serviceInstanceId = null;
    //0: baselevel, 1: stardand, 2:pro
    public static int level = 0;


    public VerifyWpLicense(){

    }

    public static synchronized VerifyWpLicense getInstance(){
        if(instance == null) {
            instance = new VerifyWpLicense();
        }
        return instance;
    }

    public String getSubscriptionId(){
        return subscriptionId;
    }

    public boolean verify(){
        if(StringUtils.isNullOrEmpty(BASEPARTNUMBER)
                || StringUtils.isNullOrEmpty(STARDANDPARTNUMBER)
                ||StringUtils.isNullOrEmpty(PARTNUMBER)){
            LOG.error("PARTNUMBER is empty");
            return false;
        }

        SsoService ssoService = new SsoService();
        String licenseInfo = ssoService.getLicenseInfoByAppName();
        System.out.println("licenseInfo:"+licenseInfo);
        if(StringUtils.isNullOrEmpty(licenseInfo)){
            LOG.error("get sso licenseinfo error");
            return false;
        }
        try{
            JSONObject licenseInfoJson = JSONObject.parseObject(licenseInfo);
            JSONArray licenseInfoArray =  licenseInfoJson.getJSONArray("resources");
            for(int i=0; i<licenseInfoArray.size(); i++){
                JSONObject licenseObj = JSONObject.parseObject(licenseInfoArray.get(i).toString());
                String pn  = licenseObj.getString("pn");
                int count = Integer.valueOf(licenseObj.getString("number"));
                String authcode = licenseObj.getString("authcode");
                subscriptionId = licenseObj.getString("subscriptionId");
                System.out.println("PN:"+pn);
                if(verifyPN(pn, authcode, count)){
                    if(pn.equals(PARTNUMBER)){
                        if(level< 1)
                            level = 1;
                        return true;
                    }else if(pn.equals(STARDANDPARTNUMBER)){
                        if(level <1)
                            level = 1;
                        return true;
                    }

                }else{
                    System.out.println("verify authcode error: pn:"+pn);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private boolean verifyPN(String pn, String authCode, long count){
        SsoService ssoService = new SsoService();
        serviceInstanceId = ssoService.getServiceInstanceId();
        String[] authCodeArray = authCode.split("-");
        if(authCodeArray.length != 3){
            LOG.error("authCodeArray length is not 3");
            return false;
        }
        int number = ThirtySixToTenUtil.ThirtysixToDeciaml(authCodeArray[2].toUpperCase());
        if(number != count){
            return false;
        }
        String verifydata = String.format("%s+%s+%s+" ,pn, serviceInstanceId, count);
        System.out.println("verifydata:"+ verifydata);
        String md5VerifyData = DigestUtils.md5Hex(verifydata);
        String authCode1 = authCodeArray[0].substring(0, authCodeArray[0].length()-1);
        int authCode1In = Integer.valueOf(authCodeArray[0].substring(authCodeArray[0].length()-1), 16);
        String authCode2 = authCodeArray[1].substring(0, authCodeArray[1].length()-2);
        int authCode2In = Integer.valueOf(authCodeArray[1].substring(authCodeArray[1].length()-1), 16);

        if(md5VerifyData != null){
            String result1 = md5VerifyData.substring(authCode1In, authCode1In+authCode1.length());
            String result2 =  md5VerifyData.substring(authCode2In, authCode2In+authCode2.length());

            if(authCode1.equalsIgnoreCase(result1) && authCode2.equalsIgnoreCase(result2)){
                return true;
            }else{
                LOG.error("verify authcode fail");
            }
        }
        return false;
    }

    public void timerVerifyLicense(){
        try{
            new Thread(){
                @Override
                public void run() {
                    while(true){
//                        LOG.info("enter");
                        boolean res = verify();
//                        LOG.info("timerVerifyLicense result="+res);
                        long PERIOD_DAY = 60*60*1000;
                        if(System.getenv("licenseTimer") != null ){
                            PERIOD_DAY = Long.valueOf(System.getenv("licenseTimer"));
                        }
                        try{
//                            LOG.info("PERIOD_DAY="+PERIOD_DAY);
                            sleep(PERIOD_DAY);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
