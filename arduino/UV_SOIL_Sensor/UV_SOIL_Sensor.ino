int uv_ain=A0;
int ad_value;

int val = 0; //value for storintg moisture level
int soilPin=A1; //analog input
int soilPower = 7; //Power
void setup()
{
  pinMode(uv_ain,INPUT); //input for UV sensor
  pinMode(soilPower, OUTPUT); //input for 
  digitalWrite(soilPower, LOW);
  Serial.begin(9600);
}
void loop()
{
  Serial.print("Soil Moisture = ");
  Serial.println(readSoil());
  ad_value=analogRead(uv_ain);
  Serial.println(ad_value);
  if(ad_value>20)
  {
    Serial.println("UV up the standard");
  }
  else
  {
    Serial.println("UV down the standard");
  }
  delay(500);
}

int readSoil(){
  digitalWrite(soilPower, HIGH);
  delay(10);
  val = analogRead(soilPin);
  digitalWrite(soilPower, LOW);
  return val;
}
