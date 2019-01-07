package com.nurbol.android.tempmonitor;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView tempTextView, hmdtTextView, lightTextView, dateTextView, roomTextView;
    private RequestQueue mQueue;
    private static final String REQUEST_URL = "http://192.168.137.1:8080/api/sensors/data/";
    private String Url;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(
                R.color.blue_swipe, R.color.green_swipe,
                R.color.orange_swipe, R.color.red_swipe);

        tempTextView = (TextView) findViewById(R.id.tempTextView);
        hmdtTextView = (TextView) findViewById(R.id.hmdtTextView);
        lightTextView = (TextView) findViewById(R.id.lightTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        roomTextView = (TextView) findViewById(R.id.roomTextView);

        String sensorId = getIntent().getStringExtra("id");
        String sensorRoom = getIntent().getStringExtra("room");

        Url = REQUEST_URL + sensorId;

        roomTextView.setText("Room " + sensorRoom);

        mQueue = Volley.newRequestQueue(this);
        jsonParse();
    }

    @Override
    public void onRefresh() {
        Log.d("my_tag", "refresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jsonParse();

                mSwipeLayout.setRefreshing(false);
            }
        }, 3000);
    }

    private void jsonParse() {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject dataEntity = response.getJSONObject(0);

                            String temp = dataEntity.getString("temp");
                            String hmdt = dataEntity.getString("hmdt");
                            String date = dataEntity.getString("date");
                            String light = dataEntity.getString("light");

                            tempTextView.setText(temp + "°C");
                            hmdtTextView.setText(hmdt + "%");
                            lightTextView.setText(light);
                            dateTextView.setText(dateFormat(date));
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

    private String dateFormat(String dateTime) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date dateReal = null;
        try {
            dateReal = fmt.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d, HH:mm");
        String dateFormatted = fmtOut.format(dateReal);

        return dateFormatted;
    }
}
