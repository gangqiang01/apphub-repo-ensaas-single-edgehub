package com.m2m.management.truelicense;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.m2m.management.entity.ServerEmail;
import com.m2m.management.restful.SsoService;
import com.m2m.management.service.IServerEmailService;
import com.m2m.management.utils.DESUtil;
import com.m2m.management.utils.FileUtil;

import com.m2m.management.utils.VerifyWpLicense;
import de.schlichtherle.license.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.util.*;
import java.util.prefs.Preferences;
import com.auth0.jwt.internal.org.bouncycastle.util.encoders.Base64;
/**
 * VerifyLicense
 * @author melina
 */
public class VerifyLicense {
    private static final Logger LOG = LoggerFactory.getLogger(VerifyLicense.class);
    //common param
    private static String PUBLICALIAS = "";
    private static String STOREPWD = "";
    private static String SUBJECT = "";
    public static String licFile = "apphub.lic";
    public static String licPath = FileUtil.tmpPath+ "/"+ licFile;
    private static String pubPath = "";
    static public boolean verifyLicense(IServerEmailService systemConfigBean){
        boolean ret = false;
        VerifyLicense vLicense = new VerifyLicense();
        vLicense.setParam("param.properties");
        List<ServerEmail> systemConfigs = systemConfigBean.getAll();
        byte[] bytes = null;
        String base64 = "";
        if(systemConfigs!=null&& systemConfigs.size() > 0){
            ServerEmail systemConfig = systemConfigs.get(0);
            base64 = systemConfig.getLicensefile();
        }
        if(!StringUtils.isEmpty(base64)){
            bytes = Base64.decode(base64);
            if(FileUtil.copyFile(bytes, licPath)){
                ret = vLicense.verify(new File(licPath));
            }
        }
        return ret;
    }
    public void setParam(String propertiesPath) {
        // 获取参数
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(propertiesPath);
        try {
            prop.load(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(PUBLICALIAS.equals(""))
            PUBLICALIAS = prop.getProperty("PUBLICALIAS");
        if(STOREPWD.equals(""))
            STOREPWD = prop.getProperty("STOREPWD");
        if(SUBJECT.equals(""))
            SUBJECT = prop.getProperty("SUBJECT");
        if(pubPath.equals("")) {
            pubPath = prop.getProperty("pubPath");
            //pubPath = this.getClass().getClassLoader().getResource("models").getPath();
        }
    }
    private boolean validateMacAddress(String mac) {
        String address = null;
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                //if (nif.getName().equalsIgnoreCase("eth0")) {
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes != null) {
                    StringBuilder res1 = new StringBuilder();
                    String stmp = "";
                    for (byte b : macBytes) {
                        stmp = Integer.toHexString(b & 0xFF);
                        if (stmp.length() == 1)
                            res1.append("0").append(stmp + ":");
                        else
                            res1.append(stmp + ":");
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    address = res1.toString();
                    System.out.println("mac="+address);
                    if(address != null && address.equalsIgnoreCase(mac)) {
                        return true;
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }

        } catch (Exception ex) {
            //handle exception
            LOG.error("[validateMacAddress]get mac address failed");
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean validateDevice(String deviceInfo) {
        String encryInfo = deviceInfo;
        String info = null;
        String serviceInstanceId = SsoService.getInstanceId();
        if(serviceInstanceId == null){
            return false;
        }
        info = DESUtil.decrypt(DESUtil.licenseKey, encryInfo);
        if(info.equalsIgnoreCase(serviceInstanceId)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verify(File licfile) {
        /************** 证书使用者端执行 ******************/

        LicenseManager licenseManager = LicenseManagerHolder
                .getLicenseManager(initLicenseParams());
        // 安装证书
        try {
            licenseManager.install(licfile);
            LOG.info("install license success!");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("install license fail!");
            return false;
        }
        // 验证证书
        try {
			/*
			licenseManager.verify();
			System.out.println("客户端验证证书成功!");
			*/

            //使用LicenseContent来接收默认校验的返回值，返回值为签名内容，进行二次校验
            LicenseContent licenseContent = licenseManager.verify();
            //扩展验证,非扩展验证可从licenseContent中获取
            Map<String,String> content = (HashMap<String, String>) licenseContent.getExtra();
            boolean result = false;
            String deviceInfo = content.get("deviceInfo");
            //此处用于存储最大用户数
            String maxAgent = content.get("maxAgent");
//            LOG.info("deviceInfo="+deviceInfo);
//            LOG.info("maxAgent="+maxAgent);
            result = validateDevice(deviceInfo);
            if(result){
                VerifyWpLicense.level = 1;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 返回验证证书需要的参数
    private static LicenseParam initLicenseParams() {
        Preferences preference = Preferences
                .userNodeForPackage(VerifyLicense.class);
        CipherParam cipherParam = new DefaultCipherParam(STOREPWD);

        KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(
                VerifyLicense.class, pubPath, PUBLICALIAS, STOREPWD, null);
        LicenseParam licenseParams = new DefaultLicenseParam(SUBJECT,
                preference, privateStoreParam, cipherParam);
        return licenseParams;
    }
}