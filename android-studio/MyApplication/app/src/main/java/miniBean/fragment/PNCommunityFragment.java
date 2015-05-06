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
import miniBean.activity.NewPNPostActivity;
import miniBean.adapter.NewsfeedListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.util.ExternalLauncherUtil;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PNCommunityFragment extends MyFragment {

    private TextView nameText,districtText,enNameText,orgValue,typeValue,timeValue,curriculumValue;
    private TextView studentNum,halfDayValue,fullDayValue,curriculumContent,addressText,phoneValue;
    private TextView websiteValue,postCount;
    private ScrollView scrollView;
    private String govtUrlValue;

    private NewsfeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;

    private ListView postList;

    private ImageView couponImage,govtImage;
    private LinearLayout gotoCommLayout,newPostLayout;

    private PreNurseryVM schoolVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pn_community_fragment, container, false);

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
        newPostLayout = (LinearLayout) view.findViewById(R.id.newPostLayout);
        postList = (ListView) view.findViewById(R.id.postList);
        postCount = (TextView) view.findViewById(R.id.postCount);
        gotoCommLayout = (LinearLayout) view.findViewById(R.id.gotoCommLayout);
        scrollView = (ScrollView) view.findViewById(R.id.scrollview);
        govtImage = (ImageView) view.findViewById(R.id.govtImage);

        initInfo();

        getNewsFeedByCommunityId(getArguments().getLong("commId"));

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
                    intent.putExtra("flag", "FromPN");
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

        newPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewPNPostActivity.class);
                intent.putExtra("id", getArguments().getLong("commId"));
                intent.putExtra("flag", "FromPN");
                startActivity(intent);
            }
        });

        gotoCommLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLauncherUtil.launchMap(PNCommunityFragment.this.getActivity(), addressText.getText().toString());
            }
        });

        phoneValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLauncherUtil.launchCall(PNCommunityFragment.this.getActivity(), phoneValue.getText().toString());
            }
        });

        websiteValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLauncherUtil.launchBrowser(PNCommunityFragment.this.getActivity(), websiteValue.getText().toString());
            }
        });

        govtImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLauncherUtil.launchBrowser(PNCommunityFragment.this.getActivity(), govtUrlValue);
            }
        });

        return view;
    }

    private void initInfo() {
        nameText.setText(schoolVM.getN());
        if (StringUtils.isEmpty(schoolVM.getNe())) {
            enNameText.setVisibility(View.GONE);
        } else {
            enNameText.setText(schoolVM.getNe());
            enNameText.setVisibility(View.VISIBLE);
        }
        districtText.setText(schoolVM.getDis());
        typeValue.setText(schoolVM.getOrgt());
        timeValue.setText(schoolVM.getCt());
        orgValue.setText(schoolVM.getOrg());
        curriculumContent.setText(schoolVM.getCur());
        curriculumValue.setText(schoolVM.getCurt());
        halfDayValue.setText(schoolVM.getFeeHd());
        fullDayValue.setText(schoolVM.getFeeWd());
        studentNum.setText(schoolVM.getNadm());
        websiteValue.setText(schoolVM.getUrl());
        phoneValue.setText(schoolVM.getPho());
        addressText.setText(schoolVM.getAdr());
        postCount.setText(schoolVM.getNop() + "");

        govtUrlValue = schoolVM.getGovUrl();
        if (govtUrlValue != null) {
            govtImage.setImageResource(R.drawable.schools_gov);
        }

        if (schoolVM.isCp()) {
            couponImage.setImageResource(R.drawable.value_yes);
        } else {
            couponImage.setImageResource(R.drawable.value_no);
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                if ("FromCommentImage".equals(getArguments().getString("flag"))) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                } else {
                    scrollView.fullScroll(View.FOCUS_UP);
                }
            }
        });
    }

    private void getNewsFeedByCommunityId(Long commId) {
        AppController.getApi().getCommunityInitialPosts(commId, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, Response response) {
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
                feedItems.addAll(communityPostVMs);
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void setSchool(PreNurseryVM schoolVM) {
        this.schoolVM = schoolVM;
    }
}

