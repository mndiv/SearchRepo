package com.example.android.searchrepo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements RepoListFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    private String mSortOrder, mLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSortOrder = Utility.getSortOption(this);
        mLang = Utility.getLanguageOption(this);



        Stetho.initialize(Stetho.newInitializerBuilder(getApplicationContext())
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(getApplicationContext()))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(getApplicationContext()))
                .build());


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        if(findViewById(R.id.repo_detail_container) != null){
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.repo_detail_container, new RepoDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sortOrder = Utility.getSortOption(this);
        String lang = Utility.getLanguageOption(this);
        //update the sortOrder in our second pane using the fragment manager
        if((sortOrder != null && !sortOrder.equals(mSortOrder)) || ( lang != null && !lang.equals(mLang))){
            RepoListFragment rf = (RepoListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_repo);
            if(null != rf){
                rf.onSettingsChanged();
            }
            mSortOrder = sortOrder;
            mLang = lang;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(RepoDetailFragment.DETAIL_URI, contentUri);

            RepoDetailFragment fragment = new RepoDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.repo_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this,DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
