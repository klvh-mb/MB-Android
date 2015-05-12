package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import miniBean.util.CommunityIconUtil;
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
    private String govtUrlValue;

    private NewsfeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;

    private View listHeader, loadingFooter;
    private ListView listView;

    private ImageView icon,couponImage,govtImage;
    private LinearLayout gotoCommLayout,newPostLayout;

    private PreNurseryVM schoolVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pn_community_fragment, container, false);

        // header
        listHeader = inflater.inflate(R.layout.pn_community_fragment_header, null);

        // footer
        loadingFooter = inflater.inflate(R.layout.list_loading_footer, null);

        icon = (ImageView) listHeader.findViewById(R.id.schoolImage);
        nameText = (TextView) listHeader.findViewById(R.id.schoolNameText);
        districtText = (TextView) listHeader.findViewById(R.id.distNameText);
        enNameText = (TextView) listHeader.findViewById(R.id.enName);
        couponImage = (ImageView) listHeader.findViewById(R.id.couponImage);
        orgValue = (TextView) listHeader.findViewById(R.id.orgValueText);
        timeValue = (TextView) listHeader.findViewById(R.id.timeValueText);
        typeValue = (TextView) listHeader.findViewById(R.id.typeValueText);
        curriculumValue = (TextView) listHeader.findViewById(R.id.curriValueText);
        studentNum = (TextView) listHeader.findViewById(R.id.studentValue);
        halfDayValue = (TextView) listHeader.findViewById(R.id.halfDayValue);
        fullDayValue = (TextView) listHeader.findViewById(R.id.fullDayValue);
        curriculumContent = (TextView) listHeader.findViewById(R.id.contentText);
        addressText = (TextView) listHeader.findViewById(R.id.addressValue);
        phoneValue = (TextView) listHeader.findViewById(R.id.phoneValue);
        websiteValue = (TextView) listHeader.findViewById(R.id.websiteValue);
        newPostLayout = (LinearLayout) listHeader.findViewById(R.id.newPostLayout);
        postCount = (TextView) listHeader.findViewById(R.id.postCount);
        gotoCommLayout = (LinearLayout) listHeader.findViewById(R.id.gotoCommLayout);
        govtImage = (ImageView) listHeader.findViewById(R.id.govtImage);

        // list
        listView = (ListView) view.findViewById(R.id.postList);
        listView.addHeaderView(listHeader);
        listView.addFooterView(loadingFooter);      // need to add footer before set adapter

        initInfo();

        getNewsFeedByCommunityId(getArguments().getLong("commId"));

        feedItems = new ArrayList<>();
        feedListAdapter = new NewsfeedListAdapter(getActivity(), feedItems, false);
        listView.setAdapter(feedListAdapter);

        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

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
                    intent.putExtra("id", getArguments().getLong("id"));
                    intent.putExtra("commId", getArguments().getLong("commId"));
                    intent.putExtra("flag", "FromPN");
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
                        feedItems.get(feedItems.size()-1).getUt() + "",       // NOTE: use updateTime not createTime!!
                        page-1);
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
                listView.smoothScrollToPosition(1);
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

        listView.post(new Runnable() {
            @Override
            public void run() {
                if ("FromCommentImage".equals(getArguments().getString("flag"))) {
                    //listView.smoothScrollToPosition(1);
                } else {
                    listView.scrollTo(0,0);
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

    private void loadNewsfeed(Long id, String date, int offset) {
        AppController.getApi().getCommunityNextPosts(id, date, AppController.getInstance().getSessionId(), new Callback<List<CommunityPostVM>>() {
            @Override
            public void success(List<CommunityPostVM> communityPostVMs, Response response) {
                if (communityPostVMs == null || communityPostVMs.size() == 0) {
                    setFooterText(R.string.list_loaded_all);
                } else {
                    setFooterText(R.string.list_loading);
                }

                feedItems.addAll(communityPostVMs);
                feedListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void setFooterText(int text) {
        TextView footerText = (TextView) listView.findViewById(R.id.listLoadingFooterText);
        footerText.setText(text);
    }

    public void setSchool(PreNurseryVM schoolVM) {
        this.schoolVM = schoolVM;
    }
}

