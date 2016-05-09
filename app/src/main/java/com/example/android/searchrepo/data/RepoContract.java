package com.example.android.searchrepo.data;

import android.provider.BaseColumns;

/**
 * Defines the table and column names for the repo database.
 */
public class RepoContract {
    /*
    Inner class that defines the table contents of the location table
    Students: This is where you will add the strings.  (Similar to what has been
    done for WeatherEntry)
    */


    public static final class MostStarsRepoEntry implements BaseColumns {
        public static final String TABLE_NAME = "moststars";

        //full name of the repo as provided by API
        public static final String COLUMN_FULL_NAME = "full_name";

        //description about the repo (stored as String)
        public static final String COLUMN_DESCRIPTION="short_desc";

        //language of the repo provided by API
        public static final String COLUMN_LANGUAGE = "language";

        //updated time ago stored as String
        public static final String COLUMN_UPDATED = "updated";

    }

    public static final class FewerStarsRepoEntry implements BaseColumns {
        public static final String TABLE_NAME = "fewerstars";
    }

    public static final class MostForksRepoEntry implements BaseColumns {
        public static final String TABLE_NAME = "mostforks";
    }

    public static final class FewerForksRepoEntry implements BaseColumns {
        public static final String TABLE_NAME = "fewerforks";
    }

    public static final class RecentUpdatedRepoEntry implements BaseColumns {
        public static final String TABLE_NAME = "recentUpdated";
    }



}
