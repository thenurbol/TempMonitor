package com.nurbol.android.tempmonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    Button goButton, filterButton;
    EditText roomEditText, firstTempEditText, secondTempEditText;
    private SwipeRefreshLayout mSwipeLayout;
    private RequestQueue mQueue;
    private TemperatureAdapter mAdapter;
    private static final String LOG_TAG = SearchActivity.class.getName();
    private static final String REQUEST_URL =
            "https://api.myjson.com/bins/pc7s0";
    //            "http://192.168.137.1:8080/api/sensors/data/current";
    List<Temperature> saveTemperatures = new ArrayList<>();


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

        goButton = (Button) findViewById(R.id.goButton);
        filterButton = (Button) findViewById(R.id.filterButton);
        roomEditText = (EditText) findViewById(R.id.roomEditText);
        firstTempEditText = (EditText) findViewById(R.id.firstTempEditText);
        secondTempEditText = (EditText) findViewById(R.id.secondTempEditText);

        ListView temperatureListView = (ListView) findViewById(R.id.list);
        // Create a new adapter that takes an empty list of temperatures as input
        mAdapter = new TemperatureAdapter(this, new ArrayList<Temperature>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        temperatureListView.setAdapter(mAdapter);

//        TemperatureAsyncTask task = new TemperatureAsyncTask();
//        task.execute(REQUEST_URL);

        mQueue = Volley.newRequestQueue(this);

        jsonParse();

        temperatureListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Temperature posItems = saveTemperatures.get(position);
                int sensorId = posItems.getId();
                String sensorRoom = posItems.getRoom();
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("id", Integer.toString(sensorId));
                intent.putExtra("room", sensorRoom);
                startActivity(intent);
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomEdit = roomEditText.getText().toString();
                for (int i = 0; i < saveTemperatures.size(); i++) {
                    if (roomEdit.equals(saveTemperatures.get(i).getRoom())) {
                        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                        int sensorId = saveTemperatures.get(i).getId();
                        String sensorRoom = saveTemperatures.get(i).getRoom();
                        intent.putExtra("id", Integer.toString(sensorId));
                        intent.putExtra("room", sensorRoom);
                        startActivity(intent);
                    }
                }
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String one = String.valueOf(firstTempEditText.getText());
                String two = String.valueOf(secondTempEditText.getText());
                if (!(one.equals("")) && !(two.equals(""))) {
                    mAdapter.addAll(saveTemperatures);
                    List<Temperature> temporary = new ArrayList<>();
                    int first = Integer.parseInt(one);
                    int second = Integer.parseInt(two);
                    for (int i = 0; i < saveTemperatures.size(); i++) {
                        if (Integer.parseInt(saveTemperatures.get(i).getTemperature()) >= first && Integer.parseInt(saveTemperatures.get(i).getTemperature()) <= second) {
                            temporary.add(saveTemperatures.get(i));
                        }
                    }
                    mAdapter.clear();
                    mAdapter.addAll(temporary);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Wrong request",
                            Toast.LENGTH_SHORT);

                    toast.show();
                }
            }
        });

    }

    //Swipe refresher
    @Override
    public void onRefresh() {
        Log.d("my_tag", "refresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveTemperatures.clear();

                jsonParse();

                mSwipeLayout.setRefreshing(false);
            }
        }, 3000);
    }


    private void jsonParse() {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, REQUEST_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            List<Temperature> temperatures = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject dataEntity = response.getJSONObject(i);
                                JSONObject sensorDataEntity = dataEntity.getJSONObject("sensorDataEntity");

                                String temp = sensorDataEntity.getString("temp");
                                String hmdt = sensorDataEntity.getString("hmdt");
                                String room = dataEntity.getString("room");
                                int id = dataEntity.getInt("id");
                                String date = sensorDataEntity.getString("date");
                                String light = sensorDataEntity.getString("light");

                                Temperature temperature = new Temperature(temp, hmdt, room, date, light, id);
                                temperatures.add(temperature);
                            }

                            mAdapter.clear();
                            mAdapter.addAll(temperatures);
                            saveTemperatures = temperatures;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            Intent numbersIntent = new Intent(SearchActivity.this, WeatherActivity.class);
            startActivity(numbersIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*    private class TemperatureAsyncTask extends AsyncTask<String, Void, List<Temperature>> {
     *//**
     * This method runs on a background thread and performs the network request.
     * We should not update the UI from a background thread, so we return a list of
     * {@link Temperature}s as the result.
     *//*
        @Override
        protected List<Temperature> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Temperature> result = QueryUtils.fetchTemperatureData(urls[0]);
            return result;
        }

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
    }*/

}
