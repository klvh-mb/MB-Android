package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import miniBean.Listener.InfiniteScrollListener;
import miniBean.R;
import miniBean.activity.DetailActivity;
import miniBean.activity.NewPostActivity;
import miniBean.adapter.NewsfeedListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KGCommunityFragment extends MyFragment {

    private TextView nameText,districtText,enNameText,orgValue,typeValue,timeValue,curriculumValue;
    private TextView studentNum,halfDayValue,fullDayValue,curriculumContent,addressText,phoneValue;
    private TextView websiteValue,postCount;
    private ImageView urlValueImage,editAction;
    private ScrollView scrollView;

    private NewsfeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;

    private ListView postList;

    private ImageView couponImage,govtImage;
    private LinearLayout gotoCommLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.kg_community_fragment, container, false);

        nameText = (TextView) view.findViewById(R.id.schoolNameText);
        districtText = (TextView) view.findViewById(R.id.distNameText);
        enNameText = (TextView) view.findViewById(R.id.enName);
        couponImage = (ImageView) view.findViewById(R.id.couponImage);
        orgValue = (TextView) view.findViewById(R.id.orgValueText);
        timeValue = (TextView) view.findViewById(R.id.timeValueText);
        typeValue = (TextView) view.findViewById(R.id.typeValueText);
        curriculumValue = (TextView) view.findViewById(R.id.curriValueText);
        studentNum = (TextView) view.findViewById(R.id.studentValue);
        halfDayValue = (TextView) view.findViewById(R.id.halfDayValue);
        fullDayValue = (TextView) view.findViewById(R.id.fullDayValue);
        curriculumContent = (TextView) view.findViewById(R.id.contentText);
        addressText = (TextView) view.findViewById(R.id.addressValue);
        phoneValue = (TextView) view.findViewById(R.id.phoneValue);
        websiteValue = (TextView) view.findViewById(R.id.websiteValue);
        urlValueImage = (ImageView) view.findViewById(R.id.govtImage);
        editAction = (ImageView) view.findViewById(R.id.editAction);
        postList = (ListView) view.findViewById(R.id.postList);
        postCount = (TextView) view.findViewById(R.id.postCount);
        gotoCommLayout = (LinearLayout) view.findViewById(R.id.gotoCommLayout);
        scrollView = (ScrollView) view.findViewById(R.id.scrollview);
        govtImage = (ImageView) view.findViewById(R.id.govtImage);

        getKGInfo(getArguments().getLong("id"));

        getNewsFeedByCommunityId(getArguments().getLong("commId"));

        if(getArguments().getString("flag").equals("FromCommentImage")){
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }

        feedItems = new ArrayList<CommunityPostVM>();
        feedListAdapter = new NewsfeedListAdapter(getActivity(), feedItems, false);
        postList.setAdapter(feedListAdapter);

        postList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                CommunityPostVM post = (CommunityPostVM) feedListAdapter.getItem(position);
                if (post != null) {
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("commId", post.getCid());
                    intent.putExtra("id", getArguments().getLong("id"));
                    intent.putExtra("commId", getArguments().getLong("commId"));
                    intent.putExtra("flag", "FromSchool");
                    startActivity(intent);
                }
            }
        });

        postList.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, false, true) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadNewsfeed(
                        getArguments().getLong("id"),
                        feedItems.get(feedItems.size() - 1).getUt() + "",       // NOTE: use updateTime not createTime!!
                        page - 1);
            }
        });


        editAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewPostActivity.class);
                intent.putExtra("id",String.valueOf(getArguments().getLong("commId")));
                intent.putExtra("flag","FromSchool");
                startActivity(intent);
            }
        });



        gotoCommLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        return view;
    }


    private void getKGInfo(Long id) {
        AppController.getApi().getKGInfo(id, AppController.getInstance().getSessionId(), new Callback<KindergartenVM>() {
            @Override
            public void success(KindergartenVM vm, Response response) {
                nameText.setText(vm.getN());
                if (StringUtils.isEmpty(vm.getNe())) {
                    enNameText.setVisibility(View.GONE);
                } else {
                    enNameText.setText(vm.getNe());
                    enNameText.setVisibility(View.VISIBLE);
                }
                districtText.setText(vm.getDis());
                typeValue.setText(vm.getOrgt());
                timeValue.setText(vm.getCt());
                orgValue.setText(vm.getOrg());
                curriculumContent.setText(vm.getCur());
                curriculumValue.setText(vm.getCurt());
                //halfDayValue.setText(vm.getFeeHd());
                //fullDayValue.setText(vm.getFeeWd());
                //studentNum.setText(vm.getNadm());
                websiteValue.setText(vm.getUrl());
                phoneValue.setText(vm.getPho());
                addressText.setText(vm.getAdr());
                postCount.setText(vm.getNop() + "");

                if (vm.getGovUrl() != null) {
                    govtImage.setImageResource(R.drawable.schools_gov);
                }

                if (vm.isCp()) {
                    couponImage.setImageResource(R.drawable.value_yes);
                } else {
                    couponImage.setImageResource(R.drawable.value_no);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();

            }
        });

    }


    private void getNewsFeedByCommunityId(Long commId) {
        AppController.getApi().getCommunityInitialPosts(commId, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, Response response) {
                System.out.println("array:::::"+array.getPosts().size());
                feedItems.addAll(array.getPosts());
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void loadNewsfeed(Long id, String date, int offset) {
        AppController.getApi().getCommunityNextPosts(id, date, AppController.getInstance().getSessionId(), new Callback<List<CommunityPostVM>>() {
            @Override
            public void success(List<CommunityPostVM> communityPostVMs, Response response) {
                System.out.println("on scroll::::::::::::");
                feedItems.addAll(communityPostVMs);
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

}

