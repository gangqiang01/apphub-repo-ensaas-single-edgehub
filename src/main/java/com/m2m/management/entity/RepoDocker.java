package com.m2m.management.entity;

import javax.persistence.*;

/**
 * @date ：Created in 5/11/20 5:32 PM
 * @description：docker info entity
 */

@Entity
@Table(name = "repodocker",schema = "g_apprepo")
public class RepoDocker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rdid", unique = true, nullable = false)
    private long rdid;

    @Column(nullable = false)
    private String container;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private Long ts;

    @ManyToOne
    @JoinColumn(name = "rdcid")
    private RepoDockerCompose repoDockerCompose;

    private RepoDocker(){};
    private RepoDocker(long rdid){
        this.rdid = rdid;
    }
    public RepoDocker(String container, String image, String version){
        this.container = container;
        this.image = image;
        this.version = version;
    }

    public long getRdid(){
        return this.rdid;
    }
    private void setrdid(long rdid){
        this.rdid = rdid;
    }

    public String getContainer(){
        return this.container;
    }
    public void setContainer(String container){
        this.container = container;
    }

    public String getImage(){
        return this.image;
    }
    public void setImage(String image){
        this.image = image;
    }

    public String getVersion(){
        return this.version;
    }
    public void setVersion(String version){
        this.version = version;
    }

    public Long getTs() {
        return this.ts;
    }
    public void setTs(Long ts) {
        this.ts = ts;
    }

    public void setRepoDockerCompose(RepoDockerCompose repoDockerCompose){
        this.repoDockerCompose = repoDockerCompose;
    }

}
