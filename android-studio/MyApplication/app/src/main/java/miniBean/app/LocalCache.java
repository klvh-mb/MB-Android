package miniBean.app;

import java.util.ArrayList;
import java.util.List;

import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;

public class LocalCache {
    private static List<CommunityCategoryMapVM> communityCategoryMapList = new ArrayList<CommunityCategoryMapVM>();
    private static CommunitiesParentVM myCommunitiesParentVM;
    private static Boolean dirty;

    public static Boolean isDirty() {
        return dirty;
    }

    public static void setDirty(Boolean dirty) {
        LocalCache.dirty = dirty;
    }

    public static CommunitiesParentVM getMyCommunitiesParentVM() {
        return myCommunitiesParentVM;
    }

    public static void setMyCommunitiesParentVM(CommunitiesParentVM communitiesParentVM) {
        myCommunitiesParentVM = communitiesParentVM;
    }

    public static List<CommunityCategoryMapVM> getCommunityCategoryMapList() {
        return LocalCache.communityCategoryMapList;
    }

    public static void addCommunityCategoryMapToList(CommunityCategoryMapVM communityCategoryMap) {
        LocalCache.communityCategoryMapList.add(communityCategoryMap);
    }

    public static void clear() {
        communityCategoryMapList = new ArrayList<CommunityCategoryMapVM>();
        myCommunitiesParentVM = null;
        dirty = false;
    }
}
