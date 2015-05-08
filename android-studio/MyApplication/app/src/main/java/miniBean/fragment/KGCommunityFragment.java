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
import miniBean.activity.NewKGPostActivity;
import miniBean.adapter.NewsfeedListAdapter;
import miniBean.app.AppController;
import miniBean.util.CommunityIconUtil;
import miniBean.util.DefaultValues;
import miniBean.util.ExternalLauncherUtil;
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
    private ScrollView scrollView;
    private String govtUrlValue;

    private TextView TextnumEnrollAM_N,TextnumEnrollAM_LKG,TextnumEnrollAM_UKG,TextTextnumEnrollAM_T;
    private TextView TextnumEnrollPM_N,TextnumEnrollPM_LKG,TextnumEnrollPM_UKG,TextnumEnrollPM_T;
    private TextView TextnumEnrollWD_N,TextnumEnrollWD_LKG,TextnumEnrollWD_UKG,TextnumEnrollWD_T;

    private TextView TextannualFeeAM_N,TextannualFeeAM_LKG,TextannualFeeAM_UKG;
    private TextView TextannualFeePM_N,TextannualFeePM_LKG,TextannualFeePM_UKG;
    private TextView TextannualFeeWD_N,TextannualFeeWD_LKG,TextannualFeeWD_UKG;

    private TextView TextannualCpFeeAM_N,TextannualCpFeeAM_LKG,TextannualCpFeeAM_UKG;
    private TextView TextannualCpFeePM_N,TextannualCpFeePM_LKG,TextannualCpFeePM_UKG;
    private TextView TextannualCpFeeWD_N,TextannualCpFeeWD_LKG,TextannualCpFeeWD_UKG;

    private NewsfeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;

    private ListView postList;

    private ImageView icon,couponImage,pnImage,govtImage;
    private LinearLayout gotoCommLayout,newPostLayout;

    private KindergartenVM schoolVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.kg_community_fragment, container, false);

        icon = (ImageView) view.findViewById(R.id.schoolImage);
        nameText = (TextView) view.findViewById(R.id.schoolNameText);
        districtText = (TextView) view.findViewById(R.id.distNameText);
        enNameText = (TextView) view.findViewById(R.id.enName);
        couponImage = (ImageView) view.findViewById(R.id.couponImage);
        orgValue = (TextView) view.findViewById(R.id.orgValueText);
        timeValue = (TextView) view.findViewById(R.id.timeValueText);
        typeValue = (TextView) view.findViewById(R.id.typeValueText);
        curriculumValue = (TextView) view.findViewById(R.id.curriValueText);
        pnImage = (ImageView) view.findViewById(R.id.pnImage);
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

        TextnumEnrollAM_N= (TextView) view.findViewById(R.id.TextnumEnrollAM_N);
        TextnumEnrollAM_LKG= (TextView) view.findViewById(R.id.TextnumEnrollAM_LKG);
        TextnumEnrollAM_UKG= (TextView) view.findViewById(R.id.TextnumEnrollAM_UKG);
        TextTextnumEnrollAM_T= (TextView) view.findViewById(R.id.TextTextnumEnrollAM_T);

        TextnumEnrollPM_N= (TextView) view.findViewById(R.id.TextnumEnrollPM_N);
        TextnumEnrollPM_LKG= (TextView) view.findViewById(R.id.TextnumEnrollPM_LKG);
        TextnumEnrollPM_UKG= (TextView) view.findViewById(R.id.TextnumEnrollPM_UKG);
        TextnumEnrollPM_T= (TextView) view.findViewById(R.id.TextnumEnrollPM_T);

        TextnumEnrollWD_N= (TextView) view.findViewById(R.id.TextnumEnrollWD_N);
        TextnumEnrollWD_LKG= (TextView) view.findViewById(R.id.TextnumEnrollWD_LKG);
        TextnumEnrollWD_UKG= (TextView) view.findViewById(R.id.TextnumEnrollWD_UKG);
        TextnumEnrollWD_T= (TextView) view.findViewById(R.id.TextnumEnrollWD_T);

        TextannualFeeAM_N= (TextView) view.findViewById(R.id.TextannualFeeAM_N);
        TextannualFeeAM_LKG= (TextView) view.findViewById(R.id.TextannualFeeAM_LKG);
        TextannualFeeAM_UKG= (TextView) view.findViewById(R.id.TextannualFeeAM_UKG);

        TextannualFeePM_N= (TextView) view.findViewById(R.id.TextannualFeePM_N);
        TextannualFeePM_LKG= (TextView) view.findViewById(R.id.TextannualFeePM_LKG);
        TextannualFeePM_UKG= (TextView) view.findViewById(R.id.TextannualFeePM_UKG);

        TextannualFeeWD_N= (TextView) view.findViewById(R.id.TextannualFeeWD_N);
        TextannualFeeWD_LKG= (TextView) view.findViewById(R.id.TextannualFeeWD_LKG);
        TextannualFeeWD_UKG= (TextView) view.findViewById(R.id.TextannualFeeWD_UKG);

        TextannualCpFeeAM_N= (TextView) view.findViewById(R.id.TextannualCpFeeAM_N);
        TextannualCpFeeAM_LKG= (TextView) view.findViewById(R.id.TextannualCpFeeAM_LKG);
        TextannualCpFeeAM_UKG= (TextView) view.findViewById(R.id.TextannualCpFeeAM_UKG);

        TextannualCpFeePM_N= (TextView) view.findViewById(R.id.TextannualCpFeePM_N);
        TextannualCpFeePM_LKG= (TextView) view.findViewById(R.id.TextannualCpFeePM_LKG);
        TextannualCpFeePM_UKG= (TextView) view.findViewById(R.id.TextannualCpFeePM_UKG);

        TextannualCpFeeWD_N= (TextView) view.findViewById(R.id.TextannualCpFeeWD_N);
        TextannualCpFeeWD_LKG= (TextView) view.findViewById(R.id.TextannualCpFeeWD_LKG);
        TextannualCpFeeWD_UKG= (TextView) view.findViewById(R.id.TextannualCpFeeWD_UKG);


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
                    intent.putExtra("flag", "FromKG");
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
                Intent intent = new Intent(getActivity(), NewKGPostActivity.class);
                intent.putExtra("id", getArguments().getLong("commId"));
                intent.putExtra("flag", "FromKG");
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
                ExternalLauncherUtil.launchMap(KGCommunityFragment.this.getActivity(), addressText.getText().toString());
            }
        });

        phoneValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLauncherUtil.launchCall(KGCommunityFragment.this.getActivity(), phoneValue.getText().toString());
            }
        });

        websiteValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLauncherUtil.launchBrowser(KGCommunityFragment.this.getActivity(), websiteValue.getText().toString());
            }
        });

        govtImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExternalLauncherUtil.launchBrowser(KGCommunityFragment.this.getActivity(), govtUrlValue);
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
        int iconMapped = CommunityIconUtil.map(schoolVM.getIcon());
        if (iconMapped != -1) {
            icon.setImageDrawable(getResources().getDrawable(iconMapped));
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }
        districtText.setText(schoolVM.getDis());
        typeValue.setText(schoolVM.getOrgt());
        timeValue.setText(schoolVM.getCt());
        orgValue.setText(schoolVM.getOrg());
        curriculumContent.setText(schoolVM.getCur());
        curriculumValue.setText(schoolVM.getCurt());
        //halfDayValue.setText(schoolVM.getFeeHd());
        //fullDayValue.setText(schoolVM.getFeeWd());
        //studentNum.setText(schoolVM.getNadm());
        websiteValue.setText(schoolVM.getUrl());
        phoneValue.setText(schoolVM.getPho());
        addressText.setText(schoolVM.getAdr());
        postCount.setText(schoolVM.getNop() + "");

        //entries for table 1
        TextnumEnrollAM_N.setText(schoolVM.getNadAmN());
        TextnumEnrollAM_LKG.setText(schoolVM.getNadAmL());
        TextnumEnrollAM_UKG.setText(schoolVM.getNadAmU());
        TextTextnumEnrollAM_T.setText(schoolVM.getNadAmT());

        TextnumEnrollPM_N.setText(schoolVM.getNadPmN());
        TextnumEnrollPM_LKG.setText(schoolVM.getNadPmL());
        TextnumEnrollPM_UKG.setText(schoolVM.getNadPmU());
        TextnumEnrollPM_T.setText(schoolVM.getNadPmT());

        TextnumEnrollWD_N.setText(schoolVM.getNadWdN());
        TextnumEnrollWD_LKG.setText(schoolVM.getNadWdL());
        TextnumEnrollWD_UKG.setText(schoolVM.getNadWdU());
        TextnumEnrollWD_T.setText(schoolVM.getNadWdT());

        //entries for table 2
        TextannualFeeAM_N.setText(schoolVM.getFeeAmN());
        TextannualFeeAM_LKG.setText(schoolVM.getFeeAmL());
        TextannualFeeAM_UKG.setText(schoolVM.getFeeAmU());

        TextannualFeePM_N.setText(schoolVM.getFeePmN());
        TextannualFeePM_LKG.setText(schoolVM.getFeePmL());
        TextannualFeePM_UKG.setText(schoolVM.getFeePmU());

        TextannualFeeWD_N.setText(schoolVM.getFeeWdN());
        TextannualFeeWD_LKG.setText(schoolVM.getFeeWdL());
        TextannualFeeWD_UKG.setText(schoolVM.getFeeWdU());

        //entries for table 3
        TextannualCpFeeAM_N.setText(schoolVM.getCpFeeAmN());
        TextannualCpFeeAM_LKG.setText(schoolVM.getCpFeeAmL());
        TextannualCpFeeAM_UKG.setText(schoolVM.getCpFeeAmU());

        TextannualCpFeePM_N.setText(schoolVM.getCpFeePmN());
        TextannualCpFeePM_LKG.setText(schoolVM.getCpFeePmL());
        TextannualCpFeePM_UKG.setText(schoolVM.getCpFeePmU());

        TextannualCpFeeWD_N.setText(schoolVM.getCpFeeWdN());
        TextannualCpFeeWD_LKG.setText(schoolVM.getCpFeeWdL());
        TextannualCpFeeWD_UKG.setText(schoolVM.getCpFeeWdU());


        govtUrlValue = schoolVM.getGovUrl();
        if (govtUrlValue != null) {
            govtImage.setImageResource(R.drawable.schools_gov);
        }

        if (schoolVM.isCp()) {
            couponImage.setImageResource(R.drawable.value_yes);
        } else {
            couponImage.setImageResource(R.drawable.value_no);
        }

        if (schoolVM.hasPN()) {
            pnImage.setImageResource(R.drawable.value_yes);
        } else {
            pnImage.setImageResource(R.drawable.value_no);
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

    public void setSchool(KindergartenVM schoolVM) {
        this.schoolVM = schoolVM;
    }
}

