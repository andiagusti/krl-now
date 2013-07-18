package org.mufid.krlnow.org.mufid.krlnow.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;

import org.mufid.krlnow.R;

public class MainTableActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        getActionBarThemedContextCompat(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1)
                        }),
                this);
    }

    /**
     * Backward-compatible version of {@link ActionBar#getThemedContext()} that
     * simply returns the {@link android.app.Activity} if
     * <code>getThemedContext</code> is unavailable.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Context getActionBarThemedContextCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getActionBar().getThemedContext();
        } else {
            return this;
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_table, menu);
        return true;
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        Fragment fragment = new TrainScheduleTableFragment();
        Bundle args = new Bundle();
        args.putInt(TrainScheduleTableFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        return true;
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class TrainScheduleTableFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public TrainScheduleTableFragment() {
        }
        private ScrollView sView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_table, container, false);
            sView = (ScrollView) rootView;
            TableLayout table = (TableLayout) rootView.findViewById(R.id.table_schedule);



            for (int i = 0; i < 100; i++) {
                TableRow tr = (TableRow) inflater.inflate(R.layout.row_table, null);
                TextView tv1 = (TextView) tr.findViewById(R.id.row_time);
                tv1.setText("U yeah " + i);
                table.addView(tr);

            }

            try {
                int sViewX = savedInstanceState.getInt("sViewX");
                int sViewY = savedInstanceState.getInt("sViewY");

                sView.scrollTo(sViewX, sViewY);

            } catch (Exception ex) {
                // do nothing
            }

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState)
        {
            //---save whatever you need to persist—

            outState.putInt("sViewX", sView.getScrollX());
            outState.putInt("sViewY",sView.getScrollY());

            super.onSaveInstanceState(outState);

        }

    }

    public class Requester extends AsyncTask<String, Integer, Long> {

        public void setSuperTable(TableLayout superTable) {
            this.superTable = superTable;
        }

        TableLayout superTable;

        @Override
        protected Long doInBackground(String... destinations) {
            String destination = destinations[0];
            HttpRequest x = HttpRequest.get("http://infoka.krl.co.id/to/" + destination);
            x.
        }
    }
}
