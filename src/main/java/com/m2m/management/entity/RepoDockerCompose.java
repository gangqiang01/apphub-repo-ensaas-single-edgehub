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
@Table(name = "repodockercompose",schema = "g_apprepo")
public class RepoDockerCompose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( unique = true, nullable = false)
    private long rdcid;


    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private long ts;

    private String address;

    private String description;

    private String type;
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }

    @ManyToOne
    @JoinColumn(name = "sid")
    private Storage storage;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @ManyToOne
    @JoinColumn(name = "rid")
    private Repo repo;

    @JsonIgnore
    @Transient
    private String content;


    @OneToMany(mappedBy = "repoDockerCompose", cascade={CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private Set<RepoDocker> repoDockers = new HashSet<RepoDocker>(0);

    @JsonIgnore
    @Transient
    private long[] rdcidArray;


    public long[] getRdcidArray(){
        return this.rdcidArray;
    }
    public void setRdcidArray(long[] rdcidArray){
        this.rdcidArray = rdcidArray;
    }

    private RepoDockerCompose(){};
    private RepoDockerCompose(long rdcid){
        this.rdcid = rdcid;
    }
    public RepoDockerCompose(String tag, String description){
        this.tag = tag;
        this.description = description;
    }

    public long getRdcid(){
        return this.rdcid;
    }
    private void setRdcid(long rdcid){
        this.rdcid = rdcid;
    }

    public String getTag(){
        return this.tag;
    }
    public void setTag(String tag){
        this.tag = tag;
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

    public Set<RepoDocker> getRepoDockers(){
        return repoDockers;
    }
    public void setRepoDockers(Set<RepoDocker> repoDockers){
        this.repoDockers = repoDockers;
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

    public String getContent(){
        return this.content;
    }
    public void setContent(String content){
        this.content = content;
    }
}
