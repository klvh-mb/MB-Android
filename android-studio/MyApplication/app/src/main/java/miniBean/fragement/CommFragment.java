package miniBean.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import miniBean.activity.CommunityActivity;
import miniBean.activity.DetailActivity;
import miniBean.adapter.CommunityListAdapter;
import miniBean.adapter.FeedListAdapter;
import miniBean.app.AppController;
import miniBean.app.LocalCache;
import miniBean.app.MyApi;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunitiesParentVM;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import miniBean.viewmodel.CommunityCategoryMapVM;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class CommFragment extends Fragment {

    public SharedPreferences session = null;
    public MyApi api;
    TextView noPost, noMember, commName;
    ListView listView;
    FeedListAdapter feedListAdapter;
    List<CommunityPostVM> feedItems;
    ProgressBar progressBar, spinner;
    List<CommunityCategoryMapVM> item;
    List<CommunitiesWidgetChildVM> commItem;
    ImageView imageView, backImage;
    ImageView communityCoverPic,communityIcon;
    CommunitiesWidgetChildVM currentCommunity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.community_activity, container, false);
        session = getActivity().getSharedPreferences("prefs", 0);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient()).build();


        noPost = (TextView) view.findViewById(R.id.noPostComm);
        noMember = (TextView) view.findViewById(R.id.noMemberComm);
        commName = (TextView) view.findViewById(R.id.commNameText);
        imageView = (ImageView) view.findViewById(R.id.join_community);
        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);
         communityCoverPic = (ImageView) view.findViewById(R.id.communityPic);
         communityIcon = (ImageView) view.findViewById(R.id.commIconView);
         spinner= (ProgressBar) view.findViewById(R.id.loadCover);
        System.out.print("commfragment:::::::::::::::::::::::::::");
        System.out.println("id:::::::::"+getArguments().getString("id"));
        System.out.println("ism:::::::"+ getArguments().getBoolean("isM", false));
        System.out.println("nomember:::::::"+(getArguments().getString("noMember")));
        System.out.println("nomember:::::::"+ (getArguments().getString("noPost")));
        System.out.println("nomember:::::::"+(getArguments().getString("noMember")));

        for (CommunityCategoryMapVM categoryMapVM : LocalCache.categoryMapList) {
            if (categoryMapVM.communities != null)
                for (CommunitiesWidgetChildVM vm : categoryMapVM.communities) {
                    if (vm.getId() == Long.parseLong(getArguments().getString("id"))) {
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
                System.out.println("feeddd::" + post.getN_c());
                intent.putExtra("comments", post.getN_c());

                startActivity(intent);
            }
        });
        getNewsFeedByCommuityId(Long.parseLong(getArguments().getString("id")));
        System.out.println("::::::::::::::::::::::::::::::::::::: boolean  " + getArguments().getBoolean("isM", false));
        if (getArguments().getBoolean("isM", false))
            imageView.setImageResource(R.drawable.add);
      /*  backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView = (ListView) view.findViewById(R.id.listCommunityFeed);
        listView.setAdapter(feedListAdapter);

        getNewsFeedByCommuityId(Long.parseLong(getArguments().getString("id")));
        if (!getArguments().getBoolean("isM", false)) {
            imageView.setImageResource(R.drawable.check);
        } else {
            imageView.setImageResource(R.drawable.add);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(getArguments().getBoolean("isM", false))) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Are You Want Join This Community?");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendJoinRequest(Long.parseLong(getArguments().getString("id")));
                            Toast.makeText(getActivity(), "Community Joined", Toast.LENGTH_LONG).show();
                            ImageView image = (ImageView) view.findViewById(R.id.join_community);
                            image.setImageResource(R.drawable.add);
                            currentCommunity.setIsM(true);

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
                            leaveCommunity(Long.parseLong(getArguments().getString("id")));
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
        return view;
    }

    private void getNewsFeedByCommuityId(long id) {
        AppController.api.getCommNewsfeed(id, session.getString("sessionID", null), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, retrofit.client.Response response) {
                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
                commName.setText(getArguments().getString("commName"));
                noMember.setText(getArguments().getString("noMember"));
                noPost.setText(getArguments().getString("noPost"));
                //ImageView communityCoverPic = (ImageView)vi findViewById(R.id.communityPic);
                //ImageView communityIcon = (ImageView) findViewById(R.id.commIconView);
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
                AppController.mImageLoader.displayImage(getResources().getString(R.string.base_url) + getArguments().getString("icon"), communityIcon);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void sendJoinRequest(Long id) {
        AppController.api.sendJoinRequest(id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }

    public void leaveCommunity(Long id) {
        AppController.api.sendLeaveRequest(id, session.getString("sessionID", null), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace(); //to see if you have errors
            }
        });
    }
  }


