package com.m2m.management.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "productconcern", schema = "g_apprepo")
public class ProductConcern implements java.io.Serializable{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pcid", unique = true, nullable = false)
    private long pcid;

    private String productname;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    public long getPcid() {
        return pcid;
    }

    public void setPcid(long pcid) {
        this.pcid = pcid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
