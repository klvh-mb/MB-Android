package miniBean.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import miniBean.R;
import miniBean.activity.MessageDetailActivity;
import miniBean.adapter.ConversationListAdapter;
import miniBean.app.AppController;
import miniBean.app.TrackedFragment;
import miniBean.viewmodel.ConversationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessageListFragment extends TrackedFragment {

    private static final String TAG = MessageListFragment.class.getName();
    private ListView listView;
    private ConversationListAdapter adapter;
    private List<ConversationVM> conversationVMList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.message_list_fragment, container, false);

        listView= (ListView) view.findViewById(R.id.conversationList);

        getAllConversation();

        conversationVMList=new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConversationVM childVM = adapter.getItem(i);
                Intent intent=new Intent(getActivity(),MessageDetailActivity.class);
                intent.putExtra("user_name",childVM.getNm());
                intent.putExtra("uid",childVM.getUid());
                intent.putExtra("cid",childVM.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    private void getAllConversation(){
        AppController.getApi().getAllConversation(AppController.getInstance().getSessionId(),new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> conversationVMs, Response response) {
                conversationVMList=conversationVMs;

                adapter=new ConversationListAdapter(getActivity(),conversationVMList);
                listView.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
