package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    Button findBtn;
    EditText cityEt;
    TextView infoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findBtn = findViewById(R.id.findBtn);
        cityEt = findViewById(R.id.cityEt);
        infoTv = findViewById(R.id.infoTv);
        infoTv.setVisibility(View.INVISIBLE);
    }

    public void getWeather(View view){
        String cityName = cityEt.getText().toString();
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(cityEt.getText().toString(), "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q="+ encodedCityName +"&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(cityEt.getWindowToken(), 0);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't Find Weather :(", Toast.LENGTH_SHORT).show();
            infoTv.setVisibility(View.INVISIBLE);
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while(data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return result;

            } catch (Exception e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Couldn't Find Weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Info", weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                String message = "";
                for (int i=0; i<arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if(!main.equals("") && !description.equals("")){
                        message += main + ": " + description;
                    }
                }
                if(!message.equals("")){
                    infoTv.setVisibility(View.VISIBLE);
                    infoTv.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(), "Couldn't Find Weather :(", Toast.LENGTH_SHORT).show();
                    infoTv.setVisibility(View.INVISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't Find Weather :(", Toast.LENGTH_SHORT).show();
                infoTv.setVisibility(View.INVISIBLE);
            }

        }
    }
}
