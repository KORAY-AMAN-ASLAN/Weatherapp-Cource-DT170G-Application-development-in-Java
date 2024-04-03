
package se.miun.dt170g;
/**
 * @Authors: Lucas Persson, Jesper Nilsson
 *
 * @Description: The weather class represent the current state of the weather including temperature,
 * wind speed, wind direction, cloudiness, and minimum and maximum expected rain together with a symbol to
 * a corresponding image for the weather state
 */

import android.util.Pair;

public class Weather {
    private double temperature;
    private double windSpeed;
    private String windDirection;
    private double cloudiness;
    private Pair<Double, Double> precipitation;

    public String getSymbol() {
        return symbol;
    }

    private String symbol;

    private String weatherImage;

    public Weather(double temperature, double windSpeed, String windDirection, double cloudiness, Pair<Double, Double> precipitation) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.cloudiness = cloudiness;
        this.precipitation = precipitation;
        chooseWeatherImage();
    }

    public Weather() {

    }

    private void chooseWeatherImage(){
        /**
         * 0-20% no cloud
         * 20-60 medium cloud
         * 60-100 heavy cloudy
         *
         * < 0.5 no rain
         * 0.5 - 2.5 light rain
         * 2.5 - 7.5 medium rain
         * > 7.5 heavy rain
         */
        double avgRain = (precipitation.first + precipitation.second) /2;
        String filename;


        if (cloudiness < 20) {
            filename = "noCloud";
        }
        else if(cloudiness >= 20 && cloudiness < 60) {
            filename = "mediumCloud";
        }
        else {
            filename = "heavyCloud";
        }

        if(avgRain <= 0.5) {
            filename += "noRain";

        } else if(avgRain <= 2.5) {
            filename += "lightRain";

        } else if(avgRain <= 7.5) {
            filename += "mediumRain";

        } else {
            filename += "heavyRain";
        }

        this.weatherImage = filename  + ".png";
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public double getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(double cloudiness) {
        this.cloudiness = cloudiness;
    }

    public Pair<Double, Double> getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Pair<Double, Double> precipitation) {
        this.precipitation = precipitation;
    }

    public void setSymbol(String name) {
        this.symbol = name;
    }
}
