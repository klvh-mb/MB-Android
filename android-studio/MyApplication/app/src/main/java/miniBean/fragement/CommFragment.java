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
import miniBean.viewmodel.CommunitiesParentVM;
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
    private ImageView imageView;
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
        communityIcon = (ImageView) view.findViewById(R.id.communityIcon);
        commName = (TextView) view.findViewById(R.id.commNameText);
        noMember = (TextView) view.findViewById(R.id.noMemberComm);
        imageView = (ImageView) view.findViewById(R.id.join_community);
        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems, false);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);

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
            getCommunities(Long.parseLong(getArguments().getString("id")));
        }

        for (CommunityCategoryMapVM categoryMapVM : LocalCache.getCommunityCategoryMapList()) {
            if (categoryMapVM.communities != null)
                for (CommunitiesWidgetChildVM vm : categoryMapVM.communities) {
                    if (vm.getId() == commid) {
                        currentCommunity = vm;
                    }
                }
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

    private void initializeData(){
        getNewsFeedByCommunityId(commid);
        Log.d("initializeData", "isM: "+isM);

        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems, false);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);

        if (!isM) {
            imageView.setImageResource(R.drawable.check);
        } else {
            imageView.setImageResource(R.drawable.add);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(isM)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Are You Want Join This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendJoinRequest(commid);
                            Toast.makeText(getActivity(), "Community Joined", Toast.LENGTH_LONG).show();
                            ImageView image = (ImageView) view.findViewById(R.id.join_community);
                            image.setImageResource(R.drawable.add);
                            currentCommunity.setIsM(true);
                            AppController.api.getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>(){

                                @Override
                                public void success(CommunitiesParentVM communitiesParentVM, Response response) {
                                    LocalCache.setMyCommunitiesParentVM(communitiesParentVM);
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            });

                        }
                    });
                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Are You Want To Leave This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveCommunity(commid);
                            Toast.makeText(getActivity(), "Community left", Toast.LENGTH_LONG).show();
                            ImageView image = (ImageView) view.findViewById(R.id.join_community);
                            image.setImageResource(R.drawable.check);
                            currentCommunity.setIsM(false);

                        }
                    });
                    alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "CANCEL", Toast.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
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
                    //Log.d("getView", "replace source with local comm icon - " + commIcon);
                    communityIcon.setImageDrawable(getResources().getDrawable(iconMapped));
                } else {
                    Log.d("getView", "load comm icon from background - " + icon);
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
                getMyCommunities();
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
                    getMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void getMyCommunities() {
        AppController.api.getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>(){

            @Override
            public void success(CommunitiesParentVM communitiesParentVM, Response response) {
                LocalCache.setMyCommunitiesParentVM(communitiesParentVM);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void getCommunities(Long id) {
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


