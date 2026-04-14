package com.m2m.management.entity;


import javax.persistence.*;

@Entity
@Table(name = "productitem", schema = "g_apprepo")
public class ProductItem implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "piid", unique = true, nullable = false)
    private long piid;

    private String filename;

    private String appname;

    private String version;

    private String type;

    private String apptype;

    public String getApptype() {
        return apptype;
    }

    public void setApptype(String apptype) {
        this.apptype = apptype;
    }

    @ManyToOne
    @JoinColumn(name = "pid")
    private Product product;

    public long getPiid() {
        return piid;
    }

    public void setPiid(long piid) {
        this.piid = piid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
