package miniBean.app;

import java.util.ArrayList;
import java.util.List;

import miniBean.viewmodel.CommunityCategoryMapVM;

/**
 * Created by User on 2/10/15.
 */
public class LocalCache {
    public static List<CommunityCategoryMapVM> categoryMapList = new ArrayList<>();

    public static List<CommunityCategoryMapVM> getCategoryMapList() {
        return LocalCache.categoryMapList;
    }

    public static void setCategoryMapList(List<CommunityCategoryMapVM> categoryMapList) {
        LocalCache.categoryMapList = categoryMapList;
    }
}
