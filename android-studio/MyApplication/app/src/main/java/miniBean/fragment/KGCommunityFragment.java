package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.activity.NewKGPostActivity;
import miniBean.util.CommunityIconUtil;
import miniBean.util.ExternalLauncherUtil;
import miniBean.viewmodel.KindergartenVM;

public class KGCommunityFragment extends AbstractSchoolCommunityFragment {

    private TextView nameText,districtText,enNameText,orgValue,typeValue,timeValue,curriculumValue;
    private TextView curriculumContent,addressText,phoneValue;
    private TextView websiteValue,postCount;
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

    private TextView summerUniformFee, winterUniformFee, schoolBagFee, teaFee, textbooksFee, workbooksFee;

    private ImageView icon,couponImage,pnImage,govtImage;
    private LinearLayout gotoCommLayout,newPostLayout;

    private KindergartenVM schoolVM;

    @Override
    protected View getListHeader(LayoutInflater inflater) {
        return inflater.inflate(R.layout.kg_community_fragment_header, null);
    }

    @Override
    protected String getIntentFlag() {
        return "FromKG";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

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
        curriculumContent = (TextView) view.findViewById(R.id.contentText);
        addressText = (TextView) view.findViewById(R.id.addressValue);
        phoneValue = (TextView) view.findViewById(R.id.phoneValue);
        websiteValue = (TextView) view.findViewById(R.id.websiteValue);
        newPostLayout = (LinearLayout) view.findViewById(R.id.newPostLayout);
        postCount = (TextView) view.findViewById(R.id.postCount);
        gotoCommLayout = (LinearLayout) view.findViewById(R.id.gotoCommLayout);
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

        summerUniformFee = (TextView) view.findViewById(R.id.extraFeesTableRow1Cell2);
        winterUniformFee = (TextView) view.findViewById(R.id.extraFeesTableRow1Cell4);
        schoolBagFee = (TextView) view.findViewById(R.id.extraFeesTableRow2Cell2);
        teaFee = (TextView) view.findViewById(R.id.extraFeesTableRow2Cell4);
        textbooksFee = (TextView) view.findViewById(R.id.extraFeesTableRow3Cell2);
        workbooksFee = (TextView) view.findViewById(R.id.extraFeesTableRow3Cell4);

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
                listView.smoothScrollToPosition(1);
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

        // extra fees table
        summerUniformFee.setText(schoolVM.getSufee());
        winterUniformFee.setText(schoolVM.getWbfee());
        schoolBagFee.setText(schoolVM.getSbfee());
        teaFee.setText(schoolVM.getTsfee());
        textbooksFee.setText(schoolVM.getTbfee());
        workbooksFee.setText(schoolVM.getWbfee());

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
    }

    public void setSchool(KindergartenVM schoolVM) {
        this.schoolVM = schoolVM;
    }
}

