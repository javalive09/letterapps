package com.javalive09.letterapps;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppModelAdapter extends RecyclerView.Adapter<AppModelAdapter.Holder>
        implements View.OnClickListener, View.OnLongClickListener {

    private final List<AppModel> appModelList = new ArrayList<>();

    AppModelAdapter() {
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        AppModel appModel = appModelList.get(position);
        holder.name.setText(appModel.getLabel());

        holder.icon.setImageDrawable(appModel.getIcon());
        holder.icon.setTag(appModel);
        holder.icon.setOnClickListener(this);
        holder.icon.setOnLongClickListener(this);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return appModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshData(List<AppModel> appModelList) {
        if (!this.appModelList.equals(appModelList)) {
            this.appModelList.clear();
            this.appModelList.addAll(appModelList);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        AppModel appModel = (AppModel) view.getTag();
        Intent intent = view.getContext().getPackageManager().
                getLaunchIntentForPackage(appModel.getApplicationPackageName());
        if (intent != null) {
            intent.setComponent(appModel.getComponentName());
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        final ViewParent appGroupRecyclerView = view.getParent().getParent().getParent().getParent();
        final AppModel appModel = (AppModel) view.getTag();
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle(appModel.label);
        builder.setIcon(appModel.icon);
        if (appModel.isShortcutAvailable()) {
            final ShortcutUtil shortcutUtil = new ShortcutUtil();
            final List<ShortcutInfo> shortcutInfoList = shortcutUtil.getShortcutListInfo(appModel);
            if (shortcutInfoList != null && shortcutInfoList.size() > 0) {
                CharSequence[] arrayOfCharSequence = shortcutUtil.getItems(shortcutInfoList);
                builder.setItems(arrayOfCharSequence, (dialogInterface, i) -> {
                    ShortcutInfo shortcutInfo = shortcutInfoList.get(i);
                    shortcutUtil.launchShortcutAPP(appModel.context, shortcutInfo);
                });
            }
        }
        builder.setPositiveButton(R.string.detail, (dialogInterface, i) -> showDetailStopView(view.getContext(), appModel));
        final boolean isFavorite = SharedPreferenceUtil.getBoolean(appModel.context,
                SharedPreferenceUtil.FAVORITE, appModel.getFavoriteKey());
        builder.setNegativeButton(isFavorite ? R.string.favorite_cancel : R.string.favorite, (dialog, which) -> SharedPreferenceUtil.putBoolean(appModel.context, SharedPreferenceUtil.FAVORITE, appModel
                .getFavoriteKey(), !isFavorite));
        builder.setOnDismissListener(dialogInterface -> ((AppGroupRecyclerView) appGroupRecyclerView).setScrollEnable(true)).show();
        ((AppGroupRecyclerView) appGroupRecyclerView).setScrollEnable(false);
        return true;
    }

    private void showDetailStopView(Context context, AppModel paramAppModel) {
        if (paramAppModel != null) {
            Intent localIntent = new Intent();
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.setData(Uri.fromParts("package", paramAppModel.getApplicationPackageName(), null));
            context.startActivity(localIntent);
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        LinearLayout.LayoutParams originLayoutParams;
        LinearLayout.LayoutParams downLayoutParams;

        @SuppressLint("ClickableViewAccessibility")
        Holder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.app_icon);
            name = (TextView) itemView.findViewById(R.id.app_name);
            originLayoutParams = (LinearLayout.LayoutParams) icon.getLayoutParams();
            downLayoutParams = new LinearLayout.LayoutParams(originLayoutParams);
            downLayoutParams.height = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen
                    .item_icon_height_down);
            downLayoutParams.width = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen
                    .item_icon_width_down);
            downLayoutParams.leftMargin = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.item_icon_margin_left);
            downLayoutParams.topMargin = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.item_icon_margin_top);
            icon.setOnTouchListener(onTouchListener);
        }

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setLayoutParams(downLayoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setLayoutParams(originLayoutParams);
                        break;
                }
                return false;
            }
        };
    }

}
