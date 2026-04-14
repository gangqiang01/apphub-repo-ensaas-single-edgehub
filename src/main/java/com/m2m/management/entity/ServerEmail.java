package com.m2m.management.entity;

import javax.persistence.*;

@Entity
@Table(name = "serveremail", schema = "g_apprepo")
public class ServerEmail implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    private String cemailserver;

    private String username;

    private String password;

    private String type;

    private int isshow;

    private String projectname;

    private byte[] lefticon;

    private byte[] righticon;

    private byte[] loginicon;


    @Column(name="licensefile", length = 1024*1024)
    private String licensefile;

    public  String getLicensefile() {
        return licensefile;
    }

    public void setLicensefile( String licensefile) {
        this.licensefile = licensefile;
    }

    public int getIsshow() {
        return isshow;
    }

    public void setIsshow(int isshow) {
        this.isshow = isshow;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public byte[] getLefticon() {
        return lefticon;
    }

    public void setLefticon(byte[] lefticon) {
        this.lefticon = lefticon;
    }

    public byte[] getRighticon() {
        return righticon;
    }

    public void setRighticon(byte[] righticon) {
        this.righticon = righticon;
    }

    public byte[] getLoginicon() {
        return loginicon;
    }

    public void setLoginicon(byte[] loginicon) {
        this.loginicon = loginicon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCemailserver() {
        return cemailserver;
    }

    public void setCemailserver(String cemailserver) {
        this.cemailserver = cemailserver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
