package com.m2m.management.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "storage", schema = "g_apprepo")
public class Storage implements java.io.Serializable {

    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sid", unique = true, nullable = false)
    private long sid;

    private String bloburl;

    private String blobbucket;

    private String blobaccesskey;

    private String blobsecretkey;

    private String type;

    private Long ts;

    private int choose;

    @OneToMany(mappedBy = "storage", cascade = {CascadeType.REMOVE})
    private Set<RepoApp> repoApps = new HashSet<RepoApp>(0);

    @OneToMany(mappedBy = "storage", cascade = {CascadeType.REMOVE})
    private Set<RepoBsp> repoBsps = new HashSet<RepoBsp>(0);

    @OneToMany(mappedBy = "storage", cascade = {CascadeType.REMOVE})
    private Set<RepoDockerCompose> repoDockerComposes = new HashSet<RepoDockerCompose>(0);

    @OneToMany(mappedBy = "storage", cascade = {CascadeType.REMOVE})
    private Set<RepoExe> repoExes = new HashSet<RepoExe>(0);

    @OneToMany(mappedBy = "storage", cascade = {CascadeType.REMOVE})
    private Set<RepoFile> repoFiles = new HashSet<RepoFile>(0);

    @OneToMany(mappedBy = "storage", cascade = {CascadeType.REMOVE})
    private Set<RepoLinuxPkg> repoLinuxPkgs = new HashSet<RepoLinuxPkg>(0);

    @OneToMany(mappedBy = "storage", cascade = {CascadeType.REMOVE})
    private Set<CloudRepoStatus> cloudRepoStatuses = new HashSet<CloudRepoStatus>(0);

    private Storage(long sid){
        this.sid = sid;
    };

    public void setRepoApps(Set<RepoApp> repoApps) {
        this.repoApps = repoApps;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Storage(){

    }

    public Storage(String bloburl, String blobbucket, String blobaccesskey, String blobsecretkey) {
        this.bloburl = bloburl;
        this.blobbucket = blobbucket;
        this.blobaccesskey = blobaccesskey;
        this.blobsecretkey = blobsecretkey;
    }

    public int getChoose() {
        return choose;
    }

    public void setChoose(int choose) {
        this.choose = choose;
    }

    

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getBloburl() {
        return bloburl;
    }

    public void setBloburl(String bloburl) {
        this.bloburl = bloburl;
    }

    public String getBlobbucket() {
        return blobbucket;
    }

    public void setBlobbucket(String blobbucket) {
        this.blobbucket = blobbucket;
    }

    public String getBlobaccesskey() {
        return blobaccesskey;
    }

    public void setBlobaccesskey(String blobaccesskey) {
        this.blobaccesskey = blobaccesskey;
    }

    public String getBlobsecretkey() {
        return blobsecretkey;
    }

    public void setBlobsecretkey(String blobsecretkey) {
        this.blobsecretkey = blobsecretkey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
