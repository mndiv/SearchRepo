package com.example.android.searchrepo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by KeerthanaS on 5/10/2016.
 */
public class Utility {

    public static String getSortOption(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(context.getString(R.string.sortList),
                context.getString(R.string.pref_sort_default_MostStars));
    }

    public static String getLanguageOption(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.list_Of_Languages),
                context.getString(R.string.pref_language_default));
    }
}
