package com.m2m.management.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "repotype",schema = "g_apprepo")
public class RepoType implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rtid", unique = true, nullable = false)
    private long rtid;

    @Column(nullable = false, unique = true)
    private String typename;

    private Long ts;

    //    @JsonBackReference

    @OneToMany(mappedBy = "repotype", cascade={CascadeType.REMOVE})
    private Set<Repo> repos = new HashSet<Repo>(0);


    private RepoType(){};
    private RepoType(long rtid){
        this.rtid = rtid;
    }

    public RepoType(String typename){
        this.typename = typename;
    }



    public long getRtid() {
        return this.rtid;
    }
    private void setRtid(long rtid) {
        this.rtid = rtid;
    }


    public String getTypename(){
        return this.typename;
    }
    public void setTypename(String typename){
        this.typename = typename;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

    public void setRepos(Set<Repo> repos) {
        this.repos = repos;
    }

}
