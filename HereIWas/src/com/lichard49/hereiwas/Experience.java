package com.lichard49.hereiwas;

public class Experience {
	private String name;
	private String pic;
	private String sound;
	private String weather;
	private String time;
	private float lat;
	private float lon;
	
	public Experience(String n){
		name = n;
	}
	public void setLat(float n){
		lat = n;
	}
	public void setLon(float n){
		lon = n;
	}
	public void setTime(String n){
		time = n;
	}
	public void setName(String n){
		name = n;
	}
	public void setPic(String p){
		pic = p;
	}
	public void setSound(String p){
		sound = p;
	}
	public void setWeather(String p){
		weather = p;
	}
	
	public String getWeather(){
		return weather;
	}
	public String getSound(){
		return sound;
	}
	public String getPic(){
		return pic;
	}
	public String getName(){
		return name;
	}
	public String getTime(){
		return time;
	}
	public float getLat(){
		return lat;
	}
	public float getLon(){
		return lon;
	}
	
}
