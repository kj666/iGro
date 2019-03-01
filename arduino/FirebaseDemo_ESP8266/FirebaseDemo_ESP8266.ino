

// FirebaseDemo_ESP8266 is a sample that demo the different functions
// of the FirebaseArduino API.

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <Wire.h>
#include "SparkFunBME280.h"

//Wifi and Firebase Configuration
#define FIREBASE_HOST "greenhouseapp-5d7af.firebaseio.com"
#define FIREBASE_AUTH "ANDIRrho71r1E8En8ySBSqXshGo3ykcJbxQqzXQq"
#define WIFI_SSID "KJ"
#define WIFI_PASSWORD "686AA884F6"

BME280 mySensor;

int t = 0;
int poll = 0;

void setup() {
  Serial.begin(9600);

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

  Wire.begin(0,2);
  Wire.setClock(100000);
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
 
  //BME Sensor
  //Temperature
  t++;
  
  //Send temperature data to firebase
  Firebase.setFloat("data/"+String(t)+"/temperature", mySensor.readTempC());
  if (Firebase.failed()) {
      Serial.print("temperature/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  
  //Send humidity data to firebase
  Firebase.setFloat("data/"+String(t)+"/humidity", mySensor.readFloatHumidity());
  if (Firebase.failed()) {
      Serial.print("humidity/ failed:");
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
  Serial.print("Humidity: ");
  Serial.print(mySensor.readFloatHumidity(), 0);

  Serial.print(" Pressure: ");
  Serial.print(mySensor.readFloatPressure(), 0);

  Serial.print(" Alt: ");
  //Serial.print(mySensor.readFloatAltitudeMeters(), 1);
  Serial.print(mySensor.readFloatAltitudeFeet(), 1);

  Serial.print(" Temp: ");
  //Serial.print(mySensor.readTempC(), 2);
  Serial.print(mySensor.readTempF(), 2);
  Serial.print("poll: ");
  Serial.print(poll);
  Serial.println();

  delay(poll);
  
}
