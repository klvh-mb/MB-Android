package miniBean.viewmodel;

import java.util.List;

/**
 * Created by User on 2/4/15.
 */
public class Post {
    public Long id;
    public Long oid;
    public String p;
    public Long t;
    public String pt;
    public String ptl;
    public int n_c;
    public int nowa;
    public List<Comment> cs;
    public boolean hasImage;
    public List<Long> imgs;
    public String cn;

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long cid;

    public String getPtl() {
        return ptl;
    }

    public void setPtl(String ptl) {
        this.ptl = ptl;
    }

    public int getNowa() {
        return nowa;
    }

    public void setNowa(int nowa) {
        this.nowa = nowa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public List<Comment> getCs() {
        return cs;
    }

    public void setCs(List<Comment> cs) {
        this.cs = cs;
    }

    public int getN_c() {
        return n_c;
    }

    public void setN_c(int n_c) {
        this.n_c = n_c;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public List<Long> getImgs() {
        return imgs;
    }

    public void setImgs(List<Long> imgs) {
        this.imgs = imgs;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }
}