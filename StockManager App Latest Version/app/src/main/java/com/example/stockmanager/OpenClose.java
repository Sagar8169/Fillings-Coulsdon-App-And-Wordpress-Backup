package com.example.stockmanager;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OpenClose extends AppCompatActivity {
    private Button btnSave;
    private Button[] openButtons, closeButtons;
    private String[] openTimes = new String[7], closeTimes = new String[7];
    private final String[] days = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_close);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OpenClose.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnSave = findViewById(R.id.btn_save);
        openButtons = new Button[]{
                findViewById(R.id.sunday_open), findViewById(R.id.monday_open),
                findViewById(R.id.tuesday_open), findViewById(R.id.wednesday_open),
                findViewById(R.id.thursday_open), findViewById(R.id.friday_open),
                findViewById(R.id.saturday_open)
        };

        closeButtons = new Button[]{
                findViewById(R.id.sunday_close), findViewById(R.id.monday_close),
                findViewById(R.id.tuesday_close), findViewById(R.id.wednesday_close),
                findViewById(R.id.thursday_close), findViewById(R.id.friday_close),
                findViewById(R.id.saturday_close)
        };

        for (int i = 0; i < 7; i++) {
            int finalI = i;
            openButtons[i].setOnClickListener(v -> showTimePicker(finalI, true));
            closeButtons[i].setOnClickListener(v -> showTimePicker(finalI, false));
        }

        btnSave.setOnClickListener(v -> saveSchedule());
        fetchScheduleFromServer();
    }

    private void fetchScheduleFromServer() {
        String url = "https://fillingscoulsdon.co.uk/wp-json/store/v1/schedule/";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < 7; i++) {
                            String day = capitalizeFirstLetter(days[i]);
                            JSONArray timeArray = response.getJSONArray(day);
                            openTimes[i] = timeArray.getString(0);
                            closeTimes[i] = timeArray.getString(1);

                            openButtons[i].setText(days[i] + " Open: " + openTimes[i]);
                            closeButtons[i].setText(days[i] + " Close: " + closeTimes[i]);
                        }
                    } catch (Exception e) {
                        Log.e("API_ERROR", "JSON Parse Error: " + e.getMessage());
                    }
                },
                error -> Log.e("API_ERROR", "Failed to fetch schedule: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + getSavedToken());
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void showTimePicker(int dayIndex, boolean isOpenTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute1) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            if (isOpenTime) {
                openTimes[dayIndex] = selectedTime;
                openButtons[dayIndex].setText(days[dayIndex] + " Open: " + selectedTime);
            } else {
                closeTimes[dayIndex] = selectedTime;
                closeButtons[dayIndex].setText(days[dayIndex] + " Close: " + selectedTime);
            }
        }, hour, minute, true).show();
    }

    private void saveSchedule() {
        try {
            JSONObject scheduleData = new JSONObject();
            JSONObject scheduleObject = new JSONObject();

            for (int i = 0; i < 7; i++) {
                String day = capitalizeFirstLetter(days[i]);
                JSONArray timeArray = new JSONArray();
                timeArray.put(openTimes[i]);
                timeArray.put(closeTimes[i]);
                scheduleObject.put(day, timeArray);
            }

            scheduleData.put("schedule", scheduleObject);
            sendScheduleToServer(scheduleData);
        } catch (Exception e) {
            Toast.makeText(this, "Error saving schedule", Toast.LENGTH_SHORT).show();
            Log.e("API_ERROR", "JSON Error: " + e.getMessage());
        }
    }

    private void sendScheduleToServer(JSONObject scheduleData) {
        String url = "https://fillingscoulsdon.co.uk/wp-json/store/v1/update/";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, scheduleData,
                response -> Toast.makeText(this, "Schedule Saved Successfully", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Failed to Save Schedule", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + getSavedToken());
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private String capitalizeFirstLetter(String str) {
        return (str == null || str.isEmpty()) ? str : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getSavedToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("api_token", "");
    }
}
