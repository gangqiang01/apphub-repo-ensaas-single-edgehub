package com.m2m.management.configuration;

import com.m2m.management.entity.ProductImage;
import com.m2m.management.entity.ServerEmail;
import com.m2m.management.restful.SsoService;

import com.m2m.management.service.IProductImageService;
import com.m2m.management.service.IServerEmailService;
import com.m2m.management.truelicense.VerifyLicense;
import com.m2m.management.utils.FileToByte;
import com.m2m.management.utils.VerifyWpLicense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class InitDB implements ApplicationRunner {
    @Autowired
    private IProductImageService productImageService;
    @Value("${spring.mail.host}")
    private String cemailserver;

    @Value("${spring.mail.username}")
    private String emailname;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.type}")
    private String type;
    @Autowired
    private IServerEmailService serverEmailService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String base64 = "data:image/png;base64,";
        ServerEmail se= null;
        SsoService ssoService = new SsoService();
        try {
            if(!ssoService.checkClientInfo()){
                ssoService.createClient();
            }
            VerifyWpLicense verifyWpLicense = new VerifyWpLicense();
            if(verifyWpLicense.verify()){
                verifyWpLicense.timerVerifyLicense();
            }else{
                //verify local license
                VerifyLicense.verifyLicense(serverEmailService);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        List<ProductImage> productImages = productImageService.getAll();
        if(productImages != null&& productImages.size() == 0){
            ProductImage productImage = new ProductImage();
            File file = new File("AppHub-Repo.png");
            productImage.setImage(base64+ Base64.getEncoder().encodeToString(FileToByte.fileToByte(file)));
            productImage.setTs(new Date().getTime());
            productImageService.add(productImage);
        }

        try{
            List<ServerEmail> serverEmailList = serverEmailService.getAll();
            if(serverEmailList != null && serverEmailList.size() == 0){
                se = new ServerEmail();
                se.setCemailserver(cemailserver);
                se.setUsername(emailname);
                se.setPassword(password);
                se.setType(type);
                serverEmailService.add(se);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
