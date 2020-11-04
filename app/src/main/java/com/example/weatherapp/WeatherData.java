package com.example.weatherapp;

public class WeatherData {
    private String feelsLike;
    private String temp;
    private String cityName;
    private String tempMax;
    private String tempMin;
    private String lat;
    private String lon;

    public WeatherData(){};

    public void setFeelsLike(String fl){
        this.feelsLike = fl;
    }

    public void setTemp(String t){
        this.temp = t;
    }

    public void setCityName(String c){
        this.cityName = c;
    }

    public void setTempMax(String t){
        this.tempMax = t;
    }

    public void setTempMin(String t){
        this.tempMin = t;
    }

    public void setLat(String l){
        this.lat = l;
    }

    public void setLon(String l){
        this.lon = l;
    }

    public String getTemp(){
        return this.temp;
    }

    public String getLat(){
        return this.lat;
    }

    public String getLon(){
        return this.lon;
    }

    public String getFeelsLike(){
        return this.feelsLike;
    }

    public String getCityName(){
        return this.cityName;
    }

    public String getTempMax(){
        return this.tempMax;
    }

    public String getTempMin(){
        return this.tempMin;
    }

}
