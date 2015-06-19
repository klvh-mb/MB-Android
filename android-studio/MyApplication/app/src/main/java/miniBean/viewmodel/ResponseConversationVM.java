package miniBean.viewmodel;

public class ResponseConversationVM {
    public Long id;
    public Boolean isRead;
    public Boolean isToday;
    public String nm;
    public Long uid;
    public Long lmd;
    public String lm;
    public Long ur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsToday() {
        return isToday;
    }

    public void setIsToday(Boolean isToday) {
        this.isToday = isToday;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getLmd() {
        return lmd;
    }

    public void setLmd(Long lmd) {
        this.lmd = lmd;
    }

    public String getLm() {
        return lm;
    }

    public void setLm(String lm) {
        this.lm = lm;
    }

    public Long getUr() {
        return ur;
    }

    public void setUr(Long ur) {
        this.ur = ur;
    }
}
