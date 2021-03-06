package org.mufid.krlnow.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import org.mufid.krlnow.R;
import org.mufid.krlnow.etc.RegexRetrieve;
import org.mufid.krlnow.models.TrainStatusModel;
import org.mufid.krlnow.models.TrainStatusRawData;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

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
    public class TrainScheduleTableFragment extends Fragment {
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
            final View rootView = inflater.inflate(R.layout.fragment_main_table, container, false);
            //sView = (ScrollView) rootView;
            TableLayout table = (TableLayout) rootView.findViewById(R.id.table_schedule);

            for (int i = 1000; i < 100; i++) {
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
            final LayoutInflater _inflater = inflater;
            Button butRefresh = (Button) rootView.findViewById(R.id.schedule_refresh);
            butRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Requester<ArrayList<TrainStatusModel>> req = new Requester<ArrayList<TrainStatusModel>>();
                    req.setList(new ArrayList<TrainStatusModel>());
                    req.setInflater(_inflater);
                    req.setMasterView(rootView);

                    Spinner destination = (Spinner) findViewById(R.id.destination_dropdown);
                    String destinationstring;
                    destinationstring = RegexRetrieve.first((String) destination.getSelectedItem(), "([A-Z]+)\\s-\\s[A-Za-z]+");

                    ProgressBar pro = (ProgressBar) findViewById(R.id.request_progress);
                    pro.setVisibility(View.VISIBLE);

                    TableLayout table = (TableLayout) rootView.findViewById(R.id.table_schedule);
                    table.removeAllViews();

                    TextView notFoundMessage = (TextView) findViewById(R.id.text_no_trains_found);
                    notFoundMessage.setVisibility(View.GONE);

                    req.execute(destinationstring.toLowerCase());
                }
            });

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState)
        {
            //---save whatever you need to persist—

            //outState.putInt("sViewX", sView.getScrollX());
            //outState.putInt("sViewY",sView.getScrollY());

            super.onSaveInstanceState(outState);

        }

    }

    private class Requester<E extends List> extends AsyncTask<String, Integer, E> {

        private void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        LayoutInflater inflater;

        public void setMasterView(View masterView) {
            this.masterView = masterView;
        }

        E list;

        public void setList(E list) {
            this.list = list;
        }

        View masterView;

        @Override
        protected E doInBackground(String... destinations) {
            String destination = destinations[0];
            String originuri = "http://infoka.krl.co.id/to/" + destination;
            HttpRequest x = HttpRequest.get(originuri);
            String[] cookiesRaw1 = x.header("Set-Cookie").split("; ");
            String ci_session = null;
            for (String cookies : cookiesRaw1 ) {
                if (cookies.startsWith("ci_session")) {
                    ci_session = cookies.substring("ci_session".length());
                }
            }
            String token = null;
            String[] regexes = new String[] {
                    "['|]([A-Za-z0-9]{45,999})['|].+.split"
            };
            for (int i = 0; token == null; i++) {
                token = RegexRetrieve.first(x.body(), regexes[i]);
            }
            try {
                String y = HttpRequest.get("http://infoka.krl.co.id/" + token)
                        .header("Referer", originuri).body();
                TrainStatusRawData json = new Gson().fromJson(y, TrainStatusRawData.class);
                json.fillListWithData(list);
            } catch (Exception ex) {
                token = token == null ? "null" : token;
                Log.e("ERROR", "with token " + token);
            }
            return list;
        }

        @Override
        protected void onPostExecute(E e) {
            super.onPostExecute(e);

            ProgressBar pro = (ProgressBar) findViewById(R.id.request_progress);
            pro.setVisibility(View.GONE);

            TableLayout table = (TableLayout) masterView.findViewById(R.id.table_schedule);

            List<TrainStatusModel> x = e;
            for (TrainStatusModel entry : x ) {
                TableRow tr = (TableRow) inflater.inflate(R.layout.row_table, null);
                TextView tv1 = (TextView) tr.findViewById(R.id.row_time);
                TextView tv2 = (TextView) tr.findViewById(R.id.row_routename);
                tv1.setText(entry.getTrainNumber() + "");
                tv2.setText(entry.getStatusString() + "");
                table.addView(tr);
            }

            if (x.size() == 0) {
                TextView notFoundMessage = (TextView) findViewById(R.id.text_no_trains_found);
                notFoundMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
