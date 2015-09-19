package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.activity.NewPNPostActivity;
import miniBean.util.ImageMapping;
import miniBean.util.ExternalLauncherUtil;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.PreNurseryVM;

public class PNCommunityFragment extends AbstractSchoolCommunityFragment {

    private TextView nameText,districtText,enNameText,orgValue,typeValue,timeValue,curriculumValue;
    private TextView studentNum,halfDayValue,fullDayValue,curriculumContent,addressText,phoneValue;
    private TextView websiteValue,postCount;
    private String govtUrlValue;

    private ImageView icon,couponImage,govtImage;
    private LinearLayout gotoCommLayout,newPostLayout;

    private RelativeLayout importantDatesLayout;
    private LinearLayout appDateBox, openDayBox;
    private TextView appDateValueText, openDayValueText;

    private PreNurseryVM schoolVM;

    @Override
    protected View getListHeader(LayoutInflater inflater) {
        return inflater.inflate(R.layout.pn_community_fragment_header, null);
    }

    @Override
    protected String getIntentFlag() {
        return "FromPN";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

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

        importantDatesLayout = (RelativeLayout) listHeader.findViewById(R.id.importantDatesLayout);
        appDateBox = (LinearLayout) listHeader.findViewById(R.id.appDateBox);
        openDayBox = (LinearLayout) listHeader.findViewById(R.id.openDayBox);
        appDateValueText = (TextView) listHeader.findViewById(R.id.appDateValueText);
        openDayValueText = (TextView) listHeader.findViewById(R.id.openDayValueText);

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

        initInfo();

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

        if (!StringUtils.isEmpty(schoolVM.getIcon())) {
            int iconMapped = ImageMapping.map(schoolVM.getIcon());
            if (iconMapped != -1) {
                icon.setImageDrawable(getResources().getDrawable(iconMapped));
            } else {
                ImageUtil.displayRoundedCornersImage(schoolVM.getIcon(), icon);
            }
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }

        districtText.setText(schoolVM.getDis());
        typeValue.setText(schoolVM.getOrgt());
        timeValue.setText(ViewUtil.translateClassTime(schoolVM.getCt(), this.getResources()));
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

        if (StringUtils.isEmpty(schoolVM.appDateTxt) && StringUtils.isEmpty(schoolVM.openDateTxt)) {
            importantDatesLayout.setVisibility(View.GONE);
        } else {
            importantDatesLayout.setVisibility(View.VISIBLE);
            if (StringUtils.isEmpty(schoolVM.appDateTxt)) {
                appDateBox.setVisibility(View.GONE);
            } else {
                appDateBox.setVisibility(View.VISIBLE);
                appDateValueText.setText(schoolVM.appDateTxt);
            }
            if (StringUtils.isEmpty(schoolVM.openDateTxt)) {
                openDayBox.setVisibility(View.GONE);
            } else {
                openDayBox.setVisibility(View.VISIBLE);
                openDayValueText.setText(schoolVM.openDateTxt);
            }
        }
    }

    public void setSchool(PreNurseryVM schoolVM) {
        this.schoolVM = schoolVM;
    }
}

