package miniBean.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.DetailActivity;
import miniBean.adapter.FeedListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.util.CommunityIconUtil;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.CommunityVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommFragment extends Fragment {

    private TextView numMemberText, commNameText;
    private ListView listView;
    private FeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;
    private ProgressBar spinner, progressBar;
    private ImageView joinImageView;
    private ImageView communityCoverPic, communityIcon;
    private CommunitiesWidgetChildVM currentCommunity;
    private Long commId;
    private String commName;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.community_activity, container, false);

        communityCoverPic = (ImageView) view.findViewById(R.id.communityPic);
        communityIcon = (ImageView) view.findViewById(R.id.commIcon);
        commNameText = (TextView) view.findViewById(R.id.commNameText);
        numMemberText = (TextView) view.findViewById(R.id.noMemberComm);
        joinImageView = (ImageView) view.findViewById(R.id.join_community);

        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems, false);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        spinner = (ProgressBar) view.findViewById(R.id.loadCover);
        progressBar = (ProgressBar) view.findViewById(R.id.progressCommunity);
        progressBar.setVisibility(View.VISIBLE);

        System.out.println("flagggg::::"+getArguments().getString("flag"));
        System.out.println("idchecked 2::::"+Long.parseLong(getArguments().getString("id")));

        if(!getArguments().getString("flag").equals("FromDetailActivity")) {
            commId = Long.parseLong(getArguments().getString("id"));
            commName = getArguments().getString("commName");
            initializeData();
        } else {
            getCommunity(Long.parseLong(getArguments().getString("id")));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                CommunityPostVM post = feedListAdapter.getItem(position);
                intent.putExtra("postId", post.getId());
                intent.putExtra("commId", post.getCid());
                intent.putExtra("comments", post.getN_c());

                startActivity(intent);
            }
        });

        return view;
    }

    private void setCurrentCommunity() {
        //Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCommunity, comm - " + commname + "|" + commid);
        //Log.d(this.getClass().getSimpleName(), "onCreateView: LocalCache.getCommunityCategoryMapList() size - " + LocalCache.getCommunityCategoryMapList().size());

        currentCommunity = null;
        for (CommunityCategoryMapVM categoryMapVM : LocalCache.getCommunityCategoryMapList()) {
            if (categoryMapVM.communities != null) {
                for (CommunitiesWidgetChildVM vm : categoryMapVM.communities) {
                    if (vm.getId().equals(commId)) {
                        Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCommunity to topic comm vm [comm - " + commName + "|" + commId + "]   [vm  - " + vm.dn + "|" + vm.getId() + "]");
                        currentCommunity = vm;
                        break;
                    }
                }
            }
        }

        if (currentCommunity == null) {
            Log.w(this.getClass().getSimpleName(), "onCreateView: commId not in topic comms, comm - " + commName + "|" + commId);
            // not in topic comm, could be closed or other special comms, get directly from my communities list
            for (CommunitiesWidgetChildVM vm : LocalCache.getMyCommunitiesParentVM().communities) {
                if (vm.getId().equals(commId)) {
                    Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCommunity to my comm vm [comm - " + commName + "|" + commId + "]   [vm  - " + vm.dn + "|" + vm.getId() + "]");
                    currentCommunity = vm;
                    break;
                }
            }
        }
    }

    private void initializeData(){
        setCurrentCommunity();

        getNewsFeedByCommunityId(currentCommunity);

        Log.d(this.getClass().getSimpleName(), "initialiazeData: community - " + commName);
        if (!currentCommunity.isM) {
            joinImageView.setImageResource(R.drawable.check);
        } else {
            joinImageView.setImageResource(R.drawable.add);
        }

        joinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentCommunity.isM)) {
                    joinCommunity(currentCommunity, joinImageView);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage(CommFragment.this.getString(R.string.community_leave_confirm));
                    alertDialogBuilder.setPositiveButton(CommFragment.this.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveCommunity(currentCommunity, joinImageView);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(CommFragment.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void getCommunity(Long id) {
        AppController.api.getCommunity(id, AppController.getInstance().getSessionId(), new Callback<CommunityVM>() {

            @Override
            public void success(CommunityVM communityVM, Response response) {
                commName = communityVM.getN();
                commId = communityVM.getId();
                initializeData();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void getNewsFeedByCommunityId(final CommunitiesWidgetChildVM community) {
        AppController.api.getCommNewsfeed(community.id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, Response response) {
                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
                commNameText.setText(community.dn);
                numMemberText.setText(community.mm+"");

                int rounded_value = 0;
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).displayer(new RoundedBitmapDisplayer(rounded_value)).build();
                ImageLoader.getInstance().displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-community-image-by-id/" + getArguments().getString("id"), communityCoverPic, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        spinner.setVisibility(View.GONE);
                    }
                });

                int iconMapped = CommunityIconUtil.map(community.gi);
                if (iconMapped != -1) {
                    //Log.d(this.getClass().getSimpleName(), "getNewsFeedByCommunityId.api.success: replace source with local comm icon - " + community.gi);
                    communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                } else {
                    Log.d(this.getClass().getSimpleName(), "getNewsFeedByCommunityId.api.success: load comm icon from background - " + community.gi);
                    AppController.getImageLoader().displayImage(getResources().getString(R.string.base_url) + community.gi, communityIcon);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                //retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void joinCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinImageView) {
        AppController.api.sendJoinRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(CommFragment.this.getActivity(), CommFragment.this.getString(R.string.community_join_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(true);
                joinImageView.setImageResource(R.drawable.add);

                LocalCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(CommFragment.this.getActivity(), CommFragment.this.getString(R.string.community_join_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }

    public void leaveCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinImageView) {
        AppController.api.sendLeaveRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(CommFragment.this.getActivity().getBaseContext(), CommFragment.this.getString(R.string.community_leave_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(false);
                joinImageView.setImageResource(R.drawable.check);

                LocalCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(CommFragment.this.getActivity().getBaseContext(), CommFragment.this.getString(R.string.community_leave_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }
}


