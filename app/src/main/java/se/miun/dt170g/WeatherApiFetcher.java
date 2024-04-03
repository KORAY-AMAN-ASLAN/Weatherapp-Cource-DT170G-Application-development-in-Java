package se.miun.dt170g;
/**
 * @author Jesper, Lucas
 * @modified 2024-01-22
 * @description this class fetches and creates a weather object from an api, communicates using a handler
 */

import android.os.Handler;
import android.util.Pair;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class WeatherApiFetcher {
    private Handler handler;
    private String url;
    private WeatherListener weatherListener;

    public WeatherApiFetcher(Handler handler, String url, WeatherListener listener){
        this.handler = handler;
        this.url = url;
        this.weatherListener = listener;
    }
    public static interface WeatherListener {
        void onWeatherFetched(Weather weather);
        void onWeatherFetchFailed(Exception e);
    }

    public void fetchAndParseWeatherData() {
        String urlString = this.url;
        new Thread(() -> {
            HttpURLConnection connection = null;
            InputStream stream = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0"); // Set User-Agent

                int responseCode = connection.getResponseCode();
                Log.d("responseCode", "" +  responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    stream = connection.getInputStream();
                    Weather weather = parseXML(stream);


                    Log.d("WeatherData", "Wind Direction: " + weather.getWindDirection());
                    Log.d("WeatherData", "temprature: " + weather.getTemperature());
                    Log.d("WeatherData", "Wind spped: " + weather.getWindSpeed());
                    Log.d("WeatherData", "cloudiness: " + weather.getCloudiness());
                    Log.d("WeatherData", "rain min: " + weather.getPrecipitation().first);
                    Log.d("WeatherData", "rain max: " + weather.getPrecipitation().second);
                    Log.d("WeatherData", "symbol: " + weather.getSymbol());
                    // Update UI with weather object (on the main thread)
                    handler.post(() -> {
                        weatherListener.onWeatherFetched(weather);

                    });
                } else {
                    Log.e("WeatherData", "HTTP error response: " + responseCode);
                }
            } catch (Exception e) {
                handler.post(() -> {
                    weatherListener.onWeatherFetchFailed(e);
                });
                e.printStackTrace();
                // Handle errors
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();

    }


    private Weather parseXML(InputStream stream) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(stream, null);

        Weather weather = new Weather();

        boolean temperatureSet = false, windDirectionSet = false, windSpeedSet = false,
                cloudinessSet = false, precipitationSet = false, symbolSet = false;


        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            if (eventType == XmlPullParser.START_TAG) {
                switch (tagName) {
                    case "temperature":
                        if (!temperatureSet) {
                            weather.setTemperature(Double.parseDouble(parser.getAttributeValue(null, "value")));
                            temperatureSet = true;
                        }
                        break;
                    case "windDirection":
                        if (!windDirectionSet) {
                            weather.setWindDirection(parser.getAttributeValue(null, "name"));
                            windDirectionSet = true;
                        }
                        break;
                    case "windSpeed":
                        if (!windSpeedSet) {
                            weather.setWindSpeed(Double.parseDouble(parser.getAttributeValue(null, "mps")));
                            windSpeedSet = true;
                        }
                        break;
                    case "cloudiness":
                        if (!cloudinessSet) {
                            weather.setCloudiness(Double.parseDouble(parser.getAttributeValue(null, "percent")));
                            cloudinessSet = true;
                        }
                        break;
                    case "precipitation":
                        if (!precipitationSet) {
                            double minPrecipitationValue = Double.parseDouble(parser.getAttributeValue(null, "minvalue"));
                            double maxPrecipitationValue = Double.parseDouble(parser.getAttributeValue(null, "maxvalue"));
                            weather.setPrecipitation(new Pair<>(minPrecipitationValue, maxPrecipitationValue));
                            precipitationSet = true;
                        }
                        break;
                    case "symbol":
                        if (!symbolSet) {
                            weather.setSymbol(parser.getAttributeValue(null, "id"));
                            symbolSet = true;
                        }
                        break;
                }
            }

            eventType = parser.next();
        }

        return weather;
    }



}

