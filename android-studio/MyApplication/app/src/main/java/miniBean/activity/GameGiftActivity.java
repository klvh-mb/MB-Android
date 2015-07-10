package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.ImageMapping;
import miniBean.util.ImageUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.GameGiftVM;
import miniBean.viewmodel.ResponseStatusVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GameGiftActivity extends TrackedFragmentActivity {

    private ScrollView scrollView;
    private RelativeLayout gameLayout;
    private TextView titleText, pointsText;
    private ImageView gameGiftImage;
    private LinearLayout redeemStepsLayout, redeemExpirationLayout, redeemShippingLayout, redeemCCLayout, redeemMoreLayout;
    private TextView redeemStepsText, redeemExpirationText, redeemShippingText, redeemCCText, redeemMoreText, redeemText1, redeemText2;
    private RelativeLayout redeemLayout1, redeemLayout2;

    private long gameGiftId, userGamePoints;
    private GameGiftVM gameGift;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_gift_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.game_gift_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );


        gameGiftId = getIntent().getLongExtra("id", 0L);
        userGamePoints = getIntent().getLongExtra("userGamePoints", 0L);

        scrollView = (ScrollView) this.findViewById(R.id.scrollView);
        gameLayout = (RelativeLayout) this.findViewById((R.id.gameLayout));
        gameGiftImage = (ImageView) this.findViewById((R.id.gameGiftImage));
        titleText = (TextView) this.findViewById(R.id.titleText);
        pointsText = (TextView) this.findViewById(R.id.pointsText);
        redeemStepsLayout = (LinearLayout) this.findViewById(R.id.redeemStepsLayout);
        redeemExpirationLayout = (LinearLayout) this.findViewById(R.id.redeemExpirationLayout);
        redeemShippingLayout = (LinearLayout) this.findViewById(R.id.redeemShippingLayout);
        redeemCCLayout = (LinearLayout) this.findViewById(R.id.redeemCCLayout);
        redeemMoreLayout = (LinearLayout) this.findViewById(R.id.redeemMoreLayout);
        redeemStepsText = (TextView) this.findViewById(R.id.redeemStepsText);
        redeemExpirationText = (TextView) this.findViewById(R.id.redeemExpirationText);
        redeemShippingText = (TextView) this.findViewById(R.id.redeemShippingText);
        redeemCCText = (TextView) this.findViewById(R.id.redeemCCText);
        redeemMoreText = (TextView) this.findViewById(R.id.redeemMoreText);
        redeemText1 = (TextView) this.findViewById(R.id.redeemText1);
        redeemText2 = (TextView) this.findViewById(R.id.redeemText2);

        redeemLayout1 = (RelativeLayout) this.findViewById((R.id.redeemLayout1));
        redeemLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemGameGift(gameGiftId);
            }
        });

        redeemLayout2 = (RelativeLayout) this.findViewById((R.id.redeemLayout2));
        redeemLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemGameGift(gameGiftId);
            }
        });

        ImageView backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refresh();
    }

    private void getGameGift(long id) {
        Log.d(GameGiftActivity.class.getSimpleName(), "getGameGift: id=" + id);
        ViewUtil.showSpinner(this);
        AppController.getApi().getGameGiftInfo(id, AppController.getInstance().getSessionId(), new Callback<GameGiftVM>() {
            @Override
            public void success(final GameGiftVM gameGiftVM, Response response) {
                gameGift = gameGiftVM;

                int imageMapped = ImageMapping.map(gameGiftVM.getIm());
                if (imageMapped != -1) {
                    //Log.d(this.getClass().getSimpleName(), "getGameGift: replace source with local game gift image - " + imageMapped);
                    gameGiftImage.setImageDrawable(getResources().getDrawable(imageMapped));
                } else {
                    Log.d(this.getClass().getSimpleName(), "getGameGift: load game gift image from background - " + gameGiftVM.getIm());
                    ImageUtil.displayThinRoundedCornersImage(gameGiftVM.getIm(), gameGiftImage);
                }

                titleText.setText(gameGiftVM.getNm());
                pointsText.setText(gameGiftVM.getRp() + "");

                if (!StringUtils.isEmpty(gameGiftVM.getRi())) {
                    redeemStepsLayout.setVisibility(View.VISIBLE);
                    ViewUtil.setHtmlText(gameGiftVM.getRi(), redeemStepsText, GameGiftActivity.this, true, true);
                } else {
                    redeemStepsLayout.setVisibility(View.GONE);
                }

                if (!StringUtils.isEmpty(gameGiftVM.getRi())) {
                    redeemExpirationLayout.setVisibility(View.VISIBLE);
                    ViewUtil.setHtmlText(gameGiftVM.getEi(), redeemExpirationText, GameGiftActivity.this, true, true);
                } else {
                    redeemExpirationLayout.setVisibility(View.GONE);
                }

                if (!StringUtils.isEmpty(gameGiftVM.getSi())) {
                    redeemShippingLayout.setVisibility(View.VISIBLE);
                    ViewUtil.setHtmlText(gameGiftVM.getSi(), redeemShippingText, GameGiftActivity.this, true, true);
                } else {
                    redeemShippingLayout.setVisibility(View.GONE);
                }

                if (!StringUtils.isEmpty(gameGiftVM.getCi())) {
                    redeemCCLayout.setVisibility(View.VISIBLE);
                    ViewUtil.setHtmlText(gameGiftVM.getCi(), redeemCCText, GameGiftActivity.this, true, true);
                } else {
                    redeemCCLayout.setVisibility(View.GONE);
                }

                if (!StringUtils.isEmpty(gameGiftVM.getMi())) {
                    redeemMoreLayout.setVisibility(View.VISIBLE);
                    ViewUtil.setHtmlText(gameGiftVM.getMi(), redeemMoreText, GameGiftActivity.this, true, true);
                } else {
                    redeemMoreLayout.setVisibility(View.GONE);
                }

                if (gameGiftVM.isPending()) {
                    redeemText1.setText(getString(R.string.game_gifts_redeem_pending));
                    redeemText2.setText(getString(R.string.game_gifts_redeem_pending));
                }

                ViewUtil.stopSpinner(GameGiftActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameGiftActivity.class.getSimpleName(), "getGameAccount: failure", error);
                ViewUtil.stopSpinner(GameGiftActivity.this);
            }
        });
    }

    private void redeemGameGift(long id) {
        if (gameGift.isPending) {
            ViewUtil.alertGameStatus(GameGiftActivity.this, getString(R.string.game_gifts_redeem_requested), -1, 5000);
            return;
        }

        if (userGamePoints < gameGift.getRp()) {
            String msg = getString(R.string.game_gifts_redeem_points_not_enough);
            msg = String.format(msg, gameGift.getRp(), userGamePoints);
            ViewUtil.alertGameStatus(this, msg, -1, 5000);
            return;
        }

        AppController.getApi().redeemGameGift(id, AppController.getInstance().getSessionId(), new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatusVM, Response response1) {
                if (responseStatusVM.isSuccess()) {
                    // change button state and alert
                    userGamePoints -= gameGift.getRp();
                    redeemText1.setText(getString(R.string.game_gifts_redeem_pending));
                    redeemText2.setText(getString(R.string.game_gifts_redeem_pending));
                    ViewUtil.alertGameStatus(GameGiftActivity.this, getString(R.string.game_gifts_redeem_requested), -1, 5000);
                } else {
                    ViewUtil.alertGameStatus(GameGiftActivity.this, responseStatusVM.getMessages().get(0), -1, 5000);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameGiftActivity.class.getSimpleName(), "redeemGameGift: failure", error);
            }
        });
    }

    private void refresh() {
        getGameGift(gameGiftId);

        /*
        if (UserInfoCache.getUser().isAdmin()) {
            latestGameTransactionsLayout.setVisibility(View.VISIBLE);
            getLatestGameTransactions();
        } else {
            latestGameTransactionsLayout.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
