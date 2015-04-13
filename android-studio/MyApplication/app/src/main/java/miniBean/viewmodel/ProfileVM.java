package miniBean.viewmodel;

public class ProfileVM {
    public long id;
    public String dn;
    public String yr;
    public String gd;
    public String a;
    public long n_f;
    public long n_p;

    public long n_c;
    //public List<User> l_f;

    public boolean isf;
    public boolean isP;

    public boolean isfv = true;
    public boolean isaV = true;
    public boolean iscV = true;
    public boolean isdV = true;

    // admin readonly fields
    public String n;
    public boolean mb;
    public boolean fb;
    public boolean vl;
    public String em;
    public String sd;
    public String ll;
    public Long tl;
    public Long qc;
    public Long ac;
    public Long lc;
    public Long wc;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getYr() {
        return yr;
    }

    public void setYr(String yr) {
        this.yr = yr;
    }

    public String getGd() {
        return gd;
    }

    public void setGd(String gd) {
        this.gd = gd;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public long getN_f() {
        return n_f;
    }

    public void setN_f(long n_f) {
        this.n_f = n_f;
    }

    public long getN_p() {
        return n_p;
    }

    public void setN_p(long n_p) {
        this.n_p = n_p;
    }

    public long getN_c() {
        return n_c;
    }

    public void setN_c(long n_c) {
        this.n_c = n_c;
    }

    public boolean isIsf() {
        return isf;
    }

    public void setIsf(boolean isf) {
        this.isf = isf;
    }

    public boolean isP() {
        return isP;
    }

    public void setP(boolean isP) {
        this.isP = isP;
    }

    public boolean isIsfv() {
        return isfv;
    }

    public void setIsfv(boolean isfv) {
        this.isfv = isfv;
    }

    public boolean isIsaV() {
        return isaV;
    }

    public void setIsaV(boolean isaV) {
        this.isaV = isaV;
    }

    public boolean isIscV() {
        return iscV;
    }

    public void setIscV(boolean iscV) {
        this.iscV = iscV;
    }

    public boolean isIsdV() {
        return isdV;
    }

    public void setIsdV(boolean isdV) {
        this.isdV = isdV;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public boolean isMb() {
        return mb;
    }

    public void setMb(boolean mb) {
        this.mb = mb;
    }

    public boolean isFb() {
        return fb;
    }

    public void setFb(boolean fb) {
        this.fb = fb;
    }

    public boolean isVl() {
        return vl;
    }

    public void setVl(boolean vl) {
        this.vl = vl;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getLl() {
        return ll;
    }

    public void setLl(String ll) {
        this.ll = ll;
    }

    public Long getTl() {
        return tl;
    }

    public void setTl(Long tl) {
        this.tl = tl;
    }

    public Long getQc() {
        return qc;
    }

    public void setQc(Long qc) {
        this.qc = qc;
    }

    public Long getAc() {
        return ac;
    }

    public void setAc(Long ac) {
        this.ac = ac;
    }

    public Long getLc() {
        return lc;
    }

    public void setLc(Long lc) {
        this.lc = lc;
    }

    public Long getWc() {
        return wc;
    }

    public void setWc(Long wc) {
        this.wc = wc;
    }

    @Override
    public String toString() {
        return "id=" + id +
                "\nemail=" + em +
                "\nemailValidated=" + vl +
                "\nmobileSignup=" + mb +
                "\nfbLogin=" + fb +
                "\nsignupDate=" + sd +
                "\nlastLogin=" + ll +
                "\ntotalLogin=" + tl +
                "\nquestionsCount=" + qc +
                "\nanswersCount=" + ac +
                "\nlikesCount=" + lc;
    }
}

