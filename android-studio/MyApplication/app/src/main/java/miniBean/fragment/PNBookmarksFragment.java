package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import miniBean.R;
import miniBean.activity.PNCommunityActivity;
import miniBean.adapter.PNBookmarkListAdapter;
import miniBean.app.AppController;
import miniBean.app.TrackedFragment;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PNBookmarksFragment extends TrackedFragment {

    private static final String TAG = KGBookmarksFragment.class.getName();
    private ListView bookmarkList;
    private PNBookmarkListAdapter bookmarkListAdapter;
    private TextView totalBookmarkText,bookmarkText;
    private List<PreNurseryVM> schoolVMList;

    private int totalBookmark = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.school_bookmark_fragment, container, false);

        bookmarkList = (ListView) view.findViewById(R.id.listBookmark);
        totalBookmarkText = (TextView) view.findViewById(R.id.totalBookmark);
        bookmarkText = (TextView) view.findViewById(R.id.bookmarkText);
        bookmarkText.setText(getString(R.string.schools_pn_bookmark_title_2));

        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PreNurseryVM vm = bookmarkListAdapter.getItem(i);

                Intent intent = new Intent(getActivity(), PNCommunityActivity.class);
                intent.putExtra("commId",vm.getCommId());
                intent.putExtra("id", vm.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getBookmarkedSchools();
    }

    private void getBookmarkedSchools(){
        AppController.getApi().getBookmarkedPNs(AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                // first load or bookmark list chanded
                if (totalBookmark == -1 || totalBookmark != vms.size()) {
                    totalBookmark = vms.size();
                    totalBookmarkText.setText(totalBookmark + "");
                    schoolVMList = vms;
                    bookmarkListAdapter = new PNBookmarkListAdapter(getActivity(), schoolVMList);
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
