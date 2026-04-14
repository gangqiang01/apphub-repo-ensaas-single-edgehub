package com.m2m.management.entity;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonIgnore;
import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "aimuser", schema = "g_apprepo")

public class User implements java.io.Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private long uid;

	@Column(unique = true, length = 256)
	private String name;

	private String passwd;

	private Long ts;

	private String cloudurl;
	private String cloudserverid;
	public String getCloudServerId(){
		return this.cloudserverid;
	}
	public void setCloudServerId(String cloudserverid){
		this.cloudserverid = cloudserverid;
	}

	private int role;

	private String repoip;
	private String serverip;
	public String getServerIp(){
		return this.serverip;
	}
	public void setServerIp(String serverip){
		this.serverip = serverip;
	}

	public String serverid;
	public String getServerId(){
		return this.serverid;
	}
	public void setServerId(String serverid){
		this.serverid = serverid;
	}

	public String getRepoIp(){
		return this.repoip;
	}
	public void setRepoIp(String repoip){
		this.repoip = repoip;
	}

	@Column(name = "role")
	public int getRole(){
		return this.role;
	}
	public void setRole(int role){
		this.role = role;
	}


	@OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE})
	private Set<Repo> repo = new HashSet<Repo>(0);

	public void setAttention(Set<Attention> attention) {
		this.attention = attention;
	}

	@OneToMany(mappedBy = "user", cascade={CascadeType.REMOVE})
	private Set<Attention> attention = new HashSet<>(0);

	@OneToOne
	@JoinColumn(name = "sid")
	private Storage storage;

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public User() {
	}

	private User(long uid) {
		this.uid = uid;
	}
	
	public User(String name, String passwd) {
		this.name = name;
		this.passwd = passwd;

	}


	public long getUid() {
		return this.uid;
	}

	private void setUid(long uid) {
		this.uid = uid;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getPasswd() {
		return this.passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}


	public Long getTs() {
		return this.ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}


//	public Set<Repo> getRepo() {
//		return this.repo;
//	}
	public void setRepo(Set<Repo> repo) {
		this.repo = repo;
	}

	public String getCloudurl(){
		return this.cloudurl;
	}
	public void setCloudurl(String cloudurl){
		this.cloudurl = cloudurl;
	}


}
