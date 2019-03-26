

// FirebaseDemo_ESP8266 is a sample that demo the different functions
// of the FirebaseArduino API.

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <Wire.h>
#include "SparkFunBME280.h"

//Wifi and Firebase Configuration
#define FIREBASE_HOST "greenhouseapp-5d7af.firebaseio.com"
#define FIREBASE_AUTH "ANDIRrho71r1E8En8ySBSqXshGo3ykcJbxQqzXQq"
#define WIFI_SSID "Karthi"
#define WIFI_PASSWORD "karthi666"

double uv_value;
double soil_value;

BME280 mySensor;

int t = 0;
int poll = 0;

void setup() {
  Serial.begin(9600);
  pinMode(D0, OUTPUT);
  pinMode(D3, OUTPUT);

  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
  //Read from sensor
  Serial.println("Reading basic values from BME280");

//Use only for esp
  if (mySensor.beginI2C() == false) //Begin communication over I2C
  {
    Serial.println("The sensor did not respond. Please check wiring.");
    while(1); //Freeze
  }
}


void loop() {

  
  poll = Firebase.getInt("config/poll");
  delay(100);
  t = Firebase.getInt("config/lastID");
  t++;

  
  //TemperatureC
  Firebase.setFloat("data/"+String(t)+"/temperatureC", mySensor.readTempC());
  Serial.print(" temp: "+ String(mySensor.readTempC()));
  if (Firebase.failed()) {
      Serial.println("temperatureC/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  //TemperatureF
  Firebase.setFloat("data/"+String(t)+"/temperatureF", mySensor.readTempF());
  if (Firebase.failed()) {
      Serial.println("temperatureF/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  
  //Humidity
  Firebase.setFloat("data/"+String(t)+"/humidity", mySensor.readFloatHumidity());
  Serial.print(" hum: "+ String(mySensor.readFloatHumidity()));
  if (Firebase.failed()) {
      Serial.println("humidity/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  //UV
  uv_value = readUV();
  Firebase.setFloat("data/"+String(t)+"/uv",uv_value);
  Serial.print(" UV: "+ String(uv_value));
  if (Firebase.failed()) {
      Serial.println("uv/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  //UV
  double uv_nm = readUVnm(uv_value);
  Firebase.setFloat("data/"+String(t)+"/uvnm",uv_nm);
  Serial.print(" UV: "+ String(uv_nm));
  if (Firebase.failed()) {
      Serial.println("uv/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  //Soil Moisture
  soil_value = readSoil();
  Firebase.setFloat("data/"+String(t)+"/soil",soil_value);
  Serial.println(" soil: "+ String(soil_value));
  if (Firebase.failed()) {
      Serial.print("soil/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  
 //keep track of time ID
  Firebase.setInt("config/lastID",t);
  if (Firebase.failed()) {
      Serial.print("lastId failed:");
      Serial.println(Firebase.error());  
      return;
  }

  Serial.println("poll: " + poll);

  delay(poll);
  
}
//
double readSoil(){
  digitalWrite(D3, HIGH);
  delay(10);
  double val = analogRead(A0);
  Serial.print("soil/" +String(val));
  digitalWrite(D3, LOW);
  double soil;
  if(val <150){
    soil = 0;
    Serial.print("soil0/" +String(val));
  }
  else if(val > 620){
    soil = 100;
    Serial.print("soil100/" +String(val));
  }
  else{
    soil = ((val-150)/500)*100;
    Serial.print("soilCal/" +String(val) + String(((val-150)/500)*100));
  }
  
  return soil;
}

double readUV(){
  digitalWrite(D0, HIGH);
  delay(10);
  double val = analogRead(A0);
  digitalWrite(D0, LOW);
  return val;
}

double readUVnm(double value){
  double val = (value*0.076)/100;
  int nm;
  if(val <= 0.01){
    nm = 240;
  }
  else if(val > 0.01 && val <= 0.12){
    nm=(val+0.32)/0.001375;
  }
  else if (val >0.12 && val <0.15){
    nm= (val+0.2)/0.001;
  }
  else{
    nm = 360;
  }
  return nm;
}
