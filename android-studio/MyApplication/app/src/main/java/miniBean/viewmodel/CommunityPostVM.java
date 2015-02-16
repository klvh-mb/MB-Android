package miniBean.viewmodel;

import java.util.List;

public class CommunityPostVM {
    public Long id;
    public Long oid;
    public String p;
    public long t;
    public Long uid;
    public String u;
    public long ut;
    public String ptl;
    public String pt;
    public boolean hasImage;
    public int n_c;
    public List<CommunityPostCommentVM> cs;
    public Long[] imgs;
    public String type;
    public String ctyp;
    public String cn;
    public String ci;
    public Long cid;
    public int nov;
    public int nol;
    public int nowa;
    public boolean ep;
    public boolean isO = false;
    public boolean showM = false;
    public boolean isC = false;
    public boolean isLike = false;
    public boolean isWtAns = false;
    public boolean isBookmarked = false;

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

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public long getUt() {
        return ut;
    }

    public void setUt(long ut) {
        this.ut = ut;
    }

    public String getPtl() {
        return ptl;
    }

    public void setPtl(String ptl) {
        this.ptl = ptl;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public int getN_c() {
        return n_c;
    }

    public void setN_c(int n_c) {
        this.n_c = n_c;
    }

    public List<CommunityPostCommentVM> getCs() {
        return cs;
    }

    public void setCs(List<CommunityPostCommentVM> cs) {
        this.cs = cs;
    }

    public Long[] getImgs() {
        return imgs;
    }

    public void setImgs(Long[] imgs) {
        this.imgs = imgs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCtyp() {
        return ctyp;
    }

    public void setCtyp(String ctyp) {
        this.ctyp = ctyp;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public int getNov() {
        return nov;
    }

    public void setNov(int nov) {
        this.nov = nov;
    }

    public int getNol() {
        return nol;
    }

    public void setNol(int nol) {
        this.nol = nol;
    }

    public int getNowa() {
        return nowa;
    }

    public void setNowa(int nowa) {
        this.nowa = nowa;
    }

    public boolean isEp() {
        return ep;
    }

    public void setEp(boolean ep) {
        this.ep = ep;
    }

    public boolean isO() {
        return isO;
    }

    public void setO(boolean isO) {
        this.isO = isO;
    }

    public boolean isShowM() {
        return showM;
    }

    public void setShowM(boolean showM) {
        this.showM = showM;
    }

    public boolean isC() {
        return isC;
    }

    public void setC(boolean isC) {
        this.isC = isC;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean isLike) {
        this.isLike = isLike;
    }

    public boolean isWtAns() {
        return isWtAns;
    }

    public void setWtAns(boolean isWtAns) {
        this.isWtAns = isWtAns;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}