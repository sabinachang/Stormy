package com.example.sabina.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private CurrentWeather currentWeather ;
    public static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView darkSky = findViewById(R.id.darkSkyAttribution);

        darkSky.setMovementMethod(LinkMovementMethod.getInstance());
        String apiKey = "5e15675bfe12e88dc277ed5340a6bc08";
        double latitue = 37.8267;
        double longitude = -122.4233;
        String forecastURL = "https://api.darksky.net/forecast/"
                + apiKey +"/" + latitue+"," + longitude;

        if(isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastURL).build();
            Call call = client.newCall(request);

            //execute call asynchronously
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG,jsonData );
                        if (response.isSuccessful()) {
                            currentWeather = getCurrentDetail(jsonData);
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO exception caught: ", e);
                    } catch (JSONException e){
                        Log.e(TAG,"JSON exception caught: ", e );
                    }
                }
            });

            Log.d(TAG, "Main UI code is running.");
        }
    }

    private CurrentWeather getCurrentDetail(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);

        String timeZone = forecast.getString("timezone");
        Log.i(TAG,"time zone from Json " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setLocationLabel("Alcatraz Island,california");
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timeZone);

        Log.d(TAG,currentWeather.getFormattedTime());
        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo!= null && networkInfo.isConnected()){
            isAvailable = true;
        } else{
            Toast.makeText(this, R.string.network_unavailable,Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDiaglogueFragment dialogue = new AlertDiaglogueFragment();

        dialogue.show(getSupportFragmentManager(),"Error dialogue");

    }
}
