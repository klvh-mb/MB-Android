package miniBean.viewmodel;

import java.util.List;

public class CommunitiesParentVM {
    public int sn;
    public List<CommunitiesWidgetChildVM> communities;
    public Boolean isMore;


    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunitiesWidgetChildVM> communities) {
        this.communities = communities;
    }

    public Boolean getIsMore() {
        return isMore;
    }

    public void setIsMore(Boolean isMore) {
        this.isMore = isMore;
    }
}
