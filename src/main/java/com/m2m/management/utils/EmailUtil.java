package com.m2m.management.utils;

import com.m2m.management.entity.ServerEmail;
import com.m2m.management.service.IServerEmailService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import sun.nio.cs.ext.GBK;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class EmailUtil {

    private static JavaMailSenderImpl mailSender;
    private static String PROJECTNAME = "WISE-IoTSuite/AppHub Repo";

    public static boolean sendMail(IServerEmailService serverEmailService, String receiveMailAccount, String username, String password) throws Exception{

        try{
            List<ServerEmail> serverEmailList = serverEmailService.getAll();
            if(serverEmailList != null && serverEmailList.size() > 0){
                ServerEmail serverEmail = serverEmailList.get(0);
                mailSender = new JavaMailSenderImpl();
                mailSender.setHost(serverEmail.getCemailserver());
                mailSender.setUsername(serverEmail.getUsername());
                mailSender.setPassword(serverEmail.getPassword());
                if(serverEmail.getProjectname() != null && serverEmail.getProjectname() != ""){
                    PROJECTNAME = serverEmail.getProjectname();
                }
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.tarttls.required", "true");
                properties.put("mail.smtp.ssl.enable", "true");
                mailSender.setJavaMailProperties(properties);
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
                messageHelper.setFrom(new InternetAddress(serverEmail.getUsername(), PROJECTNAME, "UTF-8"));
                messageHelper.setTo(receiveMailAccount);
                messageHelper.setSubject(PROJECTNAME+" "+"create user success!");
                String html = "<div style=\"margin-top:30px;\">\n" +
                        "            <i>Dear friend,</i>\n" +
                        "            <p style=\"margin-top:50px; font-size:20px\">Welcome to"+" "+PROJECTNAME+".</p>\n" +
                        "            <p style=\"margin-top:20px;\">Congratulations on your successful registration of"+" "+PROJECTNAME+". If it is not your request,please ignore this email.</p>\n" +
                        "            <p style=\"margin-top:20px;\">Please remember you username and paassword as below:</p>\n" +
                        "            <p style=\"margin-top:50px; margin-left:20px\"><span style=\"font-weight:600\">*Username: </span>" +" "+ username+"</p>\n"+
                        "            <p style=\"margin-top:20px; margin-left:20px\"><span style=\"font-weight:600\">*Password: </span>" +" "+ password+ "</p>\n"+
                        "            <p style=\"margin-top:30px; margin-bottom:50px;\">This is an automatically generated email,please do not reply.</p>\n" +
                        "            <i>Best regards,</i><br>\n" +
                        "            <i>"+PROJECTNAME+"</i>\n" +
                        "        </div>";
                messageHelper.setText(html, true);
                mailSender.setDefaultEncoding("UTF-8");
                mailSender.send(messageHelper.getMimeMessage());
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean batchSendMail(IServerEmailService serverEmailService, String[] receiveMailAccount, String name, String version, String type, String blobUrl){

        try{
            List<ServerEmail> serverEmailList = serverEmailService.getAll();
            if(serverEmailList != null && serverEmailList.size() > 0){
                ServerEmail serverEmail = serverEmailList.get(0);
                mailSender = new JavaMailSenderImpl();
                mailSender.setHost(serverEmail.getCemailserver());
                mailSender.setUsername(serverEmail.getUsername());
                mailSender.setPassword(serverEmail.getPassword());
                if(serverEmail.getProjectname() != null && serverEmail.getProjectname() != ""){
                    PROJECTNAME = serverEmail.getProjectname();
                }
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.tarttls.required", "true");
                properties.put("mail.smtp.ssl.enable", "true");
                mailSender.setJavaMailProperties(properties);
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
                messageHelper.setFrom(new InternetAddress(serverEmail.getUsername(), PROJECTNAME, "UTF-8"));
                messageHelper.setBcc(receiveMailAccount);
                messageHelper.setSubject(PROJECTNAME+" "+"have updated!");
                String html = "<div style=\"margin-top:30px;\">\n" +
                        "            <i>Dear friend,</i>\n" +
                        "            <p style=\"margin-top:50px; font-size:20px\">Welcome to"+" "+PROJECTNAME+".</p>\n" +
                        "            <p style=\"margin-top:20px;\">The "+ type + " you subscribed to  have been updated.</p>\n" +
                        "            <p style=\"margin-top:20px;\">Update information as below:</p>\n" +
                        "            <p style=\"margin-top:50px; margin-left:20px\"><span style=\"font-weight:600\">*Storage Url: </span>" +" "+ blobUrl +"</p>\n"+
                        "            <p style=\"margin-top:20px; margin-left:20px\"><span style=\"font-weight:600\">*Name: </span>" +" "+ name +"</p>\n"+
                        "            <p style=\"margin-top:20px; margin-left:20px\"><span style=\"font-weight:600\">*Version: </span>" +" "+ version + "</p>\n"+
                        "            <p style=\"margin-top:30px; margin-bottom:50px;\">This is an automatically generated email,please do not reply.</p>\n" +
                        "            <i>Best regards,</i><br>\n" +
                        "            <i>"+PROJECTNAME+"</i>\n" +
                        "        </div>";
                messageHelper.setText(html, true);
                mailSender.send(messageHelper.getMimeMessage());
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
