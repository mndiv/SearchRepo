package com.example.android.searchrepo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
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

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.searchrepo";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    /*
    public static final String PATH_MOSTSTARS = "moststars";
    public static final String PATH_FEWERSTARS = "fewerstars";
    public static final String PATH_MOSTFORKS = "mostforks";
    public static final String PATH_FEWERFORKS = "fewerforks";
    public static final String PATH_UPDATEDRECENTLY = "recentUpdated";
*/
    public static final String PATH_REPO = "repoList";



    public static final class RepoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPO;



        public static final String TABLE_NAME = "RepoTable";

        //full name of the repo as provided by API
        public static final String COLUMN_FULL_NAME = "full_name";

        //avatar URL of the repo if available otherwise default
        public static final String COLUMN_AVATAR_URL = "avatar";

        //description about the repo (stored as String)
        public static final String COLUMN_DESCRIPTION = "short_desc";

        //repo url for the repo (stored as String)
        public static final String COLUMN_REPO_URL = "repo_url";

        //language of the repo provided by API
        public static final String COLUMN_LANGUAGE = "language";

        //updated time ago stored as String
        public static final String COLUMN_UPDATED = "updated";

        //Created time ago stored as String
        public static final String COLUMN_CREATED = "created";

        //pushed time ago stored as String
        public static final String COLUMN_PUSHED = "pushed";

        //stargazer count as provided by API
        public static final String COLUMN_STARCOUNT = "star_count";

        //Watchgazer count as provided by API
        public static final String COLUMN_WATCHCOUNT = "watch_count";

        //fork count as provided by API
        public static final String COLUMN_FORKCOUNT = "fork_count";

        //issue count as provided by API
        public static final String COLUMN_ISSUECOUNT = "issue_count";

        public static Uri buildMostStarsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }
        public static String getNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
