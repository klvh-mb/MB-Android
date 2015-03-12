package miniBean.app;

import java.util.ArrayList;
import java.util.List;

import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;

public class LocalCache {
    public static List<CommunityCategoryMapVM> categoryMapList = new ArrayList<>();
    public static CommunitiesParentVM CommunitiesParentVM;

    public static Boolean getDirty() {
        return dirty;
    }

    public static void setDirty(Boolean dirty) {
        LocalCache.dirty = dirty;
    }

    public static Boolean dirty;

    public static CommunitiesParentVM getCommunitiesParentVM() {
        return CommunitiesParentVM;
    }

    public static void setCommunitiesParentVM(CommunitiesParentVM communitiesParentVM) {
        CommunitiesParentVM = communitiesParentVM;
    }

    public static List<CommunityCategoryMapVM> getCategoryMapList() {
        return LocalCache.categoryMapList;
    }

    public static void setCategoryMapList(List<CommunityCategoryMapVM> categoryMapList) {
        LocalCache.categoryMapList = categoryMapList;
    }

}
