package com.sample.preemptiveloginandroid;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.worklight.common.WLAnalytics;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Copyright 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class PreemptiveLoginApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        WLClient.createInstance(this);
        WLAnalytics.init(this);
        MFPPush.getInstance().initialize(this);

        //Initialize the MobileFirst SDK. This needs to happen just once.
       // WLClient.getInstance().pinTrustedCertificatePublicKey("ibmcoms.der");
//        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://github.ibm.com"));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

//        getBalanceButton.setOnClickListener(new View.OnClickListener() {



//                URI adapterPath = null;
//
//                try {
//                    adapterPath = new URI("/adapters/ResourceAdapter/balance");
//                   // adapterPath = new URI("https://www.w3schools.com/");
//
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//                WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
//                request.send(new WLResponseListener() {
//                    @Override
//                    public void onSuccess(WLResponse wlResponse) {
//                        Log.d("success", ""+wlResponse.getStatus());
//
//                        //updateTextView("Balance: " + wlResponse.getResponseText());
//                    }
//
//                    @Override
//                    public void onFailure(WLFailResponse wlFailResponse) {
//                        Log.d("Failure", wlFailResponse.getErrorMsg());
//                       // updateTextView("Failed to get balance.");
//                    }
//                });

    //    });

        //Initialize the challenge handler
        UserLoginChallengeHandler.createAndRegister();
    }
}
