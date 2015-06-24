package miniBean.viewmodel;

import java.util.Date;

public class GameTransactionVM {
    public long uid;
    public Date tt;
    public String ty;
    public long tp;
    public String td;
    public long ntp;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Date getTt() {
        return tt;
    }

    public void setTt(Date tt) {
        this.tt = tt;
    }

    public String getTy() {
        return ty;
    }

    public void setTy(String ty) {
        this.ty = ty;
    }

    public long getTp() {
        return tp;
    }

    public void setTp(long tp) {
        this.tp = tp;
    }

    public String getTd() {
        return td;
    }

    public void setTd(String td) {
        this.td = td;
    }

    public long getNtp() {
        return ntp;
    }

    public void setNtp(long ntp) {
        this.ntp = ntp;
    }
}

