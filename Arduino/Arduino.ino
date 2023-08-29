#include <SoftwareSerial.h>

#define BT_RXD 8
#define BT_TXD 7
SoftwareSerial bluetooth(BT_RXD, BT_TXD);

int distance = 0;
int redPin = 9;
int greenPin = 10;
int bluePin = 11;
int incomingByte = 0;

void setup() {
Serial.begin(9600);
bluetooth.begin(9600);
pinMode(10, INPUT);
pinMode(12, OUTPUT);
pinMode(13, INPUT);
pinMode(3, OUTPUT);

}

void loop() {
  if (Serial.available() > 0) 
  {
  incomingByte = Serial.read();
  Serial.print("I received: ");
  Serial.println(incomingByte, DEC);
  }


  int volt = map(analogRead(A0), 0, 1023, 0, 5000);
  distance = (27.61 / (volt - 0.1696)) * 1000;
  Serial.print(distance);
  Serial.println(" cm");
  Serial.println(" ");
  delay(50);
  bluetooth.available();
  

  if(distance < 30)
  {
  setColor(0, 255, 255);
  Serial.write('N');
  delay(1000);
  }

  if(distance > 30)
  {
    setColor(255, 255, 255);
    Serial.write('G');
    
    delay(1000);
  }

  if(bluetooth.read() == 'a')

  {
    if(distance < 30){
    bluetooth.write("Come  vv");
    }

    while(distance < 30)
    {
    setColor(255, 0, 255);
    int volt = map(analogRead(A0), 0, 1023, 0, 5000);
    distance = (27.61 / (volt - 0.1696)) * 1000;
    delay(1000);
    }
    if(distance > 30)
    {
      setColor(255, 255, 255);
      bluetooth.write("Leave vv");
    }
  }

  if(Serial.read() == 'b')
  {
    while(distance > 30)
    {
      setColor(0, 0, 255);
      int volt = map(analogRead(A0), 0, 1023, 0, 5000);
      distance = (27.61 / (volt - 0.1696)) * 1000;
      delay(1000);
    }
    if(distance < 30)
    setColor(0, 255, 255);
    delay(1000);
  }

  


}




////////////////////////////////////////////////////
void setColor(int red, int green, int blue)
{
analogWrite(9, red);
analogWrite(10, green);
analogWrite(11, blue);
}