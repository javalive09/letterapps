package com.javalive09.letterapps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity {

    private LetterRecyclerView letterRecyclerView;
    private AppGroupAdapter appGroupAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<AppGroup> groupDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMainView();
    }

    private void initMainView() {
        groupDataList = getGroupDataList();
        setContentView(R.layout.activity_main_layout);
        RecyclerView groupListView = findViewById(R.id.app_group_list);
        final TextView letterHint = findViewById(R.id.letter_hint);
        letterRecyclerView = findViewById(R.id.letter_list);
        appGroupAdapter = new AppGroupAdapter(groupDataList);
        groupListView.setAdapter(appGroupAdapter);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        groupListView.setLayoutManager(linearLayoutManager);
        letterRecyclerView.setData(groupDataList).setTouchLetterListener(new LetterRecyclerView.TouchLetterListener() {
            @Override
            public void onShow(String currentLetter, int index) {
                letterHint.setVisibility(View.VISIBLE);
                letterHint.setText(currentLetter);
                letterRecyclerView.setBackgroundResource(R.color.letter_hint_bg);
                linearLayoutManager.scrollToPositionWithOffset(index, 0);
                Logger.d("peter", " letter:" + currentLetter);
            }

            @Override
            public void onHide() {
                letterHint.setVisibility(View.GONE);
                letterRecyclerView.setBackground(null);
            }
        }).build(getApplicationContext());
        groupListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                refreshLetterRecyclerViewColor();
            }
        });
        registerReceiver();
        SharedPreferenceUtil.registerListener(getApplicationContext(), SharedPreferenceUtil.FAVORITE,
                onSharedPreferenceChangeListener);
    }

    private void refreshLetterRecyclerViewColor() {
        final int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        final int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        Logger.d("peter", "firstVisibleItem:" + firstVisibleItem + " ;lastVisibleItem:" +
                lastVisibleItem);
        letterRecyclerView.refreshItemColor(firstVisibleItem, lastVisibleItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        SharedPreferenceUtil.unregisterListener(getApplicationContext(), SharedPreferenceUtil.FAVORITE,
                onSharedPreferenceChangeListener);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener =
            (sharedPreferences, key) -> {
                if (sharedPreferences.contains(key)) {
                    boolean isFavorite = sharedPreferences.getBoolean(key, false);
                    String[] strings = key.split(":");
                    if (strings.length == 2) {
                        String packageName = strings[0];
                        String cls = strings[1];
                        String action = isFavorite ? Intent.ACTION_PACKAGE_ADDED : Intent.ACTION_PACKAGE_REMOVED;
                        refresh(action, packageName, cls);
                    }
                }
            };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Uri uri = intent.getData();
            if (uri != null) {
                String packageName = uri.getSchemeSpecificPart();
                refresh(intent.getAction(), packageName);
            }
        }
    };

    private void refresh(String action, String packageName) {
        refresh(action, packageName, null);
    }

    private void refresh(String action, String packageName, String cls) {
        switch (action) {
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE:
                if (TextUtils.isEmpty(cls)) {// install
                    Map<String, ?> favorites =
                            SharedPreferenceUtil.getAll(getApplicationContext(), SharedPreferenceUtil.FAVORITE);
                    installGroupList(favorites, packageName, groupDataList);
                } else {// favorite
                    Map<String, ?> favorites =
                            SharedPreferenceUtil.getAll(getApplicationContext(), SharedPreferenceUtil.FAVORITE);
                    installGroupList(favorites, packageName, groupDataList);
                }
                sort(groupDataList);
                refresh(groupDataList);
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
            case Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE:
                if (TextUtils.isEmpty(cls)) {// uninstall
                    List<AppGroup> removeAppGroupList = new ArrayList<>();
                    List<AppModel> removeAppModelList = new ArrayList<>();
                    for (AppGroup appGroup : groupDataList) {
                        removeAppModelList.clear();
                        for (AppModel appModel : appGroup.appModelList) {
                            if (TextUtils.equals(appModel.getApplicationPackageName(), packageName)) {
                                removeAppModelList.add(appModel);
                                break;
                            }
                        }
                        for (AppModel appModel : removeAppModelList) {
                            appGroup.appModelList.remove(appModel);
                            SharedPreferenceUtil
                                    .removeKey(getApplicationContext(), SharedPreferenceUtil.FAVORITE, appModel
                                            .getFavoriteKey());
                        }
                        if (appGroup.appModelList.size() == 0) {
                            removeAppGroupList.add(appGroup);
                        }
                    }
                    for (AppGroup appGroup : removeAppGroupList) {
                        groupDataList.remove(appGroup);
                    }

                } else {// favorite
                    AppGroup favoriteAppGroup = null;
                    for (AppGroup appGroup : groupDataList) {
                        if (appGroup.letter.equals(AppModel.FAVORITE_LETTER)) {
                            favoriteAppGroup = appGroup;
                            break;
                        }
                    }
                    if (favoriteAppGroup == null) {
                        return;
                    }
                    ArrayList<AppModel> tempAppModelList = new ArrayList<>(favoriteAppGroup.appModelList);
                    for (AppModel appModel : tempAppModelList) {
                        if (TextUtils.equals(appModel.getApplicationPackageName(), packageName)) {
                            favoriteAppGroup.appModelList.remove(appModel);
                        }
                    }
                    if (favoriteAppGroup.appModelList.size() == 0) {
                        groupDataList.remove(favoriteAppGroup);
                    }
                }
                sort(groupDataList);
                refresh(groupDataList);
                break;
            case Intent.ACTION_PACKAGE_CHANGED:
                Logger.d("peter", Intent.ACTION_PACKAGE_CHANGED);
                break;
        }
    }

    private void refresh(List<AppGroup> groupDataList) {
        appGroupAdapter.refreshData(groupDataList);
        letterRecyclerView.refresh(groupDataList);
        letterRecyclerView.post(refreshLetterRecyclerViewColor);
    }

    private final Runnable refreshLetterRecyclerViewColor = this::refreshLetterRecyclerViewColor;

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(broadcastReceiver, filter);

        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        registerReceiver(broadcastReceiver, sdFilter);
    }

    private List<AppGroup> getGroupDataList() {
        List<AppGroup> groupList = new ArrayList<>();
        Map<String, ?> favorites = SharedPreferenceUtil.getAll(getApplicationContext(), SharedPreferenceUtil.FAVORITE);
        installGroupList(favorites, null, groupList);
        sort(groupList);
        return groupList;
    }

    private void sort(List<AppGroup> groupDataList) {
        Collections.sort(groupDataList);
    }

    private void installGroupList(Map<String, ?> favorites, String packageName, List<AppGroup> groupList) {
        if (!TextUtils.equals(getPackageName(), packageName)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);

            for (ResolveInfo info : resolveInfoList) {
                AppModel appModel = new AppModel(this, info);
                appModel.getIcon();
                appModel.getLabel();
                appModel.getLetter();
                installData(groupList, appModel);
                if (favorites.containsKey(appModel.getFavoriteKey())) {
                    Object isFavorite = favorites.get(appModel.getFavoriteKey());
                    if (isFavorite instanceof Boolean && (Boolean) isFavorite) {
                        AppModel appModelFavorite = new AppModel(appModel);
                        appModelFavorite.setLetter(AppModel.FAVORITE_LETTER);
                        installData(groupList, appModelFavorite);
                    }
                }
            }
        }
    }

    private void installData(List<AppGroup> groupList, AppModel appModel) {
        AppGroup currentGroupData = null;
        for (AppGroup groupData : groupList) {
            if (TextUtils.equals(groupData.letter, appModel.getLetter())) {
                currentGroupData = groupData;
                break;
            }
        }
        if (currentGroupData == null) {
            currentGroupData = new AppGroup(appModel.getLetter(), appModel);
            groupList.add(currentGroupData);
        } else if (!currentGroupData.appModelList.contains(appModel)) {
            currentGroupData.appModelList.add(appModel);
        }
    }

}
