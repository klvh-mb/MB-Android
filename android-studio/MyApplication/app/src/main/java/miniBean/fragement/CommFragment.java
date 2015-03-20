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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
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

    private TextView noMember, commName;
    private ListView listView;
    private FeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;
    private ProgressBar spinner;
    List<CommunityCategoryMapVM> item;
    List<CommunitiesWidgetChildVM> commItem;
    private ImageView joinCommunity;
    private ImageView communityCoverPic,communityIcon;
    private CommunitiesWidgetChildVM currentCommunity;
    private String commname,nomember,icon;
    private Long commid;
    private boolean isM;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.community_activity, container, false);

        communityCoverPic = (ImageView) view.findViewById(R.id.communityPic);
        communityIcon = (ImageView) view.findViewById(R.id.commIcon);
        commName = (TextView) view.findViewById(R.id.commNameText);
        noMember = (TextView) view.findViewById(R.id.noMemberComm);
        joinCommunity = (ImageView) view.findViewById(R.id.join_community);
        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems, false);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        spinner= (ProgressBar) view.findViewById(R.id.loadCover);

        System.out.println("flagggg::::"+getArguments().getString("flag"));
        System.out.println("idchecked 2::::"+Long.parseLong(getArguments().getString("id")));

        if(!getArguments().getString("flag").equals("FromDetailActivity")) {
            commname = getArguments().getString("commName");
            commid = Long.parseLong(getArguments().getString("id"));
            isM = getArguments().getBoolean("isM", false);
            icon = getArguments().getString("icon");
            nomember = getArguments().getString("noMember");
            initializeData();
        } else {
            getCommunity(Long.parseLong(getArguments().getString("id")));
        }

        setCurrentCommunity();

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
                    if (vm.getId().equals(commid)) {
                        Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCommunity to vm [comm - " + commname + "|" + commid + "]   [vm  - " + vm.dn + "|" + vm.getId() + "]");
                        currentCommunity = vm;
                        break;
                    }
                }
            }
        }

        if (currentCommunity == null) {
            Log.e(this.getClass().getSimpleName(), "onCreateView: currentCommunity is null, comm - " + commname + "|" + commid);
        }
    }

    private void initializeData(){
        getNewsFeedByCommunityId(commid);
        Log.d(this.getClass().getSimpleName(), "initialiazeData: isM - " + isM);

        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems, false);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);

        if (!isM) {
            joinCommunity.setImageResource(R.drawable.check);
        } else {
            joinCommunity.setImageResource(R.drawable.add);
        }

        joinCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                if (!(isM)) {
                    alertDialogBuilder.setMessage("Are You Want Join This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendJoinRequest(commid);
                            Toast.makeText(getActivity(), "Community Joined", Toast.LENGTH_LONG).show();
                            ImageView image = (ImageView) view.findViewById(R.id.join_community);
                            image.setImageResource(R.drawable.add);
                            currentCommunity.setIsM(true);

                            // refresh my comms
                            LocalCache.refreshMyCommunities();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    alertDialogBuilder.setMessage("Are You Want To Leave This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveCommunity(commid);
                            Toast.makeText(getActivity(), "Community left", Toast.LENGTH_LONG).show();
                            ImageView image = (ImageView) view.findViewById(R.id.join_community);
                            image.setImageResource(R.drawable.check);
                            currentCommunity.setIsM(false);

                            // refresh my comms
                            LocalCache.refreshMyCommunities();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void getNewsFeedByCommunityId(long id) {
        AppController.api.getCommNewsfeed(id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, retrofit.client.Response response) {
                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
                commName.setText(commname);
                noMember.setText(nomember);
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(getResources().getString(R.string.base_url) + "/image/get-cover-community-image-by-id/" + getArguments().getString("id"), communityCoverPic, new SimpleImageLoadingListener() {
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

                int iconMapped = CommunityIconUtil.map(icon);
                if (iconMapped != -1) {
                    //Log.d(this.getClass().getSimpleName(), "getNewsFeedByCommunityId.api.success: replace source with local comm icon - " + commIcon);
                    communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                } else {
                    Log.d(this.getClass().getSimpleName(), "getNewsFeedByCommunityId.api.success: load comm icon from background - " + icon);
                    AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + icon, communityIcon);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                //retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void sendJoinRequest(Long id) {
        AppController.api.sendJoinRequest(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                LocalCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void leaveCommunity(Long id) {
        AppController.api.sendLeaveRequest(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                    LocalCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void getCommunity(Long id) {
        AppController.api.getCommunity(id, AppController.getInstance().getSessionId(), new Callback<CommunityVM>() {

            @Override
            public void success(CommunityVM communityVM, Response response) {
                commname = communityVM.getN();
                commid = communityVM.getId();
                isM = communityVM.isM();
                icon = communityVM.getIcon();
                nomember = String.valueOf(communityVM.getNom());
                initializeData();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}


