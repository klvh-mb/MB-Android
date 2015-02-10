package miniBean.viewmodel;

import java.util.List;

public class CommunityCategoryMapVM {

    public long id;
    public String name;
    public List<CommunitiesWidgetChildVM> communities;

    public CommunityCategoryMapVM() {
    }

    public CommunityCategoryMapVM(String name) {
        this.name = name;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunitiesWidgetChildVM> communities) {
        this.communities = communities;
    }
}
