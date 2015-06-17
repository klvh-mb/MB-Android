package miniBean.viewmodel;

public class ConversationVM {
	 public String nm;
	 public Long uid;
	 public Long id;
	 public Long lmd;
	 public Boolean isToday;
	 public String lm;
	 public Boolean isReaded = false;
	 public Long mc = 0L;
	 public Boolean hm = false;


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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLmd() {
        return lmd;
    }

    public void setLmd(Long lmd) {
        this.lmd = lmd;
    }

    public Boolean getIsToday() {
        return isToday;
    }

    public void setIsToday(Boolean isToday) {
        this.isToday = isToday;
    }

    public String getLm() {
        return lm;
    }

    public void setLm(String lm) {
        this.lm = lm;
    }

    public Boolean getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(Boolean isReaded) {
        this.isReaded = isReaded;
    }

    public Long getMc() {
        return mc;
    }

    public void setMc(Long mc) {
        this.mc = mc;
    }

    public Boolean getHm() {
        return hm;
    }

    public void setHm(Boolean hm) {
        this.hm = hm;
    }
}
