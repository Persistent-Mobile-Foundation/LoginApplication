/**
 * Copyright 2020 IBM Corp.
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

package com.sample.preemptiveloginandroid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.example.loginapplication.R;
import com.ibm.mfp.Configuration;
import com.ibm.mfp.ConfigurationListener;
import com.ibm.mfp.LiveUpdateManager;
import com.worklight.wlclient.api.WLClient;

public class LiveUpdate extends AppCompatActivity {
    private Context context;
    private RelativeLayout layout;
    private Button featureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WLClient.createInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        context = getApplicationContext();
        layout = (RelativeLayout) findViewById(R.id.layout);
        featureButton = (Button) findViewById(R.id.featureButton);
        hideFeature();
        checkForFeature();
    }

    public void checkForFeature() {

        LiveUpdateManager.getInstance(getApplicationContext()).obtainConfiguration(false, new ConfigurationListener() {
            @Override
            public void onSuccess(final Configuration configuration) {
                if (configuration.isFeatureEnabled("FestiveShopping")) {
                    final String buttonText = configuration.getProperty("buttonLabel");
                    final String imageName = configuration.getProperty("image");

                    if(!buttonText.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                featureButton.setText(buttonText);
                                featureButton.setVisibility(View.VISIBLE);
                            }
                        });

                    }

                    if(!imageName.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int resID = getResources().getIdentifier(imageName , "drawable", getPackageName());
                                layout.setBackground(ContextCompat.getDrawable(context,resID));
                            }
                        });
                    }

                } else {
                    hideFeature();
                }
            }

            @Override
            public void onFailure(com.worklight.wlclient.api.WLFailResponse wlFailResponse) {
                Log.e("Hello Live Update", wlFailResponse.getErrorMsg());
            }
        });
    }

    private void hideFeature() {
        layout.setBackground(ContextCompat.getDrawable(context, R.drawable.background));
        featureButton.setVisibility(View.GONE);
    }
}
