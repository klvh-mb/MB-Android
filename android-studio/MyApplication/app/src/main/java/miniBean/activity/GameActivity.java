package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.adapter.GameTransactionListAdapter;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.app.UserInfoCache;
import miniBean.util.GameConstants;
import miniBean.util.ViewUtil;
import miniBean.viewmodel.GameAccountVM;
import miniBean.viewmodel.GameTransactionVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GameActivity extends TrackedFragmentActivity {

    public static final String REFERRAL_URL_PREFIX = AppController.BASE_URL + "/signup-promo-code/";

    private TextView pointsText, redeemedPointsText;
    private ImageView signInImage;
    private TextView referralNotePoints;
    private EditText referralUrlEdit;
    private LinearLayout whatsappLayout, copyUrlLayout;
    private ImageView backImage;
    private ListView gameTransactionList, latestGameTransactionList;
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

        pointsText = (TextView) this.findViewById(R.id.pointsText);
        redeemedPointsText = (TextView) this.findViewById(R.id.redeemedPointsText);
        signInImage = (ImageView) this.findViewById(R.id.signInImage);
        referralNotePoints = (TextView) this.findViewById(R.id.referralNotePoints);
        referralUrlEdit = (EditText) this.findViewById(R.id.referralUrlEdit);
        whatsappLayout = (LinearLayout) this.findViewById(R.id.whatsappLayout);
        copyUrlLayout = (LinearLayout) this.findViewById(R.id.copyUrlLayout);
        gameTransactionList = (ListView) this.findViewById(R.id.gameTransactionList);
        latestGameTransactionList = (ListView) this.findViewById(R.id.latestGameTransactionList);
        latestGameTransactionsLayout = (RelativeLayout) this.findViewById(R.id.latestGameTransactionsLayout);

        referralNotePoints.setText(GameConstants.POINTS_REFERRAL_SIGNUP+"");

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getGameAccount();
        getGameTransactions();

        if (UserInfoCache.getUser().isAdmin()) {
            latestGameTransactionsLayout.setVisibility(View.VISIBLE);
            getLatestGameTransactions();
        } else {
            latestGameTransactionsLayout.setVisibility(View.GONE);
        }
    }

    private void getGameAccount() {
        AppController.getApi().getGameAccount(AppController.getInstance().getSessionId(), new Callback<GameAccountVM>() {
            @Override
            public void success(GameAccountVM gameAccountVM, Response response) {
                pointsText.setText(gameAccountVM.getGmpt()+"");
                if (gameAccountVM.getRdpt() == 0) {
                    redeemedPointsText.setText("-");
                } else {
                    redeemedPointsText.setText(gameAccountVM.getRdpt() + "");
                }

                signedIn = gameAccountVM.isSignedIn();
                if (signedIn) {
                    signInImage.setImageDrawable(getResources().getDrawable(R.drawable.game_signed_in));
                } else {
                    signInImage.setImageDrawable(getResources().getDrawable(R.drawable.game_sign_in));
                }

                referralUrlEdit.setText(REFERRAL_URL_PREFIX + gameAccountVM.getPmcde());

                signInImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!signedIn) {
                            signInForToday();
                        } else {
                            // alert

                        }
                    }
                });

                whatsappLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                copyUrlLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "getGameAccount: failure", error);
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

                // alert

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "signIn: failure", error);
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
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(GameActivity.class.getSimpleName(), "getLatestGameTransactions: failure", error);
            }
        });
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
