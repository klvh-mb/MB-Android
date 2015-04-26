package miniBean.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import miniBean.activity.NewPostActivity;
import miniBean.adapter.NewsfeedListAdapter;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.PostArray;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolCommunityFragment extends Fragment {

    private TextView nameText,distText,schoolName,orgValue,typeValue,timeValue,curriculumValue;
    private TextView studentName,halfDayValue,fullDayValue,curriculumContent,addressText,phoneValue;
    private TextView websiteValue,postCount;
    private ImageView urlValueImage,editAction;
    private List<PreNurseryVM> preNurseryVMList;

    private NewsfeedListAdapter feedListAdapter;
    private List<CommunityPostVM> feedItems;

    private ListView postList;

    private ImageView cpValueImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pn_comm, container, false);


        nameText= (TextView) view.findViewById(R.id.pnNameText);
        distText= (TextView) view.findViewById(R.id.distNameText);
        schoolName= (TextView) view.findViewById(R.id.name);
        cpValueImage= (ImageView) view.findViewById(R.id.cpValueImage);
        orgValue= (TextView) view.findViewById(R.id.orgValueText);
        timeValue= (TextView) view.findViewById(R.id.timeValueText);
        typeValue= (TextView) view.findViewById(R.id.typeValueText);
        curriculumValue= (TextView) view.findViewById(R.id.curriValueText);
        studentName= (TextView) view.findViewById(R.id.studentValue);
        halfDayValue= (TextView) view.findViewById(R.id.halfDayValue);
        fullDayValue= (TextView) view.findViewById(R.id.fullDayValue);
        curriculumContent= (TextView) view.findViewById(R.id.contentText);
        addressText= (TextView) view.findViewById(R.id.addressValue);
        phoneValue= (TextView) view.findViewById(R.id.phoneValue);
        websiteValue= (TextView) view.findViewById(R.id.websiteValue);
        urlValueImage= (ImageView) view.findViewById(R.id.govtImage);
        editAction= (ImageView) view.findViewById(R.id.editAction);
        postList= (ListView) view.findViewById(R.id.postList);
        postCount= (TextView) view.findViewById(R.id.postCount);



        preNurseryVMList=new ArrayList<PreNurseryVM>();

        getNewsFeedByCommunityId(getArguments().getLong("commid"));

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
                    intent.putExtra("commid", getArguments().getLong("commid"));
                    intent.putExtra("flag", "fromschool");
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

        getPnInfo(getArguments().getLong("id"));

        if(getArguments().getBoolean("cp")){
            cpValueImage.setImageResource(R.drawable.value_yes);
        }else {
            cpValueImage.setImageResource(R.drawable.value_no);
        }

        editAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewPostActivity.class);
                intent.putExtra("id",String.valueOf(getArguments().getLong("commid")));
                intent.putExtra("flag","fromschool");
                startActivity(intent);
            }
        });

        return view;
    }


    private void getPnInfo(Long id) {
        AppController.getApi().getPnInfo(id, AppController.getInstance().getSessionId(), new Callback<PreNurseryVM>() {
            @Override
            public void success(PreNurseryVM preNurseryVM, Response response) {
                nameText.setText(preNurseryVM.getN());
                distText.setText(preNurseryVM.getDis());
                schoolName.setText(preNurseryVM.getNe());
                typeValue.setText(preNurseryVM.getOrgt());
                timeValue.setText(preNurseryVM.getCt());
                orgValue.setText(preNurseryVM.getOrg());
                curriculumContent.setText(preNurseryVM.getCur());
                curriculumValue.setText(preNurseryVM.getCurt());
                halfDayValue.setText(preNurseryVM.getFeeHd());
                fullDayValue.setText(preNurseryVM.getFeeWd());
                websiteValue.setText(preNurseryVM.getUrl());
                phoneValue.setText(preNurseryVM.getPho());
                addressText.setText(preNurseryVM.getAdr());
                studentName.setText(preNurseryVM.getNadm());
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

