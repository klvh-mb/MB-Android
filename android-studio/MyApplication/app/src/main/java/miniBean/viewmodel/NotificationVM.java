package miniBean.viewmodel;

public class NotificationVM {
    public Long id;
    public Long nid;
    public int sta;
    public String msg;
    public String tp;
    public UrlsVM url;
    public String upd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNid() {
        return nid;
    }

    public void setNid(Long nid) {
        this.nid = nid;
    }

    public int getSta() {
        return sta;
    }

    public void setSta(int sta) {
        this.sta = sta;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public UrlsVM getUrl() {
        return url;
    }

    public void setUrl(UrlsVM url) {
        this.url = url;
    }

    public String getUpd() {
        return upd;
    }

    public void setUpd(String upd) {
        this.upd = upd;
    }
}
