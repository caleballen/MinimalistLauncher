package com.caleballen.minimalistlauncher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle() + " " + item.getGroupId(), Toast.LENGTH_SHORT).show();

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