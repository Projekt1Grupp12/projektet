/*
 UDPSendReceiveString:
Detta programmet funkar så att den får in en packet fråmn servern och sätter 
ström på dem portarna beroende på paketets nummer.

 created 10 April 2017
 by Carl Zetterberg och Tom Leonardsson

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

int packet;
int delayCount = 50;

// An EthernetUDP instance to let us send and receive packets over UDP
EthernetUDP Udp;
IPAddress serverIp(192, 168, 0, 12);
void setup() {
  // start the Ethernet and UDP:
  Ethernet.begin(mac, ip);
  Udp.begin(localPort);

  Serial.begin(9600);
  //sätter pinnarna på shielden till outputs.
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
}
//sätter dem olika pinnarn 
void turnOnLight(int packet){
  if( (packet) & (1<<0)){
    digitalWrite(2, HIGH); 
  }
  else{
    digitalWrite(2, LOW); 
  }
  if( (packet) & (1<<1)){
    digitalWrite(3, HIGH); 
  }
  else{
    digitalWrite(3, LOW); 
  }
  if( (packet) & (1<<2)){
    digitalWrite(4, HIGH); 
  }
  else{
    digitalWrite(4, LOW); 
  }   
  if( (packet) & (1<<3)){
    digitalWrite(5, HIGH); 
  }
  else{
    digitalWrite(5, LOW); 
  }
  if( (packet) & (1<<4)){
    digitalWrite(6, HIGH); 
  }
  else{
    digitalWrite(6, LOW); 
  }
  if( (packet) & (1<<5)){
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
 
  Serial.print("PacketBuffer: ");
  Serial.println(packetBuffer);

  packet = atoi(packetBuffer);
  turnOnLight(packet);  

  delay(delayCount);
}


