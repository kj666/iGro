

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

int temp = D0;
int hum = D2;
int uv = D4;
int soil = D6;

int tempCtrl;
int humidCtrl;
int lightCtrl;
int soilCtrl;

void setup() {
  Serial.begin(9600);
  pinMode(D0, OUTPUT);
  pinMode(hum, OUTPUT);
  pinMode(uv, OUTPUT);
  pinMode(soil, OUTPUT);

  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  
  Serial.println("connected: ");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

}


void loop() {

  tempCtrl = Firebase.getBool("Appliances/HeaterCtrl");
  Serial.print("temp: "+ String(tempCtrl));
  delay(100);
  humidCtrl = Firebase.getBool("Appliances/HumidCtrl");
  Serial.print(" hum: "+ String(humidCtrl));
  delay(100);
  lightCtrl = Firebase.getBool("Appliances/LightCtrl");
  Serial.print(" uv: "+ String(lightCtrl));
  delay(100);
  soilCtrl = Firebase.getBool("Appliances/SoilCtrl");
  Serial.println(" soil: "+ String(soilCtrl));
  delay(100);

  if(tempCtrl == 1){
    digitalWrite(D0, HIGH);
    delay(1000);
    Serial.println("temp ON");
  }
  else{
    digitalWrite(temp, LOW);
    Serial.println("temp OFF");
  }

  if(humidCtrl == 1){
    digitalWrite(hum, HIGH);
    Serial.print(" humid ON");
  }
  else{
    digitalWrite(hum, LOW);
    Serial.print(" humid OFF");
  }

  if(lightCtrl == 1){
    digitalWrite(uv, HIGH);
    Serial.print(" UV ON");
  }
  else{
    digitalWrite(uv, LOW);
    Serial.print(" UV OFF");
  }

  if(soilCtrl == 1){
    digitalWrite(soil, HIGH);
    Serial.print(" soil ON");
  }
  else{
    digitalWrite(soil, LOW);
    Serial.print(" soil OFF");
  }

}
