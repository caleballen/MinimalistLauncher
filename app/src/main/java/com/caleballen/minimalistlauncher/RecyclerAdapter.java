package com.caleballen.minimalistlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public static ArrayList<AppInfo> appList = new ArrayList<AppInfo>();

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public ConstraintLayout row;
        public TextView textView;

        public ViewHolder(View itemView){
            super(itemView);

            row = (ConstraintLayout) itemView.findViewById(R.id.a_row);
            textView = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onClick(View view){
            int pos = getAdapterPosition();
            Context context = view.getContext();

            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appList.get(pos).packageName.toString());
            context.startActivity(launchIntent);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            int pos = getAdapterPosition();
            Context context = view.getContext();

            menu.add(this.getAdapterPosition(),view.getId(),'0',"dab");
        }

    }

    public RecyclerAdapter(Context context){

        PackageManager packageManager = context.getPackageManager();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = packageManager.queryIntentActivities(i, 0);
        for(ResolveInfo resolveInfo: allApps){
            AppInfo app = new AppInfo();
            app.label = resolveInfo.loadLabel(packageManager);
            app.packageName = resolveInfo.activityInfo.packageName;
            app.icon = resolveInfo.activityInfo.loadIcon(packageManager);
            appList.add(app);

        }
        Collections.sort(appList, new Comparator<AppInfo>() {
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.label.toString().compareToIgnoreCase(o2.label.toString());
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.textView;
        AppInfo app = appList.get(position);
        textView.setText(app.label.toString());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

}
