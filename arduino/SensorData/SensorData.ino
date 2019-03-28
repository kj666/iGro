

// FirebaseDemo_ESP8266 is a sample that demo the different functions
// of the FirebaseArduino API.

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <Wire.h>
#include "Si1145.h"
#include "SparkFunBME280.h"

//Wifi and Firebase Configuration
#define FIREBASE_HOST "greenhouseapp-5d7af.firebaseio.com"
#define FIREBASE_AUTH "ANDIRrho71r1E8En8ySBSqXshGo3ykcJbxQqzXQq"
#define WIFI_SSID "Karthi"
#define WIFI_PASSWORD "karthi666"

BME280 mySensor;
Si1145 uv = Si1145();

double soil_value;
int t = 0;
int poll = 0;
String greenHouseID;

void setup() {
  Serial.begin(9600);
  
  pinMode(A0, INPUT);
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
  
  //Check temp sensor
  Serial.println("Reading basic values from BME280");

  if (mySensor.beginI2C() == false) //Begin communication over I2C
  {
    Serial.println("The sensor did not respond. Please check wiring.");
    while(1); 
  }

  //Check UV sensor
  if (! uv.begin()) {
    Serial.println("Didn't find Si1145 !\r\n");
    while (1);
  }

  Serial.println("Si1145 Init success !\r\n");

  greenHouseID = Firebase.getString("mcuGreenhouseIDSensor")+"/";
}


void loop() {

  poll = Firebase.getInt(greenHouseID+"SensorConfig/poll");
  delay(100);
  t = Firebase.getInt(greenHouseID+"SensorConfig/lastID");
  t++;

  
  //TemperatureC
  Firebase.setFloat(greenHouseID+"Data/"+String(t)+"/temperatureC", mySensor.readTempC());
  Serial.print(" temp: "+ String(mySensor.readTempC()));
  if (Firebase.failed()) {
      Serial.println("temperatureC/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  //TemperatureF
  Firebase.setFloat(greenHouseID+"Data/"+String(t)+"/temperatureF", mySensor.readTempF());
  if (Firebase.failed()) {
      Serial.println("temperatureF/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  
  //Humidity
  Firebase.setFloat(greenHouseID+"Data/"+String(t)+"/humidity", mySensor.readFloatHumidity());
  Serial.print(" hum: "+ String(mySensor.readFloatHumidity()));
  if (Firebase.failed()) {
      Serial.println("humidity/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  //UV index
  float UVindex = uv.readUV();
  UVindex /= 100.0;  
  Firebase.setFloat(greenHouseID+"Data/"+String(t)+"/uvIndex",UVindex);
  Serial.println(" UVindex: "+ String(UVindex));
  if (Firebase.failed()) {
      Serial.println("uv/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  //Visible light
  Firebase.setFloat(greenHouseID+"Data/"+String(t)+"/visibleLight",uv.readVisible());
  Serial.println(" VIS: "+ String(uv.readVisible()));
  if (Firebase.failed()) {
      Serial.println("uv/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  //IR visble
  Firebase.setFloat(greenHouseID+"Data/"+String(t)+"/IR",uv.readIR());
  Serial.println(" IR: "+ String(uv.readIR()));
  if (Firebase.failed()) {
      Serial.println("uv/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  //Soil Moisture
  soil_value = readSoil();
  Firebase.setFloat(greenHouseID+"Data/"+String(t)+"/soil",soil_value);
  Serial.println(" soil: "+ String(soil_value));
  if (Firebase.failed()) {
      Serial.print("soil/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  
 //keep track of time ID
  Firebase.setInt(greenHouseID+"SensorConfig/lastID",t);
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
  digitalWrite(D3, LOW);
  double soil = map(val, 0, 880, 0, 100);
 
  return soil;
}
