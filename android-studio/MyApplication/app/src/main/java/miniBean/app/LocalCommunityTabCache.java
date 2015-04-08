package miniBean.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.fragement.CommunityListFragment;
import miniBean.fragement.TopicCommunityFragment;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocalCommunityTabCache {

    public static enum CommunityTabType {
        MY_COMMUNITY,
        TOPIC_COMMUNITY,
        ZODIAC_YEAR_COMMUNITY
    }

    // my comms
    private static CommunitiesParentVM myCommunitiesParentVM;

    // topic comms
    private static List<CommunityCategoryMapVM> communityCategoryMapList;

    // tabs to refresh
    private static CommunityListFragment myCommunityFragment;
    private static List<TopicCommunityFragment> topicCommunityFragments;

    private LocalCommunityTabCache() {}

    static {
        init();
    }

    private static void init() {
        if (communityCategoryMapList == null) {
            communityCategoryMapList = new ArrayList<>();
        }

        if (communityCategoryMapList.isEmpty()) {
            addToCommunityCategoryMapList(AppController.getInstance().getString(R.string.community_tab_my));
            addToCommunityCategoryMapList(AppController.getInstance().getString(R.string.community_tab_topic));
            addToCommunityCategoryMapList(AppController.getInstance().getString(R.string.community_tab_year));
        }

        if (topicCommunityFragments == null) {
            topicCommunityFragments = new ArrayList<>();
        }
    }

    private static void addToCommunityCategoryMapList(String categoryName) {
        CommunityCategoryMapVM communityCategoryMapVM = new CommunityCategoryMapVM(categoryName);
        communityCategoryMapVM.setCommunities(new ArrayList<CommunitiesWidgetChildVM>());
        communityCategoryMapList.add(communityCategoryMapVM);
    }

    //
    // my comms
    //

    public static CommunitiesParentVM getMyCommunities() {
        return myCommunitiesParentVM;
    }

    public static void setMyCommunities(CommunitiesParentVM communitiesParentVM) {
        filterMyCommunities(communitiesParentVM);
        myCommunitiesParentVM = communitiesParentVM;
    }

    //
    // topic comms
    //

    public static boolean isCommunityCategoryMapListEmpty() {
        for (CommunityCategoryMapVM map : communityCategoryMapList) {
            if (map.communities != null && !map.communities.isEmpty())
                return false;
        }
        return true;
    }

    public static List<CommunityCategoryMapVM> getCommunityCategoryMapList() {
        return communityCategoryMapList;
    }

    public static void addToCommunityCategoryMapList(CommunityTabType tabType, List<CommunityCategoryMapVM> mapList) {
        Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: tabType="+tabType.name());
        if (communityCategoryMapList.size() <= tabType.ordinal()) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: tabType not found");
            return;
        }

        CommunityCategoryMapVM communityCategoryMapVM = communityCategoryMapList.get(tabType.ordinal());
        for (CommunityCategoryMapVM mapVM : mapList) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: map="+mapVM.getName());
            for (CommunitiesWidgetChildVM commVM : mapVM.communities) {
                Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: comm=" + commVM.getDn());
                communityCategoryMapVM.communities.add(commVM);
            }
        }
    }

    public static void addToCommunityCategoryMapList(CommunityTabType tabType, CommunitiesParentVM communitiesParent) {
        Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: tabType="+tabType.name());
        if (communityCategoryMapList.size() <= tabType.ordinal()) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: tabType not found");
            return;
        }

        CommunityCategoryMapVM communityCategoryMapVM = communityCategoryMapList.get(tabType.ordinal());
        for (CommunitiesWidgetChildVM commVM : communitiesParent.communities) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: comm="+commVM.getDn());
            communityCategoryMapVM.communities.add(commVM);
        }
    }

    //
    // generic
    //

    public static void clear() {
        myCommunitiesParentVM = null;
        communityCategoryMapList = new ArrayList<>();
        init();
    }

    public static void setMyCommunityFragment(CommunityListFragment fragment) {
        LocalCommunityTabCache.myCommunityFragment = fragment;
    }

    public static void addTopicCommunityFragment(TopicCommunityFragment fragment) {
        if (!LocalCommunityTabCache.topicCommunityFragments.contains(fragment)) {
            LocalCommunityTabCache.topicCommunityFragments.add(fragment);
        }
    }

    public static void refreshMyCommunities() {
        AppController.api.getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM communitiesParentVM, Response response) {
                Log.d(LocalCommunityTabCache.class.getSimpleName(), "refreshMyCommunities.api.success: my communities size - " + communitiesParentVM.communities.size());
                setMyCommunities(communitiesParentVM);
                notifyChange();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private static void filterMyCommunities(CommunitiesParentVM communitiesParentVM) {
        for (int i = communitiesParentVM.communities.size()-1; i >= 0; i--) {
            if (DefaultValues.FILTER_MY_COMM_TYPE.contains(communitiesParentVM.communities.get(i).tp) ||
                    DefaultValues.FILTER_MY_COMM_TARGETING_INFO.contains(communitiesParentVM.communities.get(i).tinfo)) {
                Log.d(LocalCommunityTabCache.class.getSimpleName(), "filterMyCommunities: filtered " + communitiesParentVM.communities.get(i).dn);
                communitiesParentVM.communities.remove(i);
            }
        }
    }

    private static void notifyChange() {
        if (myCommunityFragment != null) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "notifyChange: refresh CommunityFragment");
            myCommunityFragment.notifyChange(getMyCommunities().communities);
        }
        for (TopicCommunityFragment fragment : topicCommunityFragments) {
            fragment.notifyChange();
        }
    }
}
