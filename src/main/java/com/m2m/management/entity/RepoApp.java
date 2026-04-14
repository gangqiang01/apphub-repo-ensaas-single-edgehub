package com.m2m.management.entity;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="repoapps",schema = "g_apprepo")
public class RepoApp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long raid;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String pkgname;

    @Column(nullable = false)
    private String versionname;

    private String versioncode;

    private String license;

    private String description;

    private String summary;

    private String websit;

    private Long ts;

    private String address;

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    @Column(length = 1024*1024*10)
    private String presentation;

    @ManyToOne
    @JoinColumn(name = "sid")
    private Storage storage;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Column(length = 2048)
    private String tags;
    public String getTags(){
        return this.tags;
    }
    public void setTags(String tags){
        this.tags = tags;
    }

    private long size;
    public long getSize(){
        return this.size;
    }
    public void setSize(long size){
        this.size = size;
    }
    @Transient
    private long[] raidArray;

    @ManyToOne
    @JoinColumn(name = "rid")
    private Repo repo;

    private RepoApp(){};
    private RepoApp(long raid){
        this.raid = raid;
    }
    public RepoApp(String filename, String pkgname){
        this.filename = filename;
        this.pkgname = pkgname;
    }


    public long getraid(){
        return this.raid;
    }
    private void setraid(long raid){
        this.raid = raid;
    }

    public String getFilename(){
        return this.filename;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }

    public String getPkgname(){
        return this.pkgname;
    }
    public void setPkgname(String pkgname){
        this.pkgname = pkgname;
    }


    public String getVersionname(){
        return this.versionname;
    }
    public void setVersionname(String versionname){
        this.versionname = versionname;
    }

    public String getVersioncode(){
        return this.versioncode;
    }
    public void setVersioncode(String versioncode){
        this.versioncode = versioncode;
    }

    public String getLicense(){
        return this.license;
    }
    public void setLicense(String license){
        this.license = license;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }


    public String getSummary(){
        return this.summary;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }


    public String getWebsit(){
        return this.websit;
    }
    public void setWebsit(String websit){
        this.websit = websit;
    }

    public Repo getRepo() {
        return this.repo;
    }
    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public long[] getRaidArray(){
        return this.raidArray;
    }
    public void setRaidArray(long[] raidArray){
        this.raidArray = raidArray;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }
}
