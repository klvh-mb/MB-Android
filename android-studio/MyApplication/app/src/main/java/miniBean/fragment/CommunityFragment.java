package miniBean.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.Listener.InfiniteScrollListener;
import miniBean.R;
import miniBean.activity.DetailActivity;
import miniBean.adapter.NewsfeedListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCommunityTabCache;
import miniBean.app.TrackedFragment;
import miniBean.util.ImageMapping;
import miniBean.util.CommunityUtil;
import miniBean.util.DefaultValues;
import miniBean.util.ImageUtil;
import miniBean.util.SharingUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.CommunityVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommunityFragment extends TrackedFragment {

    private TextView numMemberText, commNameText;
    private ListView listView;
    private NewsfeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;
    private ImageView joinImageView;
    private ImageView communityCoverPic, communityIcon;
    private CommunitiesWidgetChildVM currentCommunity;
    private Long commId;
    private View listHeader, loadingFooter;

    private CommunityUtil communityUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.community_fragment, container, false);

        communityUtil = new CommunityUtil(getActivity());

        // header
        listHeader = inflater.inflate(R.layout.community_fragment_header, null);
        communityCoverPic = (ImageView) listHeader.findViewById(R.id.communityPic);
        communityIcon = (ImageView) listHeader.findViewById(R.id.commIcon);
        commNameText = (TextView) listHeader.findViewById(R.id.commNameText);
        numMemberText = (TextView) listHeader.findViewById(R.id.noMemberComm);
        joinImageView = (ImageView) listHeader.findViewById(R.id.join_community);

        // list
        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new NewsfeedListAdapter(getActivity(), feedItems, false);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);

        // footer
        loadingFooter = inflater.inflate(R.layout.list_loading_footer, null);

        listView.addHeaderView(listHeader);
        listView.addFooterView(loadingFooter);      // need to add footer before set adapter
        listView.setAdapter(feedListAdapter);

        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        if (!getArguments().getString("flag").equals("FromDetailActivity")) {
            commId = getArguments().getLong("id");
            initData();
        } else {
            getCommunity(getArguments().getLong("id"));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = listView.getHeaderViewsCount();
                if (position < headerViewsCount) {
                    // listview header
                    return;
                }

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                CommunityPostVM post = feedListAdapter.getItem(position - headerViewsCount);
                if (post != null) {
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("commId", post.getCid());
                    intent.putExtra("flag","FromCommunity");
                    startActivity(intent);
                }
            }
        });

        listView.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, true, true) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadingFooter.setVisibility(View.VISIBLE);
                loadNewsfeed(
                        getArguments().getLong("id"),
                        feedItems.get(feedItems.size() - 1).getUt()+"",       // NOTE: use updateTime not createTime!!
                        page - 1);
            }
        });

        return view;
    }

    private void setFooterText(int text) {
        TextView footerText = (TextView) listView.findViewById(R.id.listLoadingFooterText);
        footerText.setText(text);
    }

    private void setCurrentCommunity() {
        currentCommunity = null;
        for (CommunityCategoryMapVM categoryMapVM : LocalCommunityTabCache.getCommunityCategoryMapList()) {
            if (categoryMapVM.communities != null) {
                for (CommunitiesWidgetChildVM vm : categoryMapVM.communities) {
                    if (vm.getId().equals(commId)) {
                        Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCommunity to topic comm vm [commId=" + commId + "] [vm=" + vm.dn + "|" + vm.getId() + "]");
                        currentCommunity = vm;
                        break;
                    }
                }
            }
        }

        if (currentCommunity == null) {
            Log.w(this.getClass().getSimpleName(), "onCreateView: commId not in topic comms, commId=" + commId);
            // not in topic comm, could be closed or other special comms, get directly from my communities list
            for (CommunitiesWidgetChildVM vm : LocalCommunityTabCache.getMyCommunities().communities) {
                if (vm.getId().equals(commId)) {
                    Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCommunity to my comm vm [commId=" + commId + "] [vm=" + vm.dn + "|" + vm.getId() + "]");
                    currentCommunity = vm;
                    break;
                }
            }
        }
    }

    private void initData(){
        setCurrentCommunity();
        getNewsFeedByCommunityId(currentCommunity);
        if (!currentCommunity.isM) {
            joinImageView.setImageResource(R.drawable.ic_add);
        } else {
            joinImageView.setImageResource(R.drawable.ic_check);
        }

        joinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentCommunity.isM)) {
                    communityUtil.joinCommunity(currentCommunity, joinImageView);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage(CommunityFragment.this.getString(R.string.community_leave_confirm));
                    alertDialogBuilder.setPositiveButton(CommunityFragment.this.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            communityUtil.leaveCommunity(currentCommunity, joinImageView);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(CommunityFragment.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        // actionbar
        ImageView whatsappAction = (ImageView) getActivity().findViewById(R.id.whatsappAction);
        whatsappAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtil.shareToWhatapp(currentCommunity, getActivity());
            }
        });
    }

    private void getCommunity(Long id) {
        AppController.getApi().getCommunity(id, AppController.getInstance().getSessionId(), new Callback<CommunityVM>() {
            @Override
            public void success(CommunityVM communityVM, Response response) {
                commId = communityVM.getId();
                initData();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CommunityFragment.class.getSimpleName(), "getCommunity: failure", error);
            }
        });
    }

    private void getNewsFeedByCommunityId(final CommunitiesWidgetChildVM community) {
        ImageUtil.displayCommunityCoverImage(community.id, communityCoverPic);

        AppController.getApi().getCommunityInitialPosts(community.id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, Response response) {
                if (array.getPosts().isEmpty())
                    setFooterText(R.string.list_no_posts);

                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
                commNameText.setText(community.dn);
                numMemberText.setText(community.mm + "");

                int iconMapped = ImageMapping.map(community.gi);
                if (iconMapped != -1) {
                    //Log.d(this.getClass().getSimpleName(), "getNewsFeedByCommunityId.api.success: replace source with local comm icon - " + community.gi);
                    communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                } else {
                    Log.d(this.getClass().getSimpleName(), "getNewsFeedByCommunityId.api.success: load comm icon from background - " + community.gi);
                    ImageUtil.displayRoundedCornersImage(community.gi, communityIcon);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CommunityFragment.class.getSimpleName(), "getNewsFeedByCommunityId: failure", error);
            }
        });
    }

    public void loadNewsfeed(Long id, String date, int offset) {
        setFooterText(R.string.list_loading);
        AppController.getApi().getCommunityNextPosts(id, date, AppController.getInstance().getSessionId(), new Callback<List<CommunityPostVM>>() {
            @Override
            public void success(List<CommunityPostVM> communityPostVMs, Response response) {
                Log.d(CommunityFragment.this.getClass().getSimpleName(), "loadNewsfeed.success: communityPostVMs.size=" + communityPostVMs.size());
                if (communityPostVMs == null || communityPostVMs.size() == 0) {
                    setFooterText(R.string.list_loaded_all);
                }

                feedItems.addAll(communityPostVMs);
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CommunityFragment.class.getSimpleName(), "loadNewsfeed: failure", error);
            }
        });
    }
}

