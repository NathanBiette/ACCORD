/**
 * Contrôle du Zoom 
 */


#define cmdPin 7 
#define lancPin 11
#define recButton 2
int cmdRepeatCount;
int bitDuration = 104; //Duration of one LANC bit in microseconds. 

void setup() {
  pinMode(lancPin, INPUT); //listens to the LANC line
  pinMode(cmdPin, OUTPUT); //writes to the LANC line
  pinMode(recButton, INPUT); //start-stop recording button
  digitalWrite(recButton, HIGH); //turn on an internal pull up resistor 
  digitalWrite(cmdPin, LOW); //set LANC line to +5V
  delay(5000); //Wait for camera to power up completly
  bitDuration = bitDuration - 8; //Writing to the digital port takes about 8 microseconds so only 96 microseconds are left till the end of each bit
  Serial.begin(9600); 
  Serial.write("Debut");
}

void loop() {
    if(Serial.available()>0){   // check if the buffer isn't empty
      char c = Serial.read();
        switch (c) {
          case 'R' :     //record
            REC();
            break;
          case 'D' :     //ZoomDown ZoomWide
            ZoomWide();
            break;
          case 'U' :     // ZoomUp ZoomTele
            ZoomTele();
            break;
          case 'u' :     // FocusNear FocusUp 
            FocusNear();
            break;
          case 'd':      // FocusFar FocusDown
            FocusFar();
            break;
        }
    }
}


void REC() {
  cmdRepeatCount = 0;
  while (cmdRepeatCount < 5) {  //repeat 5 times to make sure the camera accepts the command
      while (pulseIn(lancPin, HIGH) < 5000) {   
                                                //"pulseIn, HIGH" catches any 0V TO +5V TRANSITION and waits until the LANC line goes back to 0V 
                                                //"pulseIn" also returns the pulse duration so we can check if the previous +5V duration was long enough (>5ms) to be the pause before a new 8 byte data packet
                                                //Loop till pulse duration is >5ms
      }
                                                //LOW after long pause means the START bit of Byte 0 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of byte 0 
                                                //"18hex" or “00011000”  tells the camera that there will be a normal command to camera in the next byte
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, LOW);                //Write bit 0. 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 1 
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);               //Write bit 3
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, HIGH);               //Write bit 4
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 5 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 0 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW);
      delayMicroseconds(10);                    //make sure to be in the stop bit before byte 1
      while (digitalRead(lancPin)) { 
                                                //Loop as long as the LANC line is +5V during the stop bit
      }   
                                                //0V after the previous stop bit means the START bit of Byte 1 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of Byte 1
                                                //"33hex" or “00110011” sends the  Record Start/Stop command
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, HIGH);               //Write bit 0 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);               //Write bit 1 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 3
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);               //Write bit 4 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);               //Write bit 5
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 1 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW); 
      cmdRepeatCount++;  //increase repeat count by 1
                                                //Control bytes 0 and 1 are written, now don’t care what happens in Bytes 2 to 7 and just wait for the next start bit after a long pause to send the first two command bytes again.
  }//While cmdRepeatCount < 5
}

void ZoomWide() {
  cmdRepeatCount = 0;
  while (cmdRepeatCount < 5) {  //repeat 5 times to make sure the camera accepts the command
      while (pulseIn(lancPin, HIGH) < 5000) {   
                                                //"pulseIn, HIGH" catches any 0V TO +5V TRANSITION and waits until the LANC line goes back to 0V 
                                                //"pulseIn" also returns the pulse duration so we can check if the previous +5V duration was long enough (>5ms) to be the pause before a new 8 byte data packet
                                                //Loop till pulse duration is >5ms
      }
                                                //LOW after long pause means the START bit of Byte 0 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of byte 0 
                                                //"28hex" or “00101000”  tells the camera that there will be a normal command to camera in the next byte
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, LOW);                //Write bit 0. 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 1 
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);               //Write bit 3
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);               //Write bit 4
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);                //Write bit 5 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 0 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW);
      delayMicroseconds(10);                    //make sure to be in the stop bit before byte 1
      while (digitalRead(lancPin)) { 
                                                //Loop as long as the LANC line is +5V during the stop bit
      }   
                                                //0V after the previous stop bit means the START bit of Byte 1 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of Byte 1
                                                //"1Ehex" or “00011110” sends the  Record Start/Stop command
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, LOW);               //Write bit 0 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);               //Write bit 1 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);                //Write bit 3
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);               //Write bit 4 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);               //Write bit 5
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 1 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW); 
      cmdRepeatCount++;  //increase repeat count by 1
                                                //Control bytes 0 and 1 are written, now don’t care what happens in Bytes 2 to 7 and just wait for the next start bit after a long pause to send the first two command bytes again.
  }//While cmdRepeatCount < 5
}

void ZoomTele() {
  cmdRepeatCount = 0;
  while (cmdRepeatCount < 5) {  //repeat 5 times to make sure the camera accepts the command
      while (pulseIn(lancPin, HIGH) < 5000) {   
                                                //"pulseIn, HIGH" catches any 0V TO +5V TRANSITION and waits until the LANC line goes back to 0V 
                                                //"pulseIn" also returns the pulse duration so we can check if the previous +5V duration was long enough (>5ms) to be the pause before a new 8 byte data packet
                                                //Loop till pulse duration is >5ms
      }
                                                //LOW after long pause means the START bit of Byte 0 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of byte 0 
                                                //"28hex" or “00101000”  tells the camera that there will be a normal command to camera in the next byte
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, LOW);                //Write bit 0. 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 1 
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);               //Write bit 3
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);               //Write bit 4
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);                //Write bit 5 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 0 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW);
      delayMicroseconds(10);                    //make sure to be in the stop bit before byte 1
      while (digitalRead(lancPin)) { 
                                                //Loop as long as the LANC line is +5V during the stop bit
      }   
                                                //0V after the previous stop bit means the START bit of Byte 1 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of Byte 1
                                                //"0Ehex" or “00001110” sends the  Record Start/Stop command
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, LOW);               //Write bit 0 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);               //Write bit 1 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);                //Write bit 3
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);               //Write bit 4 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);               //Write bit 5
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 1 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW); 
      cmdRepeatCount++;  //increase repeat count by 1
                                                //Control bytes 0 and 1 are written, now don’t care what happens in Bytes 2 to 7 and just wait for the next start bit after a long pause to send the first two command bytes again.
  }//While cmdRepeatCount < 5
}

void FocusFar() {
  cmdRepeatCount = 0;
  while (cmdRepeatCount < 5) {  //repeat 5 times to make sure the camera accepts the command
      while (pulseIn(lancPin, HIGH) < 5000) {   
                                                //"pulseIn, HIGH" catches any 0V TO +5V TRANSITION and waits until the LANC line goes back to 0V 
                                                //"pulseIn" also returns the pulse duration so we can check if the previous +5V duration was long enough (>5ms) to be the pause before a new 8 byte data packet
                                                //Loop till pulse duration is >5ms
      }
                                                //LOW after long pause means the START bit of Byte 0 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of byte 0 
                                                //"28hex" or “00101000”  tells the camera that there will be a normal command to camera in the next byte
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, LOW);                //Write bit 0. 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 1 
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);               //Write bit 3
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);               //Write bit 4
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);                //Write bit 5 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 0 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW);
      delayMicroseconds(10);                    //make sure to be in the stop bit before byte 1
      while (digitalRead(lancPin)) { 
                                                //Loop as long as the LANC line is +5V during the stop bit
      }   
                                                //0V after the previous stop bit means the START bit of Byte 1 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of Byte 1
                                                //"45hex" or “01000101” sends the  Record Start/Stop command
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, HIGH);               //Write bit 0 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);               //Write bit 1 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 3
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);               //Write bit 4 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);               //Write bit 5
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);                //Write bit 6
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 1 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW); 
      cmdRepeatCount++;  //increase repeat count by 1
                                                //Control bytes 0 and 1 are written, now don’t care what happens in Bytes 2 to 7 and just wait for the next start bit after a long pause to send the first two command bytes again.
  }//While cmdRepeatCount < 5
}
void FocusNear() {
  cmdRepeatCount = 0;
  while (cmdRepeatCount < 5) {  //repeat 5 times to make sure the camera accepts the command
      while (pulseIn(lancPin, HIGH) < 5000) {   
                                                //"pulseIn, HIGH" catches any 0V TO +5V TRANSITION and waits until the LANC line goes back to 0V 
                                                //"pulseIn" also returns the pulse duration so we can check if the previous +5V duration was long enough (>5ms) to be the pause before a new 8 byte data packet
                                                //Loop till pulse duration is >5ms
      }
                                                //LOW after long pause means the START bit of Byte 0 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of byte 0 
                                                //"28hex" or “00101000”  tells the camera that there will be a normal command to camera in the next byte
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, LOW);                //Write bit 0. 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 1 
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);               //Write bit 3
      delayMicroseconds(bitDuration);  
      digitalWrite(cmdPin, LOW);               //Write bit 4
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);                //Write bit 5 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 6
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 0 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW);
      delayMicroseconds(10);                    //make sure to be in the stop bit before byte 1
      while (digitalRead(lancPin)) { 
                                                //Loop as long as the LANC line is +5V during the stop bit
      }   
                                                //0V after the previous stop bit means the START bit of Byte 1 is here
      delayMicroseconds(bitDuration);           //wait START bit duration
                                                //Write the 8 bits of Byte 1
                                                //"47hex" or “01000111” sends the  Record Start/Stop command
                                                //Note that the command bits have to be put out in reverse order with the least significant, right-most bit (bit 0) first
      digitalWrite(cmdPin, HIGH);               //Write bit 0 
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);               //Write bit 1 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, HIGH);                //Write bit 2
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);                //Write bit 3
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);               //Write bit 4 
      delayMicroseconds(bitDuration); 
      digitalWrite(cmdPin, LOW);               //Write bit 5
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, HIGH);                //Write bit 6
      delayMicroseconds(bitDuration);
      digitalWrite(cmdPin, LOW);                //Write bit 7
      delayMicroseconds(bitDuration);
                                                //Byte 1 is written now put LANC line back to +5V
      digitalWrite(cmdPin, LOW); 
      cmdRepeatCount++;  //increase repeat count by 1
                                                //Control bytes 0 and 1 are written, now don’t care what happens in Bytes 2 to 7 and just wait for the next start bit after a long pause to send the first two command bytes again.
  }//While cmdRepeatCount < 5
}

