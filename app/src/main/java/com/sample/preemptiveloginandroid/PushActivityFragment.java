package com.sample.preemptiveloginandroid;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.loginapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationButton;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationCategory;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationOptions;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;
import com.worklight.common.WLAnalytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PushActivityFragment extends Fragment implements View.OnClickListener, MFPPushNotificationListener {

    private static final String TAG = "MainActivityFragment";

    private BroadcastReceiver loginSuccessReceiver, loginRequiredReceiver, loginFailureReceiver;

    private Context _this;

    // Button references to enable/disable
    private Button subscribeBtn;
    private Button getSubscriptionBtn;
    private Button unsubscribeBtn;
    private Button unregisterBtn;

    private String[] tags;

    private MFPPush push = null;
    private MFPPushNotificationListener notificationListener;

    public PushActivityFragment() {
        // Mandatory empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _this = getActivity();

        WLAnalytics.init(getActivity().getApplication());


 /*
 *  Create buttons for interactive category
  */

        MFPPushNotificationOptions options = new MFPPushNotificationOptions();

        MFPPushNotificationButton firstButton = new MFPPushNotificationButton.Builder("Accept Button")
                .setIcon("extension_circle")
                .setLabel("Accept")
                .build();

        MFPPushNotificationButton secondButton = new MFPPushNotificationButton.Builder("Decline Button")
                .setIcon("extension_circle")
                .setLabel("Decline")
                .build();

        MFPPushNotificationButton secondButton1 = new MFPPushNotificationButton.Builder("Ignore Button")
                .setIcon("extension_circle")
                .setLabel("Ignore")
                .build();

        List<MFPPushNotificationButton> getButtons =  new ArrayList<MFPPushNotificationButton>();
        getButtons.add(firstButton);
        getButtons.add(secondButton);
        getButtons.add(secondButton1);

        List<MFPPushNotificationButton> getButtons1 =  new ArrayList<MFPPushNotificationButton>();
        getButtons1.add(firstButton);
        getButtons1.add(secondButton);

        List<MFPPushNotificationButton> getButtons2 =  new ArrayList<MFPPushNotificationButton>();
        getButtons2.add(firstButton);

        MFPPushNotificationCategory category = new MFPPushNotificationCategory.Builder("Category_Name1").setButtons(getButtons).build();
        MFPPushNotificationCategory category1 = new MFPPushNotificationCategory.Builder("Category_Name2").setButtons(getButtons1).build();
        MFPPushNotificationCategory category2 = new MFPPushNotificationCategory.Builder("Category_Name3").setButtons(getButtons2).build();

        List<MFPPushNotificationCategory> categoryList =  new ArrayList<MFPPushNotificationCategory>();
        categoryList.add(category);
        categoryList.add(category1);
        categoryList.add(category2);


        options.setInteractiveNotificationCategories(categoryList);

        push = MFPPush.getInstance();
        push.initialize(this.getContext(),options);


        // Option for receiving push notifications
        push.listen(this);


//        notificationListener = new MFPPushNotificationListener() {
//            @Override
//            public void onReceive(final MFPSimplePushNotification message) {
//                Log.i(TAG, "Received a Push Notification: " + message.toString());
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        new android.app.AlertDialog.Builder(_this)
//                                .setTitle("Received a Push Notification")
//                                .setMessage(message.getAlert())
//                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                    }
//                                })
//                                .show();
//                    }
//                });
//            }
//        };


        //Handle auto-login success
        loginSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Go back to main area
                Intent relaunchMain = new Intent(_this, MainActivity.class);
                getActivity().finish();
                _this.startActivity(relaunchMain);
            }
        };

        // Handle challenge broadcast
        loginRequiredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Open login screen
                Intent loginIntent = new Intent(_this, LoginActivity.class);
                _this.startActivity(loginIntent);
            }
        };

        // Handle challenge broadcast
        loginFailureReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showAlertMsg("Error", intent.getStringExtra("errorMsg"));
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(_this).registerReceiver(loginSuccessReceiver, new IntentFilter(Constants.ACTION_LOGIN_SUCCESS));
        LocalBroadcastManager.getInstance(_this).registerReceiver(loginRequiredReceiver, new IntentFilter(Constants.ACTION_LOGIN_REQUIRED));
        LocalBroadcastManager.getInstance(_this).registerReceiver(loginFailureReceiver, new IntentFilter(Constants.ACTION_LOGIN_FAILURE));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(_this).unregisterReceiver(loginSuccessReceiver);
        LocalBroadcastManager.getInstance(_this).unregisterReceiver(loginRequiredReceiver);
        LocalBroadcastManager.getInstance(_this).unregisterReceiver(loginFailureReceiver);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (push != null) {
            push.hold();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (push != null) {
            push.listen(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button pushSupportedBtn = (Button) view.findViewById(R.id.btn_push_supported);
        pushSupportedBtn.setOnClickListener(this);

        Button registerBtn = (Button) view.findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(this);

        Button getTagsBtn = (Button) view.findViewById(R.id.btn_get_tags);
        getTagsBtn.setOnClickListener(this);

        subscribeBtn = (Button) view.findViewById(R.id.btn_subscribe);
        subscribeBtn.setOnClickListener(null);

        getSubscriptionBtn = (Button) view.findViewById(R.id.btn_get_subscriptions);
        getSubscriptionBtn.setOnClickListener(this);

        unsubscribeBtn = (Button) view.findViewById(R.id.btn_unsubscribe);
        unsubscribeBtn.setOnClickListener(this);

        unregisterBtn = (Button) view.findViewById(R.id.btn_unregister);
        unregisterBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.btn_push_supported:
                if (push.isPushSupported()) {
                    showSnackbar("Push is supported");
                    WLAnalytics.send();
                } else {
                    showSnackbar("Push is not supported");
                }
                break;
            case R.id.btn_register:
                push.registerDevice(null, new MFPPushResponseListener<String>() {


                    @Override
                    public void onSuccess(String s) {}

                    @Override
                    public void onSuccess(JSONObject jobj) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                enableButtons();
                                showSnackbar("Registered Successfully");
                            }
                        });
                    }

                    @Override
                    public void onFailure(final MFPPushException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                showSnackbar("Failed to register device");
                                Log.d(TAG, "Failed to register device with error: " + e.toString());
                            }
                        });
                    }
                });
                break;
            case R.id.btn_get_tags:
                push.getTags(new MFPPushResponseListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> strings) {

                        if (strings.isEmpty()) {
                            tags = new String[0];
                            showAlertMsg("Tags", "There are no available tags");
                        } else {
                            tags = new String[strings.size()];

                            for (int i = 0; i < strings.size(); i++) {
                                tags[i] = strings.get(i);
                            }

                            showAlertMsg("Tags", strings.toString());
                        }
                    }

                    @Override
                    public void onSuccess(JSONObject jobj) {}

                    @Override
                    public void onFailure(MFPPushException e) {
                        showSnackbar("Error: " + e.getErrorMessage());
                        Log.d(TAG, "Error: " + e + " Doc URL: " + e.getDocUrl() + " Error code: " + e.getErrorCode());
                    }
                });
                break;
            case R.id.btn_subscribe:
                if (tags != null && tags.length > 0) {
                    if (!tags[0].equals("Push.ALL")) {
                        push.subscribe(tags, new MFPPushResponseListener<String[]>() {
                            @Override
                            public void onSuccess(String[] strings) {
                                showSnackbar("Subscribed successfully");
                            }

                            @Override
                            public void onSuccess(JSONObject jobj) {}

                            @Override
                            public void onFailure(MFPPushException e) {
                                showSnackbar("Failed to subscribe");
                                Log.d(TAG, "Failed to subscribe with error: " + e.toString());
                            }
                        });
                    } else {
                        showAlertMsg("Push Notifications", "There are no tags to subscribe to \n\n Try clicking on the \"Get Tags\" button");
                    }
                } else {
                    showAlertMsg("Push Notifications", "There are no tags to subscribe to \n\n Try clicking on the \"Get Tags\" button");
                }
                break;
            case R.id.btn_get_subscriptions:
                push.getSubscriptions(new MFPPushResponseListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> strings) {
                        tags = new String[strings.size()];

                        for (int i = 0; i < strings.size(); i++) {
                            tags[i] = strings.get(i);
                        }
                        showAlertMsg("Push Notification", strings.toString());
                    }

                    @Override
                    public void onSuccess(JSONObject jobj) {}

                    @Override
                    public void onFailure(MFPPushException e) {
                        showAlertMsg("Push Notification", e.getErrorMessage());
                        Log.d(TAG, "Failed to subscribe with error: " + e.toString());
                    }
                });
                break;
            case R.id.btn_unsubscribe:
                push.unsubscribe(tags, new MFPPushResponseListener<String[]>() {
                    @Override
                    public void onSuccess(String[] strings) {
                        updateTags(null);
                        showSnackbar("Unsubscribed successfully");
                    }

                    @Override
                    public void onSuccess(JSONObject jobj) {}

                    @Override
                    public void onFailure(MFPPushException e) {
                        showSnackbar("Failed to unsubscribe");
                        Log.d(TAG, "Failed to unsubscribe with error: " + e.toString());
                    }
                });
                break;
            case R.id.btn_unregister:
                push.unregisterDevice(new MFPPushResponseListener<String>() {

                    @Override
                    public void onSuccess(String s) {
                        disableButtons();

                        showSnackbar("Unregistered successfully");
                    }

                    @Override
                    public void onSuccess(JSONObject jobj) {}

                    @Override
                    public void onFailure(MFPPushException e) {
                        showSnackbar("Failed to unregister");
                        Log.d(TAG, "Failed to unregister device with error: " + e.toString());
                    }
                });
                break;
            default:
                throw new RuntimeException("Click not handled!!");
        }
    }

    private void enableButtons() {

        Runnable run = new Runnable() {
            public void run() {
                subscribeBtn.setEnabled(true);
                getSubscriptionBtn.setEnabled(true);
                unsubscribeBtn.setEnabled(true);
                unregisterBtn.setEnabled(true);
            }
        };
        getActivity().runOnUiThread(run);
    }

    private void disableButtons() {

        Runnable run = new Runnable() {
            public void run() {
                subscribeBtn.setEnabled(false);
                getSubscriptionBtn.setEnabled(false);
                unsubscribeBtn.setEnabled(false);
                unregisterBtn.setEnabled(false);
            }
        };
        getActivity().runOnUiThread(run);
    }

    private void showSnackbar(String message) {
        //noinspection ConstantConditions
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    private void updateTags(List<String> strings) {
        if (strings != null && strings.size() > 0) {
            tags = new String[strings.size()];

            for (int i = 0; i < strings.size(); i++) {
                tags[i] = strings.get(i);
            }
        } else {
            tags = new String[0];
        }
    }

    public void showAlertMsg(final String title, final String msg) {

        Runnable run = new Runnable() {
            @Override
            public void run() {
                // Create an AlertDialog Builder, and configure alert
                AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setTitle(title)
                        .setMessage(msg)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "Okay was pressed");
                            }
                        });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();

                // Display the dialog
                dialog.show();
            }
        };

        getActivity().runOnUiThread(run);
    }





    @Override
    public void onReceive(final MFPSimplePushNotification mfpSimplePushNotification) {

        String alert = "Alert: " + mfpSimplePushNotification.getAlert();
        String alertID = "ID: " + mfpSimplePushNotification.getId();
        String alertPayload = "Payload: " + mfpSimplePushNotification.getPayload();
        String category = "Category: " +  mfpSimplePushNotification.getCategory();
        String button = mfpSimplePushNotification.getInteractiveCategory();
        String action = mfpSimplePushNotification.getActionName();
        NotificationManager notif=(NotificationManager)_this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (getContext()).setContentTitle(alert).setContentText(alertID).
                setContentTitle(alertID).setSmallIcon(R.drawable.ic_launcher_background).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);

            if (action!=null) {
                if (action.equals("Accept Button")) {
                    System.out.print("Clicked Accept Action");
                    showAlertMsg("Push Notifications",  action + "\n" + " pressed");
                } else if (action.equals("Decline Button")) {
                    System.out.print("Clicked Decline Action");
                    showAlertMsg("Push Notifications",  action + "\n" + " pressed");
                } else if (action.equals("Ignore Button")) {
                    System.out.print("Clicked Ignore Action");
                    showAlertMsg("Push Notifications",  action + "\n" + " pressed");
                }

            }

                showAlertMsg("Push Notifications", alert + "\n" + alertID + "\n" + alertPayload );

    }
}
