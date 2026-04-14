package com.m2m.management.entity;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @date ：Created in 5/11/20 5:33 PM
 * @description：docker compose info entity
 */

@Entity
@Table(name = "repolinuxpkg",schema = "g_apprepo")
public class RepoLinuxPkg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( unique = true, nullable = false)
    private long rlid;


    @Column(nullable = false)
    private String productname;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private long ts;

    private String address;

    @Column(length = 2048)
    private String tags;

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
    public String getTags(){
        return this.tags;
    }
    public void setTags(String tags){
        this.tags = tags;
    }

    @Column(nullable = false)
    private String description;

    private long size;
    public long getSize(){
        return this.size;
    }
    public void setSize(long size){
        this.size = size;
    }

    @ManyToOne
    @JoinColumn(name = "rid")
    private Repo repo;

    @JsonIgnore
    @Transient
    private long[] rlidArray;

    public long[] getRlidArray(){
        return this.rlidArray;
    }
    public void setRlidArray(long[] rlidArray){
        this.rlidArray = rlidArray;
    }

    private RepoLinuxPkg(){};
    private RepoLinuxPkg(long rlid){
        this.rlid = rlid;
    }
    public RepoLinuxPkg(String productname,String version, String description){
        this.productname = productname;
        this.description = description;
        this.version = version;
    }

    public long getRlid(){
        return this.rlid;
    }
    private void setRlid(long rlid){
        this.rlid = rlid;
    }

    public String getProductname(){
        return this.productname;
    }
    public void setProductname(String productname){
        this.productname = productname;
    }

    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getVersion(){
        return this.version;
    }
    public void setVersion(String version){
        this.version = version;
    }

    public String getFilename(){
        return this.filename;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String version){
        this.description = description;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Repo getRepo() {
        return this.repo;
    }
    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }

}
