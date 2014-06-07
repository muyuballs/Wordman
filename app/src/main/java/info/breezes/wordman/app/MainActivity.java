package info.breezes.wordman.app;

import android.app.*;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import info.breezes.wordman.utils.StreamUtils;

import java.io.IOException;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        if (!WordmanApplication.current.getBoolean("init", false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            },300);
        }
    }

    private void initData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在初始化，请稍等...");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = getResources().getAssets();
                try {
                    updateProgress(progressDialog, (int) (1 / 8.0 * 100), 0, handler);
                    String[] sqlArray = StreamUtils.readStrings(assetManager.open("init.sql", AssetManager.ACCESS_STREAMING), "UTF-8");
                    SQLiteDatabase db = WordmanApplication.current.getDbHelper().getWritableDatabase();
                    db.beginTransaction();
                    for (int i = 0; i < sqlArray.length; i++) {
                        String sql = sqlArray[i];
                        Log.d("Init", sql);
                        if (!sql.startsWith("---")) {
                            db.execSQL(sql);
                            updateProgress(progressDialog, (int) ((i + 1) / sqlArray.length * 100), 1, handler);
                        }
                    }
                    for (int i = 1; i < 8; i++) {
                        updateProgress(progressDialog, (int) ((i + 1) / 8.0 * 100), 0, handler);
                        sqlArray = StreamUtils.readStrings(assetManager.open("classes/class-" + i + ".sql", AssetManager.ACCESS_STREAMING), "UTF-8");
                        for (int j = 0; j < sqlArray.length; j++) {
                            String sql = sqlArray[j];
                            Log.d("Init", sql);
                            if (!sql.startsWith("---")) {
                                db.execSQL(sql);
                                updateProgress(progressDialog, (int) ((j + 1) / sqlArray.length * 100), 1, handler);
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    WordmanApplication.current.setBoolean("init", true);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        progressDialog.show();
    }

    private void updateProgress(final ProgressDialog progressDialog, final int value, final int type, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (type == 0) {
                    progressDialog.setProgress(value);
                } else {
                    progressDialog.setSecondaryProgress(value);
                }
            }
        });
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public boolean onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment=new RecordFragment();
                break;
            case 1:
                fragment = new LessonsFragment();
                break;
            case 2:
                startActivity(new Intent(this, SettingsActivity.class));
                return false;
            default:
                fragment = PlaceholderFragment.newInstance(position + 1);
                break;
        }
        // update the main content by replacing fragments

        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        return true;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
