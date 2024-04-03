package se.miun.dt170g.Fetch_JSON;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.miun.dt170g.R;

public class MainActivity extends AppCompatActivity {
    //precipitation_amount_max		mm	maximum likely precipitation for period
    //precipitation_amount_min	    mm	minimum likely precipitation for period

    //wind_from_direction	        degrees	direction the wind is coming from (0° is north, 90° east, etc.)
    //wind_speed		            m/s	wind speed at 10m above the ground (10 min average)
    //air_temperature		        celsius	air temperature at 2m above the ground
    //cloud_area_fraction		    %	total cloud cover for all heights



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        fetchData();

    }

    private void fetchData() {
        final String WS_HOST = "https://api.met.no/";
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0";


        //Creating a HttpClient with a user agent in the header.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .header("User-Agent", USER_AGENT)
                        .build()))
                .build();

        //Creating Retrofit obj.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WS_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        //Letting Retrofit know what api methods it has access to.
        FetchWeather fetchWeather = retrofit.create(FetchWeather.class);

        //Fetching using the HTTP @GET method.
        Call<WeatherData> call = fetchWeather.getWeatherData("60.10","9.58");
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful()) {
                    WeatherData weatherData = response.body();

                    List<TimeSeries> timeList = weatherData.getProperties().getTimeseries();
                    for(int i=0; i<timeList.size(); i++){
                        System.out.println("Time: " + timeList.get(i).getTime() + " | Data: "
                                + timeList.get(i).getData().getInstant().getDetails().getAir_temperature() + " "
                                + timeList.get(i).getData().getInstant().getDetails().getWind_speed()  + " "
                                + timeList.get(i).getData().getInstant().getDetails().getWind_from_direction()  + " "
                                + timeList.get(i).getData().getInstant().getDetails().getCloud_area_fraction()  + " "
                                + timeList.get(i).getData().getNext_1_hours().getPrecipitation().getPrecipitation_amount() + " "
                                + timeList.get(i).getData().getNext_1_hours().getSummary().getSymbol_code());
                    }

                } else {
                    // Handle error
                    Log.d("GG", "Error bdy: " + response.errorBody());
                    Log.d("GG", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                // Handle failure
                Log.d("GG",  "Network request failed: " + t.getMessage());
            }
        });
    }
}