package com.m2m.management.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "repo",schema = "g_apprepo")
public class Repo implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rid", unique = true, nullable = false)
    private long rid;

    @Column(nullable = false, unique = true)
    private String reponame;

    @Column(nullable = false)
    private String darkname;

    @OneToOne(optional=false)
    @JoinColumn(name = "rtid", unique=true, nullable = false)
    private RepoType repotype;

    @Column(name="description", length = 32)
    private String description;

    private Long ts;

//    @JsonBackReference
    @ManyToOne(optional=false)
    @JoinColumn(name = "uid")
    private User user;

    @OneToMany(mappedBy = "repo")
    private Set<RepoApp> repoApps = new HashSet<RepoApp>(0);

    @Transient
    private long uid;

    @Transient
    private long rtid;

    private String type;

    public RepoType getRepotype() {
        return repotype;
    }

    public void setRepotype(RepoType repotype) {
        this.repotype = repotype;
    }

    private Repo(){

    }

    private Repo(long rid){
        this.rid = rid;
    }

    public Repo(String reponame, String description){
        this.reponame = reponame;
        this.description = description;
    }

    public Repo(String reponame, String description, User user){
        this.reponame = reponame;
        this.description = description;
        this.user = user;
    }


    public long getRid() {
        return this.rid;
    }
    private void setRid(long rid) {
        this.rid = rid;
    }


    public String getReponame(){
        return this.reponame;
    }
    public void setReponame(String reponame){
        this.reponame = reponame;
    }


    public String getDarkname(){
        return this.darkname;
    }
    public void setDarkname(String darkname){
        this.darkname = darkname;
    }


    public RepoType getRepoType(){
        return this.repotype;
    }
    public void setRepoType(RepoType repotype){
        this.repotype = repotype;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public User getUser(){
        return this.user;
    }
    public void setUser(User user){
        this.user = user;
    }

//    public Set<RepoApps> getRepoApps() {
//        return this.repoApps;
//    }
    public void setRepoApps(Set<RepoApp> repoApps) {
        this.repoApps = repoApps;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

    public long getUid(){
        return this.uid;
    }
    public long getRtid() {
        return this.rtid;
    }
}
