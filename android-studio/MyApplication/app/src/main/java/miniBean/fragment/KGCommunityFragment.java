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
import android.widget.TableLayout;
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

    private TextView numEnrollAM_N,numEnrollAM_LKG,numEnrollAM_UKG,numEnrollAM_T;
    private TextView numEnrollPM_N,numEnrollPM_LKG,numEnrollPM_UKG,numEnrollPM_T;
    private TextView numEnrollWD_N,numEnrollWD_LKG,numEnrollWD_UKG,numEnrollWD_T;

    private TextView annualFeeAM_N,annualFeeAM_LKG,annualFeeAM_UKG;
    private TextView annualFeePM_N,annualFeePM_LKG,annualFeePM_UKG;
    private TextView annualFeeWD_N,annualFeeWD_LKG,annualFeeWD_UKG;

    private TextView cpFeeText;
    private TableLayout cpFeeTable;
    private TextView annualCpFeeAM_N,annualCpFeeAM_LKG,annualCpFeeAM_UKG;
    private TextView annualCpFeePM_N,annualCpFeePM_LKG,annualCpFeePM_UKG;
    private TextView annualCpFeeWD_N,annualCpFeeWD_LKG,annualCpFeeWD_UKG;

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

        numEnrollAM_N = (TextView) view.findViewById(R.id.acceptanceTableRow2Cell2);
        numEnrollAM_LKG = (TextView) view.findViewById(R.id.acceptanceTableRow2Cell3);
        numEnrollAM_UKG = (TextView) view.findViewById(R.id.acceptanceTableRow2Cell4);
        numEnrollAM_T = (TextView) view.findViewById(R.id.acceptanceTableRow2Cell5);

        numEnrollPM_N = (TextView) view.findViewById(R.id.acceptanceTableRow3Cell2);
        numEnrollPM_LKG = (TextView) view.findViewById(R.id.acceptanceTableRow3Cell3);
        numEnrollPM_UKG = (TextView) view.findViewById(R.id.acceptanceTableRow3Cell4);
        numEnrollPM_T = (TextView) view.findViewById(R.id.acceptanceTableRow3Cell5);

        numEnrollWD_N = (TextView) view.findViewById(R.id.acceptanceTableRow4Cell2);
        numEnrollWD_LKG = (TextView) view.findViewById(R.id.acceptanceTableRow4Cell3);
        numEnrollWD_UKG = (TextView) view.findViewById(R.id.acceptanceTableRow4Cell4);
        numEnrollWD_T = (TextView) view.findViewById(R.id.acceptanceTableRow4Cell5);

        annualFeeAM_N = (TextView) view.findViewById(R.id.feeTableRow2Cell2);
        annualFeeAM_LKG = (TextView) view.findViewById(R.id.feeTableRow2Cell3);
        annualFeeAM_UKG = (TextView) view.findViewById(R.id.feeTableRow2Cell4);

        annualFeePM_N = (TextView) view.findViewById(R.id.feeTableRow3Cell2);
        annualFeePM_LKG = (TextView) view.findViewById(R.id.feeTableRow3Cell3);
        annualFeePM_UKG = (TextView) view.findViewById(R.id.feeTableRow3Cell4);

        annualFeeWD_N = (TextView) view.findViewById(R.id.feeTableRow4Cell2);
        annualFeeWD_LKG = (TextView) view.findViewById(R.id.feeTableRow4Cell3);
        annualFeeWD_UKG = (TextView) view.findViewById(R.id.feeTableRow4Cell4);

        cpFeeText = (TextView) view.findViewById(R.id.cpFeeText);
        cpFeeTable = (TableLayout) view.findViewById(R.id.cpFeeTable);

        annualCpFeeAM_N = (TextView) view.findViewById(R.id.cpFeeTableRow2Cell2);
        annualCpFeeAM_LKG = (TextView) view.findViewById(R.id.cpFeeTableRow2Cell3);
        annualCpFeeAM_UKG = (TextView) view.findViewById(R.id.cpFeeTableRow2Cell4);

        annualCpFeePM_N = (TextView) view.findViewById(R.id.cpFeeTableRow3Cell2);
        annualCpFeePM_LKG = (TextView) view.findViewById(R.id.cpFeeTableRow3Cell3);
        annualCpFeePM_UKG = (TextView) view.findViewById(R.id.cpFeeTableRow3Cell4);

        annualCpFeeWD_N = (TextView) view.findViewById(R.id.cpFeeTableRow4Cell2);
        annualCpFeeWD_LKG = (TextView) view.findViewById(R.id.cpFeeTableRow4Cell3);
        annualCpFeeWD_UKG = (TextView) view.findViewById(R.id.cpFeeTableRow4Cell4);

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

        // acceptanceTable
        numEnrollAM_N.setText(schoolVM.getNadAmN());
        numEnrollAM_LKG.setText(schoolVM.getNadAmL());
        numEnrollAM_UKG.setText(schoolVM.getNadAmU());
        numEnrollAM_T.setText(schoolVM.getNadAmT());

        numEnrollPM_N.setText(schoolVM.getNadPmN());
        numEnrollPM_LKG.setText(schoolVM.getNadPmL());
        numEnrollPM_UKG.setText(schoolVM.getNadPmU());
        numEnrollPM_T.setText(schoolVM.getNadPmT());

        numEnrollWD_N.setText(schoolVM.getNadWdN());
        numEnrollWD_LKG.setText(schoolVM.getNadWdL());
        numEnrollWD_UKG.setText(schoolVM.getNadWdU());
        numEnrollWD_T.setText(schoolVM.getNadWdT());

        // feeTable
        annualFeeAM_N.setText(schoolVM.getFeeAmN());
        annualFeeAM_LKG.setText(schoolVM.getFeeAmL());
        annualFeeAM_UKG.setText(schoolVM.getFeeAmU());

        annualFeePM_N.setText(schoolVM.getFeePmN());
        annualFeePM_LKG.setText(schoolVM.getFeePmL());
        annualFeePM_UKG.setText(schoolVM.getFeePmU());

        annualFeeWD_N.setText(schoolVM.getFeeWdN());
        annualFeeWD_LKG.setText(schoolVM.getFeeWdL());
        annualFeeWD_UKG.setText(schoolVM.getFeeWdU());

        // cpFeeTable
        if (schoolVM.isCp()) {
            cpFeeText.setVisibility(View.VISIBLE);
            cpFeeTable.setVisibility(View.VISIBLE);

            annualCpFeeAM_N.setText(schoolVM.getCpFeeAmN());
            annualCpFeeAM_LKG.setText(schoolVM.getCpFeeAmL());
            annualCpFeeAM_UKG.setText(schoolVM.getCpFeeAmU());

            annualCpFeePM_N.setText(schoolVM.getCpFeePmN());
            annualCpFeePM_LKG.setText(schoolVM.getCpFeePmL());
            annualCpFeePM_UKG.setText(schoolVM.getCpFeePmU());

            annualCpFeeWD_N.setText(schoolVM.getCpFeeWdN());
            annualCpFeeWD_LKG.setText(schoolVM.getCpFeeWdL());
            annualCpFeeWD_UKG.setText(schoolVM.getCpFeeWdU());
        } else {
            cpFeeText.setVisibility(View.GONE);
            cpFeeTable.setVisibility(View.GONE);
        }

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

