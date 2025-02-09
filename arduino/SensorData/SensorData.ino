

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
int tempT = 0;
int soilT = 0;
int humT = 0;
int uvT = 0;

int poll = 2000;
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
  tempT = Firebase.getInt(greenHouseID+"SensorConfig/TempLastID");
  tempT++;
  delay(100);
  soilT = Firebase.getInt(greenHouseID+"SensorConfig/SoilLastID");
  soilT++;
  delay(100);
  humT = Firebase.getInt(greenHouseID+"SensorConfig/HumLastID");
  humT++;
  delay(100);
  uvT = Firebase.getInt(greenHouseID+"SensorConfig/UVLastID");
  uvT++;

  
  //TemperatureC
  Firebase.setFloat(greenHouseID+"Data/TemperatureSensor1/"+String(tempT)+"/value", mySensor.readTempC());
  Serial.print(" temp: "+ String(mySensor.readTempC()));
  if (Firebase.failed()) {
      Serial.println("temperatureC/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(10);
  //Humidity
  Firebase.setFloat(greenHouseID+"Data/HumiditySensor1/"+String(humT)+"/value", mySensor.readFloatHumidity());
  Serial.print(" hum: "+ String(mySensor.readFloatHumidity()));
  if (Firebase.failed()) {
      Serial.println("humidity/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(10);
  //UV index
  float UVindex = uv.readUV();
  UVindex /= 100.0;  
  Firebase.setFloat(greenHouseID+"Data/UVSensor1/"+String(uvT)+"/value",UVindex);
  Serial.println(" UVindex: "+ String(UVindex));
  if (Firebase.failed()) {
      Serial.println("uv/ failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(10);
  //Soil Moisture
  soil_value = readSoil();
  Firebase.setFloat(greenHouseID+"Data/SoilSensor1/"+String(soilT)+"/value",soil_value);
  Serial.println(" soil: "+ String(soil_value));
  if (Firebase.failed()) {
      Serial.print("soil/ failed:");
      Serial.println(Firebase.error()); 
      return;
  }
  
 //keep track of time ID
  Firebase.setInt(greenHouseID+"SensorConfig/TempLastID",tempT);
  if (Firebase.failed()) {
      Serial.print("lastId failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(10);
  Firebase.setInt(greenHouseID+"SensorConfig/HumLastID",humT);
  if (Firebase.failed()) {
      Serial.print("lastId failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(10);
  Firebase.setInt(greenHouseID+"SensorConfig/UVLastID",uvT);
  if (Firebase.failed()) {
      Serial.print("lastId failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(10);
  Firebase.setInt(greenHouseID+"SensorConfig/SoilLastID",soilT);
  if (Firebase.failed()) {
      Serial.print("lastId failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(10);


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
