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
char packetBuffer[16];  //buffer to hold incoming packet,
char  ReplyBuffer[] = "acknowledged";       // a string to send back

int packet;
int delayCount = 100;
char messageToServer[6]; 
int checkIfSent = -1;
float motor1Clock;
float motor2Clock;
bool hasPressed = false;

// An EthernetUDP instance to let us send and receive packets over UDP
EthernetUDP Udp;
IPAddress serverIp(192, 168, 0, 12);
void setup() {
  // start the Ethernet and UDP:
  Ethernet.begin(mac, ip);
  Udp.begin(localPort);

  Serial.begin(9600);
  //sätter pinnarna på shielden, dem som används till outputs.
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(10, OUTPUT);
}
//sätter dem olika pinnarn 
void turnOnLight(int packet){
  
  if( (packet) & (1<<0)){
    digitalWrite(2, HIGH); 
    messageToServer[5] = '1';
  }
  else{
    digitalWrite(2, LOW);
    messageToServer[5] = '0'; 
  }
  if( (packet) & (1<<1)){
    digitalWrite(3, HIGH); 
    messageToServer[4] = '1';
  }
  else{
    digitalWrite(3, LOW); 
    messageToServer[4] = '0';
  }
  if( (packet) & (1<<2)){
    digitalWrite(4, HIGH); 
    messageToServer[3] = '1';
  }
  else{
    digitalWrite(4, LOW); 
    messageToServer[3] = '0';
  }   
  if( (packet) & (1<<3)){
    digitalWrite(5, HIGH); 
    messageToServer[2] = '1';
  }
  else{
    digitalWrite(5, LOW);
    messageToServer[2] = '0'; 
  }
  if( (packet) & (1<<4)){
    digitalWrite(6, HIGH);
    messageToServer[1] = '1'; 
  }
  else{
    digitalWrite(6, LOW); 
    messageToServer[1] = '0';
  }
  if( (packet) & (1<<5)){
    digitalWrite(7, HIGH); 
    messageToServer[0] = '1';
  }
  else{
    digitalWrite(7, LOW); 
    messageToServer[0] = '0';
  }
}
void motorOn(int pin){
  digitalWrite(pin, HIGH);
  delay(20);

}
void motorOff(int pin){
  digitalWrite(pin, LOW);
  delay(20); 
}

void loop() {
  // if there's data available, read a packet
  int packetSize = Udp.parsePacket();
  Udp.read(packetBuffer, 16);
  //läser av A1 pinnen /knappen
  int sensorValue = analogRead(A1);
  packet = atoi(packetBuffer);
  int motorInt;
  digitalWrite(10, LOW);
  if(packet >= 0 && packetBuffer[0] != '\0')
    turnOnLight(packet);
 // Serial.println(sensorValue);    
  
 // sätter på motorerna en kort stund så att inte den rör sig för länge
  if(packet == -3){
    motor1Clock = 1;
  }
  else if(packet == -4){
    motor2Clock = 1;
  }
  if(motor1Clock > 0) motorOn(9);
  if(motor2Clock > 0) motorOn(10);
  if(motor1Clock > 0){
    motor1Clock -= 1;
  }
  if(motor1Clock <= 0){
     motorOff(9);
  }
  if(motor2Clock > 0){
    motor2Clock -= 1;
  }
  if(motor2Clock <= 0){
     motorOff(10);
  }
  //kollar om innehållet i paketet har ändrats, om den har det så skicka tillbaka ett medelande till servern om inte gör inget.
  if(checkIfSent != packet){
    
    Serial.print("PacketBuffer: ");
    Serial.println(packetBuffer);
    Serial.println("To Server: ");
    Serial.println(messageToServer);
    checkIfSent = packet;
  }
  // kollar om knappen är nedtryckt, om den är så skicka en -2 udp paket till servern. Kollar också så att inte kanppen spammar servern utan skickar bara e gång.
  if(sensorValue >= 1023){
    sensorValue = 1;
    Udp.beginPacket(serverIp, localPort);
    Udp.write("-2");
    Udp.endPacket();
  }
  else{
    sensorValue = 0;
  }
  if(sensorValue == 1 && !hasPressed){

    Serial.println("haj ");
    hasPressed = true;
  }

  if(sensorValue == 0 && hasPressed) {
    hasPressed = false;
  }
  for(int i = 0; i < 16; i++) {
    packetBuffer[i] = '\0';
  }
  delay(delayCount);
}


