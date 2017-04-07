/*
 UDPSendReceiveString:
 This sketch receives UDP message strings, prints them to the serial port
 and sends an "acknowledge" string back to the sender

 A Processing sketch is included at the end of file that can be used to send
 and received messages for testing with a computer.

 created 21 Aug 2010
 by Michael Margolis

 This code is in the public domain.
 */


#include <SPI.h>         // needed for Arduino versions later than 0018
#include <Ethernet.h>
#include <EthernetUdp.h>         // UDP library from: bjoern@cs.stanford.edu 12/30/2008


// Enter a MAC address and IP address for your controller below.
// The IP address will be dependent on your local network:
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED
};
IPAddress ip(192, 168, 0, 2);

unsigned int localPort = 4444;      // local port to listen on

// buffers for receiving and sending data
char packetBuffer[UDP_TX_PACKET_MAX_SIZE];  //buffer to hold incoming packet,
char  ReplyBuffer[] = "acknowledged";       // a string to send back

char test = '3';
int count = 0;
boolean sent = false;
char last = '0';
int testIntD1 = 0b001;
int testIntD2 = 0b110;

// An EthernetUDP instance to let us send and receive packets over UDP
EthernetUDP Udp;
IPAddress l(192, 168, 0, 12);
void setup() {
  // start the Ethernet and UDP:
  Ethernet.begin(mac, ip);
  Udp.begin(localPort);

  Serial.begin(9600);

  Serial.println("test");
  
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);

}

void turnOnLight(int numD1, int numD2){
    if( (numD1) & (1<<0)){
      digitalWrite(2, HIGH); 
    }
    else{
      digitalWrite(2, LOW); 
    }
    if( (numD1) & (1<<1)){
      digitalWrite(3, HIGH); 
    }
    else{
      digitalWrite(3, LOW); 
    }
    if( (numD1) & (1<<2)){
      digitalWrite(4, HIGH); 
    }
    else{
      digitalWrite(4, LOW); 
    }
    
    
    if( (numD2) & (1<<0)){
      digitalWrite(5, HIGH); 
    }
    else{
      digitalWrite(5, LOW); 
    }
    if( (numD2) & (1<<1)){
      digitalWrite(6, HIGH); 
    }
    else{
      digitalWrite(6, LOW); 
    }
    if( (numD2) & (1<<2)){
      digitalWrite(7, HIGH); 
    }
    else{
      digitalWrite(7, LOW); 
    }  
  }

void loop() {
  // if there's data available, read a packet
 int packetSize = Udp.parsePacket();

 

   Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
   Serial.println("Contents:");
   Serial.println(packetBuffer);
  int d = 50;
  Serial.print("content start: ");
  Serial.println(packetBuffer[0]);
 

  last = test;

  
  test = packetBuffer[0];
  turnOnLight(testIntD1,testIntD2);

 /* 
  if(test == '0') {
    digitalWrite(2, LOW); 
     digitalWrite(3, LOW); 
      digitalWrite(4, LOW);
    if(sent == false){
        Udp.beginPacket(l, localPort);
   //     Udp.write(count);
        Udp.endPacket();
       count ++;
       sent = true;
   }
  }

  if(test == '1') {
    digitalWrite(2, HIGH); 
     digitalWrite(3, LOW); 
      digitalWrite(4, LOW);
          if(sent == false){
        Udp.beginPacket(l, localPort);
      //  Udp.write(count);
        Udp.endPacket();
       count ++;
       sent = true;
   }
  }

  if(test == '2') {
    digitalWrite(2, LOW); 
     digitalWrite(3, HIGH); 
      digitalWrite(4, LOW);
          if(sent == false){
        Udp.beginPacket(l, localPort);
   //     Udp.write(count);
        Udp.endPacket();
       count ++;
       sent = true;
   }
  }
  
  if(test == '3') {
    digitalWrite(2, LOW); 
     digitalWrite(3, LOW); 
      digitalWrite(4, HIGH);
          if(sent == false){
        Udp.beginPacket(l, localPort);
    //    Udp.write(count);
        Udp.endPacket();
       count ++;
       sent = true;
   }
  */
   //if(last != test){
    //sent = false;
   //}
  //}
  
  //Serial.println("counter: ");
 // Serial.println(count);
  //Serial.println("Contents:");
  //Serial.println(packetBuffer);
  delay(d);
}


