package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.activity.KGCommunityActivity;
import miniBean.adapter.KGBookmarkListAdapter;
import miniBean.app.AppController;
import miniBean.viewmodel.KindergartenVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KGBookmarksFragment extends MyFragment {

    private static final String TAG = KGBookmarksFragment.class.getName();
    private ListView bookmarkList;
    private KGBookmarkListAdapter bookmarkListAdapter;
    private TextView totalBookmarkText;
    private List<KindergartenVM> schoolVMList;

    private int totalBookmark = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.school_bookmark_fragment, container, false);

        bookmarkList = (ListView) view.findViewById(R.id.listBookmark);
        totalBookmarkText = (TextView) view.findViewById(R.id.totalBookmark);

        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KindergartenVM vm = bookmarkListAdapter.getItem(i);

                Intent intent = new Intent(getActivity(), KGCommunityActivity.class);
                intent.putExtra("commId",vm.getCommId());
                intent.putExtra("id", vm.getId());
                intent.putExtra("flag","FromKGBookmarks");
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getBookmarkedKGs();
    }

    private void getBookmarkedKGs(){
        AppController.getApi().getBookmarkedKGs(AppController.getInstance().getSessionId(), new Callback<List<KindergartenVM>>() {
            @Override
            public void success(List<KindergartenVM> vms, Response response) {
                // first load or bookmark list chanded
                if (totalBookmark == -1 || totalBookmark != vms.size()) {
                    totalBookmark = vms.size();
                    totalBookmarkText.setText(totalBookmark + "");
                    schoolVMList = vms;
                    bookmarkListAdapter = new KGBookmarkListAdapter(getActivity(), schoolVMList);
                    bookmarkList.setAdapter(bookmarkListAdapter);
                    bookmarkListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
