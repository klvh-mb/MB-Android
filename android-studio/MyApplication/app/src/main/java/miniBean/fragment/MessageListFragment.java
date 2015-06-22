package miniBean.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

                AppController.getInstance().setConversationId(childVM.getId());

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ConversationVM childVM = adapter.getItem(i);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(getActivity().getString(R.string.post_delete_confirm));
                alertDialogBuilder.setPositiveButton(getActivity().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteConversation(childVM.getId());
                    }
                });
                alertDialogBuilder.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
        return view;
    }

    /*@Override
    public void onResume() {
        super.onStart();
        System.out.println("in onResume::::::::::");
        getAllConversation();
    }*/

    private void getAllConversation(){
        AppController.getApi().getAllConversation(AppController.getInstance().getSessionId(),new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> conversationVMs, Response response) {
                conversationVMList=conversationVMs;

                if(conversationVMList.size() == 0){
                 //   tipText.setVisibility(View.VISIBLE);
                }else {
                    adapter=new ConversationListAdapter(getActivity(),conversationVMList);
                    listView.setAdapter(adapter);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void deleteConversation(Long id){
        AppController.getApi().deleteConversation(id,AppController.getInstance().getSessionId(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                getAllConversation();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
