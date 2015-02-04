package miniBean;

import miniBean.fragement.NewsFeedFragement;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity implements NewsFeedFragement.Listener{
    private static final String TAG = MainActivity.class.getSimpleName();
    public NewsFeedFragement newsFeedFragement;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        newsFeedFragement = new NewsFeedFragement();
        getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, newsFeedFragement).addToBackStack(TAG).commit();
 
    }
}