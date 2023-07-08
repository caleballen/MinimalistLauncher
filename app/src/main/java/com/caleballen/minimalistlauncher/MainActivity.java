package com.caleballen.minimalistlauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
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
        String appName = recyclerAdapter.appList.get(app).packageName.toString();
        lockedApps.add(recyclerAdapter.appList.remove(app));

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(appName, true);
        Log.d("sharedPref", "put" + appName);
        editor.apply();

        update();
    }
    public void unlockAllApps(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        update();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case RecyclerAdapter.ContextMenuItems.LOCK:
                Toast.makeText(this, item.getTitle() + " " + item.getGroupId(), Toast.LENGTH_SHORT).show();
                lockApp(item.getGroupId());
                break;
            case RecyclerAdapter.ContextMenuItems.UNLOCK_ALL:
                Toast.makeText(this, "Unlocking all apps", Toast.LENGTH_SHORT).show();
                unlockAllApps();
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

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

            List<ResolveInfo> allApps = packageManager.queryIntentActivities(i, 0);
            for(ResolveInfo resolveInfo: allApps){
                AppInfo app = new AppInfo();
                app.label = resolveInfo.loadLabel(packageManager);
                app.packageName = resolveInfo.activityInfo.packageName;
                app.icon = resolveInfo.activityInfo.loadIcon(packageManager);

                boolean isLocked = sharedPref.getBoolean(resolveInfo.activityInfo.packageName,false);

                if(!isLocked) {
                    recyclerAdapter.addApp(app);
                }
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