package miniBean.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.GameGiftListAdapter;
import miniBean.adapter.GameTransactionListAdapter;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.app.UserInfoCache;
import miniBean.util.DefaultValues;
import miniBean.util.GameConstants;
import miniBean.util.SharingUtil;
import miniBean.util.UrlUtil;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.GameAccountVM;
import miniBean.viewmodel.GameGiftVM;
import miniBean.viewmodel.GameTransactionVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GameActivity extends TrackedFragmentActivity {

    private ScrollView scrollView;
    private RelativeLayout gameLayout;
    private TextView pointsText, redeemedPointsText, redeemedPointsTips;
    private ImageView signInImage;
    private TextView referralNotePoints, referralSuccessNum, referralSuccessPoints;
    private EditText referralUrlEdit;
    private LinearLayout gameRulesLayout, whatsappLayout, copyUrlLayout;
    private ImageView backImage;
    private ListView gameGiftList, gameTransactionList, latestGameTransactionList;
    private RelativeLayout latestGameTransactionsLayout;

    private boolean signedIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.game_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        scrollView = (ScrollView) this.findViewById(R.id.scrollView);
        gameLayout = (RelativeLayout) this.findViewById((R.id.gameLayout));
        pointsText = (TextView) this.findViewById(R.id.pointsText);
        redeemedPointsText = (TextView) this.findViewById(R.id.redeemedPointsText);
        redeemedPointsTips = (TextView) this.findViewById(R.id.redeemedPointsTips);
        signInImage = (ImageView) this.findViewById(R.id.signInImage);
        referralNotePoints = (TextView) this.findViewById(R.id.referralNotePoints);
        referralSuccessNum = (TextView) this.findViewById(R.id.referralSuccessNum);
        referralSuccessPoints = (TextView) this.findViewById(R.id.referralSuccessPoints);
        referralUrlEdit = (EditText) this.findViewById(R.id.referralUrlEdit);
        gameRulesLayout = (LinearLayout) this.findViewById(R.id.gameRulesLayout);
        whatsappLayout = (LinearLayout) this.findViewById(R.id.whatsappLayout);
        copyUrlLayout = (LinearLayout) this.findViewById(R.id.copyUrlLayout);
        gameGiftList = (ListView) this.findViewById(R.id.gameGiftList);
        gameTransactionList = (ListView) this.findViewById(R.id.gameTransactionList);
        latestGameTransactionList = (ListView) this.findViewById(R.id.latestGameTransactionList);
        latestGameTransactionsLayout = (RelativeLayout) this.findViewById(R.id.latestGameTransactionsLayout);

        referralNotePoints.setText(GameConstants.POINTS_REFERRAL_SIGNUP+"");

        gameRulesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, GameRulesActivity.class);
                startActivity(intent);
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refresh();
    }

    private void getGameAccount() {
        ViewUtil.showSpinner(this);
        UserInfoCache.refresh(null, new Callback<GameAccountVM>() {
            @Override
            public void success(final GameAccountVM gameAccountVM, Response response) {
                pointsText.setText(gameAccountVM.getGmpt() + "");
                if (gameAccountVM.getRdpt() == 0) {
                    redeemedPointsText.setText("-");
                } else {
                    redeemedPointsText.setText(gameAccountVM.getRdpt() + "");
                    redeemedPointsTips.setVisibility(View.GONE);
                }

                signedIn = gameAccountVM.isSignedIn();
                if (signedIn) {
                    signInImage.setImageDrawable(getResources().getDrawable(R.drawable.game_signed_in));
                } else {
                    signInImage.setImageDrawable(getResources().getDrawable(R.drawable.game_sign_in));
                }

                referralSuccessNum.setText(gameAccountVM.getRefs() + "");
                referralSuccessPoints.setText((gameAccountVM.getRefs() * GameConstants.POINTS_REFERRAL_SIGNUP) + "");

                referralUrlEdit.setText(UrlUtil.createReferralUrl(gameAccountVM));

                signInImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!signedIn) {
                            signInForToday();
                        }
                    }
                });

                whatsappLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharingUtil.shareToWhatapp(gameAccountVM, GameActivity.this);
                    }
                });

                copyUrlLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ViewUtil.copyToClipboard(referralUrlEdit)) {
                            Toast.makeText(GameActivity.this, GameActivity.this.getString(R.string.game_referral_url_copy_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GameActivity.this, GameActivity.this.getString(R.string.game_referral_url_copy_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                getGameGifts(gameAccountVM);

                ViewUtil.stopSpinner(GameActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "getGameAccount: failure", error);
                ViewUtil.stopSpinner(GameActivity.this);
            }
        });
    }

    private void signInForToday() {
        AppController.getApi().signInForToday(AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                signInImage.setImageDrawable(getResources().getDrawable(R.drawable.game_signed_in));
                UserInfoCache.getUser().setEnableSignInForToday(false);
                signedIn = true;

                // update cache
                UserInfoCache.getGameAccount().setGmpt(
                        UserInfoCache.getGameAccount().getGmpt() + GameConstants.POINTS_DAILY_SIGNIN
                );

                // alert
                final Dialog dialog = ViewUtil.alertGameStatus(GameActivity.this,
                        getString(R.string.game_daily_signin_title), GameConstants.POINTS_DAILY_SIGNIN);

                // refresh parent activity
                Intent intent = new Intent();
                intent.putExtra(ViewUtil.INTENT_VALUE_REFRESH, true);
                setResult(RESULT_OK, intent);

                refresh();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "signIn: failure", error);
            }
        });
    }

    private void getGameGifts(final GameAccountVM gameAccount) {
        AppController.getApi().getAllGameGifts(AppController.getInstance().getSessionId(), new Callback<List<GameGiftVM>>() {
            @Override
            public void success(List<GameGiftVM> vms, Response response) {
                List<GameGiftVM> gameGifts = new ArrayList<>();
                gameGifts.addAll(vms);
                GameGiftListAdapter gameGiftListAdapter = new GameGiftListAdapter(GameActivity.this, gameGifts, gameAccount);
                gameGiftList.setAdapter(gameGiftListAdapter);
                ViewUtil.setHeightBasedOnChildren(gameGiftList);
                ViewUtil.scrollTop(scrollView);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "getGameGifts: failure", error);
            }
        });
    }

    private void getGameTransactions() {
        AppController.getApi().getGameTransactions(0L, AppController.getInstance().getSessionId(), new Callback<List<GameTransactionVM>>() {
            @Override
            public void success(List<GameTransactionVM> vms, Response response) {
                List<GameTransactionVM> gameTransactions = new ArrayList<>();
                gameTransactions.addAll(vms);
                GameTransactionListAdapter gameTransactionListAdapter = new GameTransactionListAdapter(GameActivity.this, gameTransactions);
                gameTransactionList.setAdapter(gameTransactionListAdapter);
                ViewUtil.setHeightBasedOnChildren(gameTransactionList);
                ViewUtil.scrollTop(scrollView);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "getGameTransactions: failure", error);
            }
        });
    }

    private void getLatestGameTransactions() {
        AppController.getApi().getLatestGameTransactions(AppController.getInstance().getSessionId(), new Callback<List<GameTransactionVM>>() {
            @Override
            public void success(List<GameTransactionVM> vms, Response response) {
                List<GameTransactionVM> gameTransactions = new ArrayList<>();
                gameTransactions.addAll(vms);
                GameTransactionListAdapter gameTransactionListAdapter = new GameTransactionListAdapter(GameActivity.this, gameTransactions, true);
                latestGameTransactionList.setAdapter(gameTransactionListAdapter);
                ViewUtil.setHeightBasedOnChildren(latestGameTransactionList);
                ViewUtil.scrollTop(scrollView);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "getLatestGameTransactions: failure", error);
            }
        });
    }

    private void refresh() {
        getGameAccount();
        getGameTransactions();

        if (UserInfoCache.getUser().isAdmin()) {
            latestGameTransactionsLayout.setVisibility(View.VISIBLE);
            getLatestGameTransactions();
        } else {
            latestGameTransactionsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_VALUE_REFRESH, false);
            if (refresh) {
                refresh();

                // refresh parent activity
                Intent intent = new Intent();
                intent.putExtra(ViewUtil.INTENT_VALUE_REFRESH, true);
                setResult(RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
