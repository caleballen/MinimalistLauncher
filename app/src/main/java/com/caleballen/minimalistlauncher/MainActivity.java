package com.caleballen.minimalistlauncher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<AppInfo> lockedApps = new ArrayList<>();

    public RecyclerAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        recyclerView.setItemAnimator(null);
        recyclerAdapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new AppListThread().execute();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void update(){
        recyclerAdapter.notifyDataSetChanged();
    }

    public void lockApp(int app){
        lockedApps.add(recyclerAdapter.appList.remove(app));
        update();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerAdapter.addApp(lockedApps.remove(0));
                update();
            }
        },3000);
    }
    public void lockAllApps(){
        findViewById(R.id.RecyclerView).setVisibility(View.INVISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.RecyclerView).setVisibility(View.VISIBLE);
            }
        },28800000);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case RecyclerAdapter.ContextMenuItems.LOCK:
                Toast.makeText(this, item.getTitle() + " " + item.getGroupId(), Toast.LENGTH_SHORT).show();
                lockApp(item.getGroupId());
                break;
            case RecyclerAdapter.ContextMenuItems.LOCK_ALL:
                Toast.makeText(this, "Locking all apps", Toast.LENGTH_SHORT).show();
                lockAllApps();
                break;
        }

        return super.onContextItemSelected(item);
    }

    public class AppListThread extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... Params){
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> allApps = packageManager.queryIntentActivities(i, 0);
            for(ResolveInfo resolveInfo: allApps){
                AppInfo app = new AppInfo();
                app.label = resolveInfo.loadLabel(packageManager);
                app.packageName = resolveInfo.activityInfo.packageName;
                app.icon = resolveInfo.activityInfo.loadIcon(packageManager);
                recyclerAdapter.addApp(app);
            }

            return "Success";
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            update();
        }
    }
}