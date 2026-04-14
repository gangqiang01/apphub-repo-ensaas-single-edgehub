package com.m2m.management.entity;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="repofiles",schema = "g_apprepo")
public class RepoFile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long rfid;

    @Column(nullable = false, unique = true)
    private String filename;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String format;


    @Column(nullable = false)
    private String address;

    private String description;

    private Long ts;
    private long size;

    @ManyToOne
    @JoinColumn(name = "sid")
    private Storage storage;

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

    @ManyToOne
    @JoinColumn(name = "rid")
    private Repo repo;

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    @Column(length = 1024*1024*10)
    private String presentation;

    @Transient
    private long[] rfidArray;

    private RepoFile(){};
    private RepoFile(long rfid){
        this.rfid = rfid;
    }
    public RepoFile(String filename, String type){
        this.filename = filename;
        this.type = type;
    }


    public long getrfid(){
        return this.rfid;
    }
    private void setrfid(long rfid){
        this.rfid = rfid;
    }


    public String getFilename(){
        return this.filename;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }

    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getFormat(){
        return this.format;
    }
    public void setFormat(String format){
        this.format = format;
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

    public Repo getRepo() {
        return this.repo;
    }
    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

    public long[] getRfidArray(){
        return this.rfidArray;
    }
    public void setRfidArray(long[] rfidArray){
        this.rfidArray = rfidArray;
    }

}

