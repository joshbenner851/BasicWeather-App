package joshbenner.stormy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
    //butterknife way of declaring variables
    @InjectView(R.id.timeLbl) TextView mTimeLbl;
    @InjectView(R.id.tempLbl) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryTextView) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageview;
    @InjectView(R.id.progressBar) ImageView mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRefreshImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForecast();
            }
        });
        getForecast();
       Log.d(TAG,"Main UI code is running!");

    }

    private void getForecast() {
        String apiKey = "c6e30450829a5bceee827440de85af0a";
        double latitude = 37.8267;
        double longitude = -122.423;
        //hard coding the url
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/"
                + latitude + "," + longitude;
        //if there's a network available
        if(isNetworkAvaible()) {
            ToggleRefresh();
            OkHttpClient client = new OkHttpClient();
            //making request to the api
            Request request = new Request.Builder().url(forecastUrl).build();
            Call call = client.newCall(request);
            //background thread
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            Log.v(TAG, jsonData);
                            //gets the weather data and sets them to weather object
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {
                            //tells user what error is
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);

                    }
                    catch (JSONException e){
                        Log.e(TAG, "Exception caught: ", e);

                    }

                }
            });
        }
    }

    private void ToggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageview.setVisibility(View.INVISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageview.setVisibility(View.VISIBLE);
        }

    }

    private void updateDisplay() {
        //setting all the labels with their respective text
        mTemperatureLabel.setText(mCurrentWeather.getTemp() + "");
        mTimeLbl.setText("At" + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumid() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecip() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());

        //declaring and setting the weather icon
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG,"From Timezone " + timezone);
        //gets the currently JSON object which is an object of forecast
        //sets current weather attributes
        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumid(currently.getDouble("humidity"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecip(currently.getDouble("precipitation"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemp(currently.getInt("temperature"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTimeZone(timezone);

        return currentWeather;

    }

    private boolean isNetworkAvaible() {
        //declares network objects and checks if it's available
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        Boolean isAvailable = false;
        if(info!=null && info.isConnected()){
            isAvailable = true;
        }
        else{
            Toast.makeText(this,getString(R.string.network_unavailable),Toast.LENGTH_LONG).show();
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }
}
