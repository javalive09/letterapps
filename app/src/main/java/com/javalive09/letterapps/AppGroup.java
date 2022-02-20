package com.javalive09.letterapps;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

public class AppGroup implements Comparable<AppGroup> {

    String letter;

    List<AppModel> appModelList = new ArrayList<>();

    AppGroup(String letter, AppModel appModel) {
        this.letter = letter;
        this.appModelList.add(appModel);
    }

    @Override
    public int compareTo(AppGroup o) {
        if (TextUtils.equals(letter, AppModel.NO_LETTER)) {
            return 1;
        }

        if (TextUtils.equals(o.letter, AppModel.NO_LETTER)) {
            return -1;
        }

        if (TextUtils.equals(letter, MainActivity.FAVORITE_LETTER)) {
            return -1;
        }

        if (TextUtils.equals(o.letter, MainActivity.FAVORITE_LETTER)) {
            return 1;
        }

        return letter.compareTo(o.letter);
    }

    @Override
    public String toString() {
        return "AppGroup{" +
                "letter='" + letter + '\'' +
                ", appModelList=" + appModelList +
                '}';
    }
}
