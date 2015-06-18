package miniBean.viewmodel;

public class MessageVM {
	 public String snm;
	 public Long suid;
	 public Long id;
	 public long cd;
	 public String txt;
	 public boolean hasImage=false;
	 public Long imgs;

    public String getSnm() {
        return snm;
    }

    public void setSnm(String snm) {
        this.snm = snm;
    }

    public Long getSuid() {
        return suid;
    }

    public void setSuid(Long suid) {
        this.suid = suid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCd() {
        return cd;
    }

    public void setCd(long cd) {
        this.cd = cd;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Long getImgs() {
        return imgs;
    }

    public void setImgs(Long imgs) {
        this.imgs = imgs;
    }
}
