package miniBean.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.app.TrackedFragment;
import miniBean.util.AnimationUtil;
import miniBean.util.CommunityUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;

public class MyCommunityPagerFragment extends TrackedFragment {

    private RelativeLayout comm1, comm2, comm3, comm4;
    private ImageView commImage1, commImage2, commImage3, commImage4;
    private TextView commName1, commName2, commName3, commName4;
    private ImageView joinButton1, joinButton2, joinButton3, joinButton4;
    private TextView noMember1, noMember2, noMember3, noMember4;
    private TextView noPost1, noPost2, noPost3, noPost4;

    private List<CommunitiesWidgetChildVM> communities;

    private CommunityUtil communityUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_community_pager_fragment, container, false);

        comm1 = (RelativeLayout) view.findViewById(R.id.comm1);
        commImage1 = (ImageView) view.findViewById(R.id.commImage1);
        commName1 = (TextView) view.findViewById(R.id.commName1);
        joinButton1 = (ImageView) view.findViewById(R.id.joinButton1);
        noMember1 = (TextView) view.findViewById(R.id.noMember1);
        noPost1 = (TextView) view.findViewById(R.id.noPost1);

        comm2 = (RelativeLayout) view.findViewById(R.id.comm2);
        commImage2 = (ImageView) view.findViewById(R.id.commImage2);
        commName2 = (TextView) view.findViewById(R.id.commName2);
        joinButton2 = (ImageView) view.findViewById(R.id.joinButton2);
        noMember2 = (TextView) view.findViewById(R.id.noMember2);
        noPost2 = (TextView) view.findViewById(R.id.noPost2);

        comm3 = (RelativeLayout) view.findViewById(R.id.comm3);
        commImage3 = (ImageView) view.findViewById(R.id.commImage3);
        commName3 = (TextView) view.findViewById(R.id.commName3);
        joinButton3 = (ImageView) view.findViewById(R.id.joinButton3);
        noMember3 = (TextView) view.findViewById(R.id.noMember3);
        noPost3 = (TextView) view.findViewById(R.id.noPost3);

        comm4 = (RelativeLayout) view.findViewById(R.id.comm4);
        commImage4 = (ImageView) view.findViewById(R.id.commImage4);
        commName4 = (TextView) view.findViewById(R.id.commName4);
        joinButton4 = (ImageView) view.findViewById(R.id.joinButton4);
        noMember4 = (TextView) view.findViewById(R.id.noMember4);
        noPost4 = (TextView) view.findViewById(R.id.noPost4);

        communityUtil = new CommunityUtil(this.getActivity(), this.communities);
        communityUtil.setCommunityLayout(0, comm1, commImage1, commName1, noMember1, noPost1, joinButton1);
        communityUtil.setCommunityLayout(1, comm2, commImage2, commName2, noMember2, noPost2, joinButton2);
        communityUtil.setCommunityLayout(2, comm3, commImage3, commName3, noMember3, noPost3, joinButton3);
        communityUtil.setCommunityLayout(3, comm4, commImage4, commName4, noMember4, noPost4, joinButton4);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunitiesWidgetChildVM> communities) {
        this.communities = communities;
    }
}
