package miniBean.viewmodel;


public class CommunityPostCommentVM {
    public Long id;
    public Long oid;
    public String on;
    public Long cd;
    public boolean hasImage = false;
    public Long[] imgs;
    public String d;
    public int nol;
    public String attr;
    public int n;

    public boolean isO = false;
    public boolean isLike = false;     // filled outside
    public boolean isPost;

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean isPost) {
        this.isPost = isPost;
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

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public Long getCd() {
        return cd;
    }

    public void setCd(Long cd) {
        this.cd = cd;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Long[] getImgs() {
        return imgs;
    }

    public void setImgs(Long[] imgs) {
        this.imgs = imgs;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public int getNol() {
        return nol;
    }

    public void setNol(int nol) {
        this.nol = nol;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public boolean isO() {
        return isO;
    }

    public void setO(boolean isO) {
        this.isO = isO;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean isLike) {
        this.isLike = isLike;
    }
}