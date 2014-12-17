package com.radioline.master.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.radioline.master.basic.BaseValues;
import com.radioline.master.basic.Basket;
import com.radioline.master.basic.ParseSetting;
import com.radioline.master.basic.SystemService;
import com.radioline.master.soapconnector.Link;
import com.splunk.mint.Mint;

import org.ksoap2.serialization.PropertyInfo;

public class LoginActivity extends Activity {

    private static String rtvalue;
    Button btExit, btLogin;
    EditText etUserId, etPasswordId;
    private WeakHandler handler = new WeakHandler();
    private ProgressDialog dialog;


    @Override
    protected void onStop() {
        super.onStop();
        Mint.closeSession(this);
        Mint.flush();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Mint.startSession(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.initAndStartSession(this, getString(R.string.mint));
        //Mint.enableDebug();
        setContentView(R.layout.activity_login);

        ParseCrashReporting.enable(this);
            ParseObject.registerSubclass(ParseSetting.class);
        ParseObject.registerSubclass(Basket.class);
        //ParseObject.registerSubclass(ParseGroups.class);
            Parse.enableLocalDatastore(getApplicationContext());
            Parse.initialize(this, "5pOXIrqgAidVKFx2mWnlMHj98NPYqbR37fOEkuuY", "oZII0CmkEklLvOvUQ64CQ6i4QjOzBIEGZfbXvYMG");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        //ParseConfig.getInBackground();
        //ParseConfig.getInBackground();



        etUserId = (EditText) findViewById(R.id.etUserId);

        String userID = BaseValues.GetValue("UserId");
        if (userID!=null)
            etUserId.setText(userID);

        etPasswordId = (EditText) findViewById(R.id.etPasswordId);
        String passwordId = BaseValues.GetValue("PasswordId");
        if (passwordId!=null)
            etPasswordId.setText(passwordId);


        btExit = (Button) findViewById(R.id.btExit);
        btLogin = (Button) findViewById(R.id.btLogin);
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        if ((!userID.isEmpty()) && (!passwordId.isEmpty())) {
            loadData();
        }

    }


    private void loadData() {
        SystemService ss = new SystemService(LoginActivity.this);
        if (ss.isNetworkAvailable()) {
            dialog = ProgressDialog.show(LoginActivity.this, getString(R.string.ProgressDialogTitle),
                    getString(R.string.ProgressDialogMessage));
            Thread t = new Thread() {
                public void run() {


                    String userId = etUserId.getText().toString();
                    String passwordId = etPasswordId.getText().toString();

                    Link link = new Link();
                    PropertyInfo pi0 = new PropertyInfo();
                    pi0.setName("UserId");
                    pi0.setValue(Integer.valueOf(userId));
                    pi0.setType(Integer.class);

                    PropertyInfo pi1 = new PropertyInfo();
                    pi1.setName("Password");
                    pi1.setValue(passwordId);
                    pi1.setType(String.class);


                    rtvalue = link.getFromServerSoapPrimitive("Login", new PropertyInfo[]{pi0, pi1}).toString();
                    if (rtvalue.startsWith("false")) rtvalue = null;

                    if ((etUserId.getText().toString().length() > 0)
                            && (etPasswordId.getText().toString().length() > 0)
                            && (rtvalue != null)) {
                        BaseValues.SetValue("UserId", userId);
                        BaseValues.SetValue("PasswordId", passwordId);
                        BaseValues.SetValue("PartnerId", rtvalue);


                    } else {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                String userId = etUserId.getText().toString();
                                String passwordId = etPasswordId.getText().toString();
                                if ((userId.length() == 0) || (passwordId.length() == 0)) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.NonLoginAndPassword), Toast.LENGTH_LONG).show();
                                    if (userId.length() == 0) {
                                        etUserId.requestFocus();
                                    }
                                    if (passwordId.length() == 0) {
                                        etPasswordId.requestFocus();
                                    }
                                }
                                if (rtvalue == null) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.NoLogin), Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    }


                    handler.post(new Runnable() {
                        public void run() {
                            if (dialog != null) {
                                if (dialog.isShowing()) {
                                    try {
                                        dialog.dismiss();
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    ;
                                }
                            }
                            if (rtvalue != null) {
                                Intent intent = new Intent(LoginActivity.this, FirstGroupActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            };

            t.start();
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.NoConnect), Toast.LENGTH_LONG).show();
        }
    }

}