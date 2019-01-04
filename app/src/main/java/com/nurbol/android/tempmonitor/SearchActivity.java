package com.nurbol.android.tempmonitor;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeLayout;

    private static final String LOG_TAG = SearchActivity.class.getName();
    /**
     * URL for temperature data from the USGS dataset
     */
    private static final String REQUEST_URL = "http://192.168.137.1:8080/api/sensors/data/current";



    /**
     * Adapter for the list of temperatures
     */
    private TemperatureAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Swipe Refresh
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(
                R.color.blue_swipe, R.color.green_swipe,
                R.color.orange_swipe, R.color.red_swipe);

        onRefresh();
//        // Find a reference to the {@link ListView} in the layout
//        ListView temperatureListView = (ListView) findViewById(R.id.list);
//        // Create a new adapter that takes an empty list of earthquakes as input
//        mAdapter = new TemperatureAdapter(this, new ArrayList<Temperature>());
//        // Set the adapter on the {@link ListView}
//        // so the list can be populated in the user interface
//        temperatureListView.setAdapter(mAdapter);
//
//        // Start the AsyncTask to fetch the earthquake data
//        TemperatureAsyncTask task = new TemperatureAsyncTask();
//        task.execute(REQUEST_URL);
    }
    //Swipe refresher
    @Override
    public void onRefresh() {
        Log.d("my_tag", "refresh");
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                // start refresh
                // Find a reference to the {@link ListView} in the layout
                ListView temperatureListView = (ListView) findViewById(R.id.list);
                // Create a new adapter that takes an empty list of earthquakes as input
                mAdapter = new TemperatureAdapter(SearchActivity.this, new ArrayList<Temperature>());
                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                temperatureListView.setAdapter(mAdapter);

                // Start the AsyncTask to fetch the earthquake data
                TemperatureAsyncTask task = new TemperatureAsyncTask();
                task.execute(REQUEST_URL);
                // stop refresh
                mSwipeLayout.setRefreshing(false);
            }
        }, 200);
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     * <p>
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Temperature. We won't do
     * progress updates, so the second generic is just Void.
     * <p>
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class TemperatureAsyncTask extends AsyncTask<String, Void, List<Temperature>> {
        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Temperature}s as the result.
         */
        @Override
        protected List<Temperature> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Temperature> result = QueryUtils.fetchTemperatureData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of earthquake data from a previous
         * query to USGS. Then we update the adapter with the new list of earthquakes,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Temperature> data) {
            // Clear the adapter of previous temperature data
            mAdapter.clear();
            // If there is a valid list of {@link Temperature}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}
