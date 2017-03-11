package com.iloveandrroid.divya.searchrepo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by KeerthanaS on 5/10/2016.
 */
class RepoAdapter extends CursorAdapter {

    private static final String LOG_TAG = RepoAdapter.class.getSimpleName();

    public RepoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView descView;
        public final TextView langView;
        public final TextView updateView;
        public final View circleLangView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_fullName_textView);
            descView = (TextView) view.findViewById(R.id.list_item_desc_textView);
            langView = (TextView) view.findViewById(R.id.list_item_lang_textView);
            updateView = (TextView) view.findViewById(R.id.list_item_update_textView);
            circleLangView=view.findViewById(R.id.circle_id);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_repo, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read full Name of Repo from cursor
        String fullName = cursor.getString(RepoListFragment.COL_REPO_FULLNAME);
        // Read full Name of Repo from cursor
        String desc = cursor.getString(RepoListFragment.COL_REPO_DESC);
        // Read full Name of Repo from cursor
        String lang = cursor.getString(RepoListFragment.COL_REPO_LANG);
        // Read full Name of Repo from cursor
        String updated = cursor.getString(RepoListFragment.COL_REPO_PUSHED);
        String empty = "null";
        // set name on it
        if (fullName.equals("Validation Failed")) {

            viewHolder.nameView.setText("We couldn’t find any repositories matching '" + desc + "'");
            viewHolder.descView.setVisibility(View.GONE);
            viewHolder.langView.setVisibility(View.GONE);
            viewHolder.updateView.setVisibility(View.GONE);
            viewHolder.circleLangView.setVisibility(View.GONE);

        } else {
            viewHolder.nameView.setText(fullName);
            // set description on it
            if (desc.equals(empty))
                viewHolder.descView.setText("");
            else
                viewHolder.descView.setText(desc);
            // set language on it
            if (lang.equals(empty)) {
                viewHolder.langView.setText("");
                viewHolder.circleLangView.setVisibility(View.GONE);
            }
            else {
                viewHolder.langView.setText(lang);

                if(lang.equals("JavaScript")) {
                    final GradientDrawable shape = (GradientDrawable) viewHolder.circleLangView.getBackground();
                    shape.setColor(context.getResources().getColor(R.color.colorRed));
                }
            }
            // set Updated repo on it
            viewHolder.updateView.setText("Updated on " + updated);
        }
    }
}
