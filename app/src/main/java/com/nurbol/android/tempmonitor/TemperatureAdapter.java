package com.nurbol.android.tempmonitor;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TemperatureAdapter extends ArrayAdapter<Temperature> {

    private static final String DATE_SEPARATOR = "T";

    public TemperatureAdapter(Activity context, ArrayList<Temperature> temperature) {
        super(context, 0, temperature);
    }

    private int getTemperatureColor(String temperatureDegreeStr) {
        int temperatureDegreeColorResourceId;
        int temperatureDegree = Integer.parseInt(temperatureDegreeStr);
        int temperatureDegreeFloor;

        if (temperatureDegree < 20) {
            temperatureDegreeFloor = 1;
        } else if (temperatureDegree >= 20 && temperatureDegree <=25) {
            temperatureDegreeFloor = 2;
        } else {
            temperatureDegreeFloor = 3;
        }

        switch (temperatureDegreeFloor) {
            case 1:
                temperatureDegreeColorResourceId = R.color.cold;
                break;
            case 2:
                temperatureDegreeColorResourceId = R.color.warm;
                break;
            case 3:
                temperatureDegreeColorResourceId = R.color.hot;
                break;
            default:
                temperatureDegreeColorResourceId = R.color.darkGreen;
                break;
        }
        return ContextCompat.getColor(getContext(), temperatureDegreeColorResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Temperature currentTemperature = getItem(position);

//////////////////////////////////////////////////////////////////////////////////////////////////
        // Find the TextView with view ID temperatureDegree
        TextView temperatureDegreeView = (TextView) listItemView.findViewById(R.id.temperatureDegree);
        // Format the temperatureDegree
        String formattedTemperatureDegree = currentTemperature.getTemperature();

        temperatureDegreeView.setText(formattedTemperatureDegree + "°C");
        // Set the proper background color on the temperatureDegree circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable temperatureDegreeCircle = (GradientDrawable) temperatureDegreeView.getBackground();

        // Get the appropriate background color based on the current
        int temperatureDegreeColor = getTemperatureColor(currentTemperature.getTemperature());

        // Set the color on the temperatureDegree
        temperatureDegreeCircle.setColor(temperatureDegreeColor);
/////////////////////////////////////////////////////////////////////////////////
        String humidity = currentTemperature.getHumidity();


        // Find the TextView with view ID
        TextView humidityView = (TextView) listItemView.findViewById(R.id.humidity);

        humidityView.setText(humidity + "%");
///////////////////////////////////////////////////////////////////////////////////////
        String light = currentTemperature.getLight();


        // Find the TextView with view ID
        TextView lightView = (TextView) listItemView.findViewById(R.id.light);

        lightView.setText(light);
///////////////////////////////////////////////////////////////////////////////////////
        String roomNumber = currentTemperature.getRoom();


        // Find the TextView with view ID
        TextView roomNumberView = (TextView) listItemView.findViewById(R.id.roomNumber);

        roomNumberView.setText("Room " + roomNumber);
///////////////////////////////////////////////////////////////////////////////////////////////////////
        String dateTime = currentTemperature.getDate();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date dateReal = null;
        try {
            dateReal = fmt.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String dateTimeZone = fmtOut.format(dateReal);

        String date;
        String time;

        String[] parts = dateTimeZone.split(DATE_SEPARATOR);
        date = parts[0];
        time = parts[1];

        String timeR = time.substring(0, 5);

        // Find the TextView with view ID
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);

        dateView.setText(date);

        // Find the TextView with view ID
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        timeView.setText(timeR);

        // Return the list item view that is now showing the appropriate data
        return listItemView;

}}
