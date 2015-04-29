package miniBean.viewmodel;

public class KindergartenVM {
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

    public String feeAmN;
    public String feePmN;
    public String feeWdN;
    public String feeAmL;
    public String feePmL;
    public String feeWdL;
    public String feeAmU;
    public String feePmU;
    public String feeWdU;

    public String nadAmN;
    public String nadPmN;
    public String nadWdN;
    public String nadAmL;
    public String nadPmL;
    public String nadWdL;
    public String nadAmU;
    public String nadPmU;
    public String nadWdU;

    public int nop;
    public int nol;
    public int nov;
    public int nob;

    public String appTxt = null;

    public boolean isLike = false;
    public boolean isBookmarked = false;


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

    public String getFeeAmN() {
        return feeAmN;
    }

    public void setFeeAmN(String feeAmN) {
        this.feeAmN = feeAmN;
    }

    public String getFeePmN() {
        return feePmN;
    }

    public void setFeePmN(String feePmN) {
        this.feePmN = feePmN;
    }

    public String getFeeWdN() {
        return feeWdN;
    }

    public void setFeeWdN(String feeWdN) {
        this.feeWdN = feeWdN;
    }

    public String getFeeAmL() {
        return feeAmL;
    }

    public void setFeeAmL(String feeAmL) {
        this.feeAmL = feeAmL;
    }

    public String getFeePmL() {
        return feePmL;
    }

    public void setFeePmL(String feePmL) {
        this.feePmL = feePmL;
    }

    public String getFeeWdL() {
        return feeWdL;
    }

    public void setFeeWdL(String feeWdL) {
        this.feeWdL = feeWdL;
    }

    public String getFeeAmU() {
        return feeAmU;
    }

    public void setFeeAmU(String feeAmU) {
        this.feeAmU = feeAmU;
    }

    public String getFeePmU() {
        return feePmU;
    }

    public void setFeePmU(String feePmU) {
        this.feePmU = feePmU;
    }

    public String getFeeWdU() {
        return feeWdU;
    }

    public void setFeeWdU(String feeWdU) {
        this.feeWdU = feeWdU;
    }

    public String getNadAmN() {
        return nadAmN;
    }

    public void setNadAmN(String nadAmN) {
        this.nadAmN = nadAmN;
    }

    public String getNadPmN() {
        return nadPmN;
    }

    public void setNadPmN(String nadPmN) {
        this.nadPmN = nadPmN;
    }

    public String getNadWdN() {
        return nadWdN;
    }

    public void setNadWdN(String nadWdN) {
        this.nadWdN = nadWdN;
    }

    public String getNadAmL() {
        return nadAmL;
    }

    public void setNadAmL(String nadAmL) {
        this.nadAmL = nadAmL;
    }

    public String getNadPmL() {
        return nadPmL;
    }

    public void setNadPmL(String nadPmL) {
        this.nadPmL = nadPmL;
    }

    public String getNadWdL() {
        return nadWdL;
    }

    public void setNadWdL(String nadWdL) {
        this.nadWdL = nadWdL;
    }

    public String getNadAmU() {
        return nadAmU;
    }

    public void setNadAmU(String nadAmU) {
        this.nadAmU = nadAmU;
    }

    public String getNadPmU() {
        return nadPmU;
    }

    public void setNadPmU(String nadPmU) {
        this.nadPmU = nadPmU;
    }

    public String getNadWdU() {
        return nadWdU;
    }

    public void setNadWdU(String nadWdU) {
        this.nadWdU = nadWdU;
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