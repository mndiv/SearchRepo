package com.iloveandrroid.divya.searchrepo.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by KeerthanaS on 5/9/2016.
 */
public class TestUtilities extends AndroidTestCase {

    static ContentValues createRepositoryValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(RepoContract.RepoEntry.COLUMN_FULL_NAME, "FreeCodeCamp/FreeCodeCamp");
        testValues.put(RepoContract.RepoEntry.COLUMN_DESCRIPTION, "Working on JavaScript Projects");
        testValues.put(RepoContract.RepoEntry.COLUMN_LANGUAGE, "Java Script");
        testValues.put(RepoContract.RepoEntry.COLUMN_UPDATED, "Updated 5 days ago");

        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


}
