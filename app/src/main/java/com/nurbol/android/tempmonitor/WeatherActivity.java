package com.nurbol.android.tempmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WeatherActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private static final String REQUEST_URL = "http://192.168.137.1:8080/api/weather/city?city=saint-etienne";
    //"https://api.myjson.com/bins/wcqts";
    TextView cityWtr, tempWtr, hmdtWtr, sunsetWtr, sunriseWtr, descrWtr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityWtr = (TextView) findViewById(R.id.cityWtr);
        tempWtr = (TextView) findViewById(R.id.tempWtr);
        hmdtWtr = (TextView) findViewById(R.id.hmdtWtr);
        sunsetWtr = (TextView) findViewById(R.id.sunsetWtr);
        sunriseWtr = (TextView) findViewById(R.id.sunriseWtr);
        descrWtr = (TextView) findViewById(R.id.descrWtr);

        mQueue = Volley.newRequestQueue(this);
        jsonParse();
    }


    private void jsonParse() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REQUEST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray weather = response.getJSONArray("weather");

                            for (int i = 0; i < weather.length(); i++) {
                                JSONObject first = weather.getJSONObject(i);
                                String description = first.getString("description");
                                descrWtr.setText(description);
                            }

                            JSONObject main = response.getJSONObject("main");
                            JSONObject sys = response.getJSONObject("sys");

                            String name = response.getString("name");
                            double temperature = main.getDouble("temp") - 273.15;

                            int humidity = main.getInt("humidity");
//                            long sunrise = sys.getLong("sunrise");
//                            long sunset = sys.getLong("sunset");

                            Integer temp = (int) (double) temperature;
                            tempWtr.setText(temp + "°C");
                            hmdtWtr.setText(humidity + "%");
                            cityWtr.setText(name);
//                            sunriseWtr.setText(dateFormat(sunrise));
//                            sunsetWtr.setText(dateFormat(sunset));

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

    private String dateFormat(long dateTime) {
        Date date = new Date(dateTime * 1000L);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String formattedDate = fmt.format(date);

        Date dateReal = null;
        try {
            dateReal = fmt.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");
        String dateFormatted = fmtOut.format(dateReal);

        return dateFormatted;
    }
}
