package miniBean.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import miniBean.fragement.CommunityListFragment;
import miniBean.fragement.TopicCommunityFragment;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocalCache {

    // my comms
    private static CommunitiesParentVM myCommunitiesParentVM;

    // topic comms
    private static List<CommunityCategoryMapVM> topicCommunityCategoryMapList = new ArrayList<>();

    // my comms
    private static CommunitiesParentVM zodiacYearCommunitiesParentVM;

    private static boolean dirty = false;

    private static CommunityListFragment myCommunityFragment;
    private static List<TopicCommunityFragment> topicCommunityFragments = new ArrayList<>();

    public static boolean isDirty() {
        return dirty;
    }

    public static void setDirty(Boolean dirty) {
        LocalCache.dirty = dirty;
    }

    public static CommunitiesParentVM getMyCommunitiesParentVM() {
        return myCommunitiesParentVM;
    }

    public static void setMyCommunitiesParentVM(CommunitiesParentVM communitiesParentVM) {
        filterMyCommunities(communitiesParentVM);
        myCommunitiesParentVM = communitiesParentVM;
    }

    public static List<CommunityCategoryMapVM> getTopicCommunityCategoryMapList() {
        return LocalCache.topicCommunityCategoryMapList;
    }

    public static void clearTopicCommunityCategoryMapList() {
        topicCommunityCategoryMapList.clear();
    }

    public static void addTopicCommunityCategoryMapToList(CommunityCategoryMapVM topicCommunityCategoryMap) {
        topicCommunityCategoryMapList.add(topicCommunityCategoryMap);
    }

    public static void clear() {
        topicCommunityCategoryMapList = new ArrayList<>();
        myCommunitiesParentVM = null;
        dirty = false;
    }

    public static void setMyCommunityFragment(CommunityListFragment fragment) {
        LocalCache.myCommunityFragment = fragment;
    }

    public static void addTopicCommunityFragment(TopicCommunityFragment fragment) {
        if (!LocalCache.topicCommunityFragments.contains(fragment)) {
            LocalCache.topicCommunityFragments.add(fragment);
        }
    }

    public static void refreshMyCommunities() {
        AppController.api.getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM communitiesParentVM, Response response) {
                Log.d("LocalCache", "refreshMyCommunities.api.success: my communities size - " + communitiesParentVM.communities.size());
                setMyCommunitiesParentVM(communitiesParentVM);

                // notify change to fragments...
                if (myCommunityFragment != null) {
                    Log.d("LocalCache", "refreshMyCommunities.api.success: refresh CommunityFragment");
                    myCommunityFragment.notifyChange(communitiesParentVM.communities);
                }
                for (TopicCommunityFragment fragment : topicCommunityFragments) {
                    fragment.notifyChange();
                }

                dirty = true;
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
                Log.d("LocalCache", "filterMyCommunities: filtered " + communitiesParentVM.communities.get(i).dn);
                communitiesParentVM.communities.remove(i);
            }
        }
    }
}
