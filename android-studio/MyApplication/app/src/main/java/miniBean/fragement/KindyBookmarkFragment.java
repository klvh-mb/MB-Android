package miniBean.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.PNCommunityActivity;
import miniBean.adapter.KindyBookmarkListAdapter;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.viewmodel.KindergartenVM;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class KindyBookmarkFragment extends Fragment {

    private static final String TAG = KindyBookmarkFragment.class.getName();
    private MyApi api;
    private ListView bookmarkList;
    private KindyBookmarkListAdapter bookmarkListAdapter;
    private TextView totalBookmark;
    private List<KindergartenVM> kindergartenVMList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.school_bookmark_fragment, container, false);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        api = restAdapter.create(MyApi.class);

        bookmarkList= (ListView) view.findViewById(R.id.listBookmark);
        totalBookmark= (TextView) view.findViewById(R.id.totalBookmark);

        kindergartenVMList=new ArrayList<KindergartenVM>();

        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KindergartenVM vm=bookmarkListAdapter.getItem(i);

                Intent intent=new Intent(getActivity(), PNCommunityActivity.class);
                intent.putExtra("commid",vm.getCommId());
                intent.putExtra("id", vm.getId());
                intent.putExtra("flag","fromschool");
                startActivity(intent);
            }
        });

        getKGBookmarks();

        return view;
    }

   private void getKGBookmarks(){
       AppController.getApi().getBookmarkKgs(AppController.getInstance().getSessionId(),new Callback<List<KindergartenVM>>() {
           @Override
           public void success(List<KindergartenVM> kindergartenVMs, Response response) {
               System.out.println("bookmark size::::"+kindergartenVMs.size());
               System.out.println("url:::::"+response.getUrl());
               totalBookmark.setText(""+kindergartenVMs.size());
               kindergartenVMList.addAll(kindergartenVMs);
               bookmarkListAdapter=new KindyBookmarkListAdapter(getActivity(),kindergartenVMList);
               bookmarkList.setAdapter(bookmarkListAdapter);
               bookmarkListAdapter.notifyDataSetChanged();
           }

           @Override
           public void failure(RetrofitError error) {
               error.printStackTrace();
           }
       });
   }


}
