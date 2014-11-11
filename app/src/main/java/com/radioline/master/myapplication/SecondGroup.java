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

import com.badoo.mobile.util.WeakHandler;
import com.radioline.master.basic.Group;
import com.radioline.master.basic.GroupViewAdapter;
import com.radioline.master.soapconnector.Converts;
import com.splunk.mint.Mint;

import java.util.concurrent.ExecutionException;


public class SecondGroup extends Activity implements AdapterView.OnItemClickListener {

    private ListView lvSecond;
    private WeakHandler handler = new WeakHandler();
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

        Mint.initAndStartSession(this, "3b65ddeb");
        //Mint.enableDebug();

        setContentView(R.layout.activity_second_group);
        lvSecond = (ListView)findViewById(R.id.lvSecond);
        lvSecond.setOnItemClickListener(this);
        this.setTitle(getIntent().getStringExtra("Name"));
//        Converts tg = new Converts();
//        try {
//            GroupViewAdapter groupViewAdapter = new GroupViewAdapter(this, tg.getGroupsArrayListFromServer(getIntent().getStringExtra("parentid")));
//            listView.setAdapter(groupViewAdapter);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        dialog = ProgressDialog.show(this, getString(R.string.ProgressDialogTitle),
                getString(R.string.ProgressDialogMessage));
        Thread t = new Thread() {
            public void run() {
                Converts tg = new Converts();
                try {
                    groupViewAdapter = new GroupViewAdapter(SecondGroup.this, tg.getGroupsArrayListFromServer(getIntent().getStringExtra("parentid")));

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    public void run() {
                        if ((dialog!=null)&&(dialog.isShowing())){
                            dialog.dismiss();}
                        if (groupViewAdapter!=null){
                        lvSecond.setAdapter(groupViewAdapter);}
                    }
                });
            }
        };

        t.start();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                Intent intent = new Intent(this, ScanActivity.class);
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
        Intent intent = new Intent(this,ItemActivity.class);
        intent.putExtra("parentid",itemgroup.getId());
        intent.putExtra("Name",itemgroup.getName());
        startActivity(intent);
    }

}
