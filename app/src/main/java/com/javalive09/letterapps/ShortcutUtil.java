package com.javalive09.letterapps;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;

public class ShortcutUtil {

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public CharSequence[] getItems(List<ShortcutInfo> paramList) {
        if (paramList != null) {
            CharSequence[] arrayOfCharSequence = new CharSequence[paramList.size()];
            for (int i = 0, len = paramList.size(); i < len; i++) {
                arrayOfCharSequence[i] = paramList.get(i).getShortLabel();
            }
            return arrayOfCharSequence;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public List<ShortcutInfo> getShortcutListInfo(AppModel appModel) {
        LauncherApps localLauncherApps =
                (LauncherApps) appModel.context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        if (localLauncherApps != null && localLauncherApps.hasShortcutHostPermission()) {
            int queryFlags =
                    LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC | LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
                            | LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED;
            return localLauncherApps.getShortcuts(
                    new LauncherApps.ShortcutQuery().setPackage(appModel.getApplicationPackageName()).setQueryFlags
                            (queryFlags),
                    UserHandle.getUserHandleForUid(appModel.info.activityInfo.applicationInfo.uid));
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    public void launchShortcutAPP(Context context, ShortcutInfo shortcutInfo) {
        LauncherApps localLauncherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        if (localLauncherApps != null) {
            localLauncherApps.startShortcut(shortcutInfo.getPackage(), shortcutInfo.getId(), null, null,
                    Process.myUserHandle());
        }
    }

}
