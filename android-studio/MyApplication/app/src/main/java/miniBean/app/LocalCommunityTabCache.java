package miniBean.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.fragment.CommunityListFragment;
import miniBean.fragment.MyCommunityNewsfeedListFragement;
import miniBean.fragment.TopicCommunityFragment;
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
    private static MyCommunityNewsfeedListFragement myNewsfeedListFragement;
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

    public static CommunityCategoryMapVM getCommunityCategoryMapByType(CommunityTabType tabType) {
        if (communityCategoryMapList.size() <= tabType.ordinal()) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "getCommunityCategoryMapByType: tabType not found");
            return null;
        }

        return communityCategoryMapList.get(tabType.ordinal());
    }

    public static void addToCommunityCategoryMapList(CommunityTabType tabType, List<CommunityCategoryMapVM> mapList) {
        CommunityCategoryMapVM communityCategoryMapVM = getCommunityCategoryMapByType(tabType);
        for (CommunityCategoryMapVM mapVM : mapList) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: map="+mapVM.getName());
            for (CommunitiesWidgetChildVM commVM : mapVM.communities) {
                Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: comm=" + commVM.getDn());
                communityCategoryMapVM.communities.add(commVM);
            }
        }
    }

    public static void addToCommunityCategoryMapList(CommunityTabType tabType, CommunitiesParentVM communitiesParent) {
        CommunityCategoryMapVM communityCategoryMapVM = getCommunityCategoryMapByType(tabType);
        for (CommunitiesWidgetChildVM commVM : communitiesParent.communities) {
            Log.d(LocalCommunityTabCache.class.getSimpleName(), "addToCommunityCategoryMapList: comm="+commVM.getDn());
            communityCategoryMapVM.communities.add(commVM);
        }
    }

    //
    // generic
    //

    public static void clear() {
        myNewsfeedListFragement = null;
        myCommunitiesParentVM = null;
        communityCategoryMapList = new ArrayList<>();
        init();
    }

    public static void setMyNewsfeedListFragement(MyCommunityNewsfeedListFragement fragment) {
        LocalCommunityTabCache.myNewsfeedListFragement = fragment;
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
        AppController.getApi().getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM communitiesParentVM, Response response) {
                Log.d(LocalCommunityTabCache.class.getSimpleName(), "refreshMyCommunities.api.success: my communities size - " + communitiesParentVM.communities.size());
                setMyCommunities(communitiesParentVM);
                notifyChange();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LocalCommunityTabCache.class.getSimpleName(), "refreshMyCommunities.api.failure", error);
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
        if (myNewsfeedListFragement != null) {
            myNewsfeedListFragement.notifyChange();
        }
        if (myCommunityFragment != null) {
            myCommunityFragment.notifyChange(getMyCommunities().communities);
        }
        for (TopicCommunityFragment fragment : topicCommunityFragments) {
            fragment.notifyChange();
        }
    }
}
