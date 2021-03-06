package com.radioline.master.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.radioline.master.basic.BaseValues;

public class LoginActivity extends Activity {


    public static Context contextOfApplication;
    private Button btExit, btLogin;
    private EditText etUserId, etPasswordId;

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextOfApplication = getApplicationContext();


        setContentView(R.layout.activity_login);


        etUserId = (EditText) findViewById(R.id.etUserId);

        String userID = BaseValues.GetValue("UserId");
        if (userID != null)
            etUserId.setText(userID);

        etPasswordId = (EditText) findViewById(R.id.etPasswordId);
        String passwordId = BaseValues.GetValue("PasswordId");
        if (passwordId != null)
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
                login();
            }
        });


        if ((!userID.isEmpty()) && (!passwordId.isEmpty())) {
            login();
        }

    }

    private void login() {
        Integer tlog = null;
        try {
            tlog = Integer.parseInt(etUserId.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, getString(R.string.NoLogin), Toast.LENGTH_LONG).show();
            return;
        }
        if (tlog == null) {
            Toast.makeText(LoginActivity.this, getString(R.string.NoLogin), Toast.LENGTH_LONG).show();
            return;
        }
        ParseUser.logInInBackground(tlog.toString(), etPasswordId.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    if ((Boolean) ParseUser.getCurrentUser().get("Enable")) {
                        BaseValues.SetValue("UserId", ParseUser.getCurrentUser().getUsername());
                        BaseValues.SetValue("PasswordId", etPasswordId.getText().toString());
                        BaseValues.SetValue("PartnerId", ParseUser.getCurrentUser().get("PartnerId").toString());
                        ParseUser.getCurrentUser().increment("RunCount");
                        ParseUser.getCurrentUser().saveInBackground();

                        ParsePush.subscribeInBackground("", new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                                } else {
                                    Log.e("com.parse.push", "failed to subscribe for push", e);
                                }
                            }
                        });
                        //loadParseItems();
                        ParseConfig.getInBackground();
                        ParseAnalytics.trackAppOpened(getIntent());


                        //Intent intent = new Intent(LoginActivity.this, FirstGroupActivity.class);
                        Intent intent = new Intent(LoginActivity.this, Groups.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.NoLogin), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.NoLogin), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


//    private void loadParseItems(){
//        ParseQuery query = new ParseQuery("ParseItems");
//        query.whereEqualTo("Availability", true);
//
//        query.findInBackground(new FindCallback<ParseItems>() {
//            public void done(final List<ParseItems> parseItemsList, ParseException e) {
//                // Remove the previously cached results.
//                ParseObject.unpinAllInBackground(new DeleteCallback() {
//                    public void done(ParseException e) {
//                        // Cache the new results.
//                        ParseObject.pinAllInBackground(parseItemsList);
//                    }
//                });
//            }
//        });
//    }


}
