package com.radioline.master.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.parse.Parse;
import com.parse.ParseObject;
import com.radioline.master.basic.Basket;
import com.radioline.master.basic.Group;
import com.radioline.master.basic.GroupViewAdapter;
import com.radioline.master.basic.SystemService;
import com.radioline.master.soapconnector.Converts;
import com.splunk.mint.Mint;

import java.util.concurrent.ExecutionException;


public class FirstGroupActivity extends Activity implements AdapterView.OnItemClickListener {

    //https://play.google.com/apps/
    //https://code.google.com/p/android-query/wiki/API
    private ListView listView;
    private WeakHandler handler;
    private ProgressDialog dialog;
    private GroupViewAdapter groupViewAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        Mint.startSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Mint.closeSession(this);
        Mint.flush();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new WeakHandler();
        Mint.initAndStartSession(this, "3b65ddeb");
        //Mint.enableDebug();
        ParseObject.registerSubclass(Basket.class);
        Parse.enableLocalDatastore(getApplicationContext());

        Parse.initialize(this, "5pOXIrqgAidVKFx2mWnlMHj98NPYqbR37fOEkuuY", "oZII0CmkEklLvOvUQ64CQ6i4QjOzBIEGZfbXvYMG");


        setContentView(R.layout.activity_firstgroup);
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(this);

        SystemService ss = new SystemService(this);
        if (ss.isNetworkAvailable()) {
            dialog = ProgressDialog.show(this, getString(R.string.ProgressDialogTitle),
                    getString(R.string.ProgressDialogMessage));
            Thread t = new Thread() {
                public void run() {
                    Converts tg = new Converts();
                    try {
                        groupViewAdapter = new GroupViewAdapter(FirstGroupActivity.this, tg.getGroupsArrayListFromServer());

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
                            if (groupViewAdapter != null) {
                                listView.setAdapter(groupViewAdapter);
                            }
                        }

                        ;
                    });
                }
            };

            t.start();
        } else {
            Toast.makeText(FirstGroupActivity.this, getString(R.string.NoConnect), Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_firstgroup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                Intent intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_basket:
                intent = new Intent(this, BasketActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Group itemgroup = (Group) adapterView.getItemAtPosition(position);
        Intent intent = new Intent(this, SecondGroupActivity.class);
        intent.putExtra("parentid", itemgroup.getId());
        intent.putExtra("Name", itemgroup.getName());
        startActivity(intent);
    }


}