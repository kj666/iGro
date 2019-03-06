

// FirebaseDemo_ESP8266 is a sample that demo the different functions
// of the FirebaseArduino API.

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <Wire.h>


//Wifi and Firebase Configuration
#define FIREBASE_HOST "greenhouseapp-5d7af.firebaseio.com"
#define FIREBASE_AUTH "ANDIRrho71r1E8En8ySBSqXshGo3ykcJbxQqzXQq"
#define WIFI_SSID "Karthi"
#define WIFI_PASSWORD "karthi666"

int soil_val = 0;
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

//  Wire.begin(2);
}


void loop() {
  
  poll = Firebase.getInt("config/poll");
  delay(100);
  t = Firebase.getInt("config/LastID_soil");
  t++;

  soil_val = analogRead(2);
  Serial.print("Soil: ");
  Serial.print(soil_val);
  Serial.println();
  
  //Send temperature data to firebase
  Firebase.setFloat("data/"+String(t)+"/soil", soil_val);
  if (Firebase.failed()) {
      Serial.print("temperatureF/ failed:");
      Serial.println(Firebase.error());  
      return;
  }

  
 //keep track of time ID
  Firebase.setInt("config/LastID_soil",t);
  if (Firebase.failed()) {
      Serial.print("lastId failed:");
      Serial.println(Firebase.error());  
      return;
  }


  Serial.print("poll: ");
  Serial.print(poll);
  Serial.println();

  delay(poll);
  
}
