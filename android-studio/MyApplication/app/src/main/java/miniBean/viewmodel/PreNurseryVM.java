package miniBean.viewmodel;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/7/14
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class PreNurseryVM {
    private static final String MAPURL_PREFIX = "http://maps.google.com.hk/maps?q=";

     public Long id;
     public Long commId;
    public String icon;
      public boolean myd;
      public String dis;
      public Long disId;
        public String n;
       public String ne;
      public String url;
      public String govUrl;
      public String pho;
     public String phol;
       public String em;
      public String adr;
      public String map;

      public String org;
      public String orgt;
      public boolean cp;
      public String ct;
      public String cur;
      public String curt;
      public String feeHd;
      public String feeWd;
     public String nadm;
    
     public int nop;
     public int nol;
    public int nov;
     public int nob;

     public String appTxt = null;

     public boolean isLike = false;
     public boolean isBookmarked = false;

    public PreNurseryVM() {
    }

    public PreNurseryVM(String name){
        nadm = name;
    }

    public static String getMapurlPrefix() {
        return MAPURL_PREFIX;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommId() {
        return commId;
    }

    public void setCommId(Long commId) {
        this.commId = commId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isMyd() {
        return myd;
    }

    public void setMyd(boolean myd) {
        this.myd = myd;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public Long getDisId() {
        return disId;
    }

    public void setDisId(Long disId) {
        this.disId = disId;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getNe() {
        return ne;
    }

    public void setNe(String ne) {
        this.ne = ne;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGovUrl() {
        return govUrl;
    }

    public void setGovUrl(String govUrl) {
        this.govUrl = govUrl;
    }

    public String getPho() {
        return pho;
    }

    public void setPho(String pho) {
        this.pho = pho;
    }

    public String getPhol() {
        return phol;
    }

    public void setPhol(String phol) {
        this.phol = phol;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrgt() {
        return orgt;
    }

    public void setOrgt(String orgt) {
        this.orgt = orgt;
    }

    public boolean isCp() {
        return cp;
    }

    public void setCp(boolean cp) {
        this.cp = cp;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

    public String getCurt() {
        return curt;
    }

    public void setCurt(String curt) {
        this.curt = curt;
    }

    public String getFeeHd() {
        return feeHd;
    }

    public void setFeeHd(String feeHd) {
        this.feeHd = feeHd;
    }

    public String getFeeWd() {
        return feeWd;
    }

    public void setFeeWd(String feeWd) {
        this.feeWd = feeWd;
    }

    public String getNadm() {
        return nadm;
    }

    public void setNadm(String nadm) {
        this.nadm = nadm;
    }

    public int getNop() {
        return nop;
    }

    public void setNop(int nop) {
        this.nop = nop;
    }

    public int getNol() {
        return nol;
    }

    public void setNol(int nol) {
        this.nol = nol;
    }

    public int getNov() {
        return nov;
    }

    public void setNov(int nov) {
        this.nov = nov;
    }

    public int getNob() {
        return nob;
    }

    public void setNob(int nob) {
        this.nob = nob;
    }

    public String getAppTxt() {
        return appTxt;
    }

    public void setAppTxt(String appTxt) {
        this.appTxt = appTxt;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean isLike) {
        this.isLike = isLike;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}
