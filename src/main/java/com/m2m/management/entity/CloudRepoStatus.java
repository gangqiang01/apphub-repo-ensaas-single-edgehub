package com.m2m.management.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @date ：Created in 9/3/20 11:37 AM
 * @description：cloud repo status
 */

@Entity
@Table(name = "cloudrepostatus",schema = "g_apprepo")
public class CloudRepoStatus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long cid;

    private String pkgname;

    private String version;

    private String filename;

    private String type;

    private int status;

    private int errorcode;

    private Long ts;

    private String dpname;

    @ManyToOne
    @JoinColumn(name = "sid")
    private Storage storage;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Transient
    private long[] cidArray;
    public long[] getCidArray(){
        return this.cidArray;
    }
    public void setCidArray(long[] cidArray){
        this.cidArray = cidArray;
    }

    public long getCid(){
        return this.cid;
    }
    public void setCid(long cid) {
         this.cid = cid;
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


    public String getVersion(){
        return this.version;
    }
    public void setVersion(String version){
        this.version = version;
    }

    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }

    public int getStatus(){
        return this.status;
    }
    public void setStatus(int status){
        this.status = status;
    }

    public int getErrorcode(){
        return this.errorcode;
    }
    public void setErrorcode(int errorcode){
        this.errorcode = errorcode;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getDpname(){
        return this.dpname;
    }
    public void setDpname(String dpname){
        this.dpname = dpname;
    }
}
