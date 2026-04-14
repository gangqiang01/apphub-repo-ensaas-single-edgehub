package com.m2m.management.entity;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="repobsps",schema = "g_apprepo")
public class RepoBsp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long rbid;

//    @Column(nullable = false)
//    private String filename;

    @Column(nullable = false)
    private String boardname;

    @Column(nullable = false)
    private String versionname;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String os;

    private String description;

    private Long ts;
    private long size;

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

    public long getSize(){
        return this.size;
    }
    public void setSize(long size){
        this.size = size;
    }

    @Column(length = 2048)
    private String tags;
    public String getTags(){
        return this.tags;
    }
    public void setTags(String tags){
        this.tags = tags;
    }

    @Transient
    private long rid;

    @Transient
    private long[] rbidArray;

    @ManyToOne
    @JoinColumn(name = "rid")
    private Repo repo;

    private RepoBsp(){};
    private RepoBsp(long rfid){
        this.rbid = rbid;
    }
    public RepoBsp(String versionname, String boardname){
        this.versionname = versionname;
        this.boardname = boardname;
    }


    public long getrbid(){
        return this.rbid;
    }
    private void setrbid(long rbid){
        this.rbid = rbid;
    }

//    public String getFilename(){
//        return this.filename;
//    }
//    public void setFilename(String filename){
//        this.filename = filename;
//    }

    public String getBoardname(){
        return this.boardname;
    }
    public void setBoardname(String boardname){
        this.boardname = boardname;
    }


    public String getVersionname(){
        return this.versionname;
    }
    public void setVersionname(String versionname){
        this.versionname = versionname;
    }

    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public String getOs(){
        return this.os;
    }
    public void setOs(String os){
        this.os = os;
    }

    public Repo getRepo() {
        return this.repo;
    }
    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public long getRid(){
        return this.rid;
    }

    public long[] getRbidArray(){
        return this.rbidArray;
    }
    public void setRbidArray(long[] rbidArray){
        this.rbidArray = rbidArray;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

}
