package miniBean.activity;

import miniBean.R;

public class GameRulesActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.game_rules_title);
    }

    protected String getLoadUrl() {
        return GAME_RULES_URL;
    }
}