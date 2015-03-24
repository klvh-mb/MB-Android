package miniBean.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import miniBean.R;
import miniBean.app.AppController;

public class LogoutFragment extends Fragment {
    private static final String TAG = LogoutFragment.class.getName();
    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.logout, container, false);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.logout);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // confirm exit
                AlertDialog.Builder builder = new AlertDialog.Builder(LogoutFragment.this.getActivity());
                builder.setMessage(R.string.logout_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // clear session and exit
                                AppController.getInstance().clearAll();
                                AppController.getInstance().clearPreferences();

                                AppController.getInstance().exitApp();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return view;
    }
}
