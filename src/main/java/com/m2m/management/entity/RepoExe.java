package com.m2m.management.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="repoexes",schema = "g_apprepo")
public class RepoExe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long reid;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String projectname;

    @Column(nullable = false)
    private String version;

    private String tool;

    private String type;


    private String description;

    private Long ts;

    private String address;
    private String org;
    public String getOrg() {
        return org;
    }
    public void setOrg(String org) {
        this.org = org;
    }

    @ManyToOne
    @JoinColumn(name = "sid")
    private Storage storage;

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    @Column(length = 1024*1024*10)
    private String presentation;

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
    private long[] reidArray;

    @ManyToOne
    @JoinColumn(name = "rid")
    private Repo repo;

    private RepoExe(){};
    private RepoExe(long reid){
        this.reid = reid;
    }
    public RepoExe(String filename, String projectname){
        this.filename = filename;
        this.projectname = projectname;
    }


    public long getreid(){
        return this.reid;
    }
    private void setreid(long reid){
        this.reid = reid;
    }

    public String getFilename(){
        return this.filename;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }

    public String getProjectname(){
        return this.projectname;
    }
    public void setProjectname(String projectname){
        this.projectname = projectname;
    }

    public String getTool(){
        return this.tool;
    }
    public void setTool(String tool){
        this.tool = tool;
    }


    public String getVersion(){
        return this.version;
    }
    public void setVersion(String version){
        this.version = version;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }


    public Repo getRepo() {
        return this.repo;
    }
    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public long[] getReidArray(){
        return this.reidArray;
    }
    public void setReidArray(long[] reidArray){
        this.reidArray = reidArray;
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

    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
}
