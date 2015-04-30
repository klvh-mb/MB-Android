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
import miniBean.adapter.BookmarkListAdapter;
import miniBean.app.AppController;
import miniBean.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolBookmarkFragment extends Fragment {

    private static final String TAG = SchoolBookmarkFragment.class.getName();
    private ListView bookmarkList;
    private BookmarkListAdapter bookmarkListAdapter;
    private TextView totalBookmark;
    private List<PreNurseryVM> preNurseryVMList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.school_bookmark_fragment, container, false);

        bookmarkList = (ListView) view.findViewById(R.id.listBookmark);
        totalBookmark = (TextView) view.findViewById(R.id.totalBookmark);

        preNurseryVMList=new ArrayList<PreNurseryVM>();

        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PreNurseryVM vm=bookmarkListAdapter.getItem(i);

                Intent intent=new Intent(getActivity(), PNCommunityActivity.class);
                intent.putExtra("commId",vm.getCommId());
                intent.putExtra("id", vm.getId());
                intent.putExtra("flag","FromSchoolBookmark");
                startActivity(intent);
            }
        });

        getPNBookmarks();

        return view;
    }

    private void getPNBookmarks(){
        AppController.getApi().getBookmarkPns(AppController.getInstance().getSessionId(),new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> preNurseryVMs, Response response) {
                totalBookmark.setText(""+preNurseryVMs.size());
                preNurseryVMList.addAll(preNurseryVMs);
                bookmarkListAdapter = new BookmarkListAdapter(getActivity(),preNurseryVMList);
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
