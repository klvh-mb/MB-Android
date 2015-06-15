package miniBean.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import miniBean.R;
import miniBean.activity.CommunityActivity;
import miniBean.app.AppController;
import miniBean.app.LocalCommunityTabCache;
import miniBean.app.TrackedFragment;
import miniBean.util.CommunityIconUtil;
import miniBean.util.ImageUtil;
import miniBean.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyCommunityPagerFragment extends TrackedFragment {

    private RelativeLayout comm1, comm2, comm3, comm4;
    private ImageView commImage1, commImage2, commImage3, commImage4;
    private TextView commName1, commName2, commName3, commName4;
    private ImageView joinButton1, joinButton2, joinButton3, joinButton4;
    private TextView noMember1, noMember2, noMember3, noMember4;
    private TextView noPost1, noPost2, noPost3, noPost4;

    private List<CommunitiesWidgetChildVM> communities;

    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_community_pager_fragment, container, false);

        this.inflater = inflater;

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

        setCommunityLayout(0, comm1, commImage1, commName1, noMember1, joinButton1);
        setCommunityLayout(1, comm2, commImage2, commName2, noMember2, joinButton2);
        setCommunityLayout(2, comm3, commImage3, commName3, noMember3, joinButton3);
        setCommunityLayout(3, comm4, commImage4, commName4, noMember4, joinButton4);

        //LocalCommunityTabCache.addTopicCommunityFragment(this);

        return view;
    }

    public void notifyChange() {
        //topicAdapter.notifyDataSetChanged();
        //progressBarComm.setVisibility(View.GONE);
    }

    private void setCommunityLayout(
            final int index, final View commLayout, final ImageView commImage,
            final TextView commName, final TextView noMember, final ImageView joinButton) {

        if (index >= communities.size()) {
            commLayout.setVisibility(View.GONE);
            return;
        }


        // set layout

        final CommunitiesWidgetChildVM item = communities.get(index);

        commName.setText(item.getDn());
        noMember.setText(item.getMm().toString());

        int iconMapped = CommunityIconUtil.map(item.gi);
        if (iconMapped != -1) {
            //Log.d(this.getClass().getSimpleName(), "setCommunityLayout: replace source with local comm icon - " + item.gi);
            commImage.setImageDrawable(getActivity().getResources().getDrawable(iconMapped));
        } else {
            Log.d(this.getClass().getSimpleName(), "setCommunityLayout: load comm icon from background - " + item.gi);
            ImageUtil.displayRoundedCornersImage(item.gi, commImage);
        }

        if (item.getIsM()) {
            joinButton.setImageResource(R.drawable.ic_check);
        } else {
            joinButton.setImageResource(R.drawable.ic_add);
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!item.getIsM()) {
                    joinCommunity(item, joinButton);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage(MyCommunityPagerFragment.this.getActivity().getString(R.string.community_leave_confirm));
                    alertDialogBuilder.setPositiveButton(MyCommunityPagerFragment.this.getActivity().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveCommunity(item, joinButton);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(MyCommunityPagerFragment.this.getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        commLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCommunityActivity(item);
            }
        });
    }

    private void joinCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinButton) {
        AppController.getApi().sendJoinRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), MyCommunityPagerFragment.this.getActivity().getString(R.string.community_join_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(true);
                joinButton.setImageResource(R.drawable.ic_check);

                LocalCommunityTabCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(inflater.getContext(), MyCommunityPagerFragment.this.getActivity().getString(R.string.community_join_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }

    private void leaveCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinImageView) {
        AppController.getApi().sendLeaveRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), MyCommunityPagerFragment.this.getActivity().getString(R.string.community_leave_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(false);
                joinImageView.setImageResource(R.drawable.ic_add);

                LocalCommunityTabCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(inflater.getContext(), MyCommunityPagerFragment.this.getActivity().getString(R.string.community_leave_failed), Toast.LENGTH_SHORT).show();
                retrofitError.printStackTrace();
            }
        });
    }

    private void startCommunityActivity(CommunitiesWidgetChildVM community) {
        if (community != null) {
            Log.d(this.getClass().getSimpleName(), "startCommunityActivity with commId - " + community.getId());
            Intent intent = new Intent(getActivity(), CommunityActivity.class);
            intent.putExtra("id", community.getId());
            intent.putExtra("flag", "FromTopicFragment");
            startActivity(intent);
        }
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
