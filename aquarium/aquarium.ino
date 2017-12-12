#include <FastLED.h>
#include <DallasTemperature.h>
#include <OneWire.h>
#include "Message.h"

#define SERIES_RESISTOR     748    // Value of the series resistor in ohms.    
#define SENSOR_PIN          14      // Analog pin which is connected to the sensor. 
#define UV_PIN              2
#define TEMP_PIN            16
#define PUMP_PIN            3
#define HEATER_PIN          4
#define NUM_LEDS            10
#define INDICATOR           7

#define ZERO_VOLUME_RESISTANCE    804.14    // Resistance value (in ohms) when no liquid is present.
#define CALIBRATION_RESISTANCE    0.00    // Resistance value (in ohms) when liquid is at max line.
#define CALIBRATION_VOLUME        0.00    // Volume (in any units) when liquid is at max line.

elapsedMillis sincePrint;
byte thingsMsg[128] = {};
OneWire ds(TEMP_PIN);
DallasTemperature sensors(&ds);
CRGB leds[NUM_LEDS];
int msgIndex;
Message msgBuffer;
elapsedMillis g_toggle;
uint8_t g_brightness;
bool g_lights;

typedef union {
  float f;
  byte b[4];
} CVT;

void printSerialMessage(byte *msg, int s)
{
  for (int i = 0; i < s; i++) {
    Serial.print("0x");
    Serial.print(msg[i], HEX);
    Serial.print(",");
  }
  Serial.println("");
}

float resistanceToVolume(float resistance, float zeroResistance, float calResistance, float calVolume) 
{
  if (resistance > zeroResistance || (zeroResistance - calResistance) == 0.0) {
    // Stop if the value is above the zero threshold, or no max resistance is set (would be divide by zero).
    return 0.0;
  }

  // Compute scale factor by mapping resistance to 0...1.0+ range relative to maxResistance value.
  float scale = (zeroResistance - resistance) / (zeroResistance - calResistance);
  // Scale maxVolume based on computed scale factor.
  return calVolume * scale;
}

float readResistance(int pin, int seriesResistance) 
{
  // Get ADC value.
  float resistance = analogRead(pin);
  // Convert ADC reading to resistance.
  resistance = (1023.0 / resistance) - 1.0;
  resistance = seriesResistance / resistance;
  return resistance;
}

void replyHello()
{
  Message msg;
  msg.hello();
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial.flush();
  delay(100);
}

void shutdownDevice()
{
  Serial.println("Request to shutdown");
}

void restartProgram()
{
  
}

void setLedStripColor(byte msg[], int bytes)
{
  if (bytes != 3)
    return;
    
  Serial.println("Request to set the LED color");
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i].r = msg[0];
    leds[i].g = msg[1];
    leds[i].b = msg[2];
  }
}

void setLedColor(byte msg[])
{
  Serial.print("Request to set LED ");
  Serial.print(msg[0]);
  Serial.print(" to color r=");
  Serial.print(msg[1]);
  Serial.print(", g=");
  Serial.print(msg[2]);
  Serial.print(", b=");
  Serial.println(msg[3]);
  leds[msg[0]].r = msg[1];
  leds[msg[0]].g = msg[2];
  leds[msg[0]].b = msg[3];
}

void setLedBrightness(byte b[], int bytes)
{
  if (bytes != 1)
    return;
    
  Serial.print("Request to set LED strip to brightness value ");
  Serial.println(b[0]);
  FastLED.setBrightness(b[0]);
  g_brightness = (uint8_t)b[0];
  getLightBrightness();
}

void getWaterLevel()
{
  Message msg;
  CVT convert;
  
  Serial.println("Rquest to check water level");
  float resistance = readResistance(SENSOR_PIN, SERIES_RESISTOR);
  convert.f = resistance;

  msgBuffer.setWaterLevel(convert.b, 4);
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void getTemps()
{
  Message msg;
  CVT convertRight;
  CVT convertLeft;
  
  Serial.println("Request to check water temperature");
  sensors.requestTemperatures();
  convertRight.f = sensors.getTempFByIndex(0);
  convertLeft.f = sensors.getTempFByIndex(1);
  msg.setTemps(convertLeft.b, 4, convertRight.b, 4);
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void turnOnUVLights()
{
  digitalWrite(UV_PIN, 0);
  Serial.print("Turning on UV lights: ");
  Serial.println(digitalRead(UV_PIN));
  getUVState();
}

void turnOffUVLights()
{
  digitalWrite(UV_PIN, 1);
  delay(50);
  Serial.print("Turning off UV Lights: ");
  Serial.println(digitalRead(UV_PIN));
  getUVState();
}

void getUVState()
{
  Message msg;
  Serial.println("Getting UV state");
  msg.getUVState(!digitalRead(UV_PIN));
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void getSunlightState()
{
  Message msg;
  Serial.println("Getting Sunlight state");
  msg.setPrimaryLightState(g_lights);
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void getLightBrightness()
{
  Message msg;
  Serial.println("Getting Brightness");
  msg.setLEDBrightness(g_brightness);
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void getPumpState()
{
  Message msg;
  Serial.println("Setting Pump state");
  msg.setPumpState(digitalRead(PUMP_PIN));
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void getHeaterState()
{
  Message msg;
  Serial.println("Setting Heater state");
  msg.setHeaterState(digitalRead(HEATER_PIN));
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void toggleSunLights()
{
  if (g_lights) {
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB::Black;
    }
    FastLED.show();
    g_lights = false;
  }
}

void toggleAllLights()
{
  Serial.println("Togging all lights");
  toggleSunLights();
//  toggleUV();
  getUVState();
  getLightBrightness();
  getSunlightState();
}

void getRGBValues()
{
  Serial.println("Retrieving RGB values");
  Message msg;
  msg.setRGBValues(leds[0]);
  msg.makeFinal();
  msg.printBuffer();
  Serial1.write(msg.getBuffer(), msg.getSize());
  Serial1.flush();
  delay(100);
}

void togglePumpState()
{
  digitalWrite(PUMP_PIN, !digitalRead(PUMP_PIN));
}

void toggleHeaterState()
{
  digitalWrite(HEATER_PIN, !digitalRead(HEATER_PIN));  
}

void executeShow()
{
  FastLED.show();
}

void testLEDS()
{
  Serial.println("Testing colors");
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB::Red;
  }
  FastLED.setBrightness(255);
  FastLED.show();
  delay(1000);
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB::Green;
  }
  FastLED.setBrightness(255);
  FastLED.show();
  delay(1000);
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB::Blue;
  }
  FastLED.setBrightness(255);
  FastLED.show();
  delay(1000);
}

void parseThingsMsg(byte msg[])
{
  int index = 3;
  byte contents[128];
  int numMessages = msg[2];
  
  if (msg[0] == 0xF0) {
    Serial.print("There are ");
    Serial.print(numMessages);
    Serial.println(" messages inside");
    for (int i = 0; i < numMessages; i++) { 
      Serial.print("Checking message at index: ");
      Serial.print(index);

      int message = msg[index++];
      int msgSize = msg[index++];

      Serial.print(", message id 0x");
      Serial.println(message, HEX);

      for (int j = 0; j < msgSize; j++) {
        contents[j] = msg[index + j];
      }
       switch (message) {
        case 0xAA:
          replyHello();
          break;
        case 0xFF:
          shutdownDevice();
          break;
        case 0x01:
          setLedStripColor(contents, msgSize);
          break;
        case 0x02:
          setLedBrightness(contents, msgSize);
          break;
        case 0x03:
          getWaterLevel();
          break;
        case 0x04:
          getTemps();
          break;
        case 0x05:
          Serial.println("Someone asked to toggle the UV lights");
//          toggleUV();
          break;
        case 0x06:
          restartProgram();
          break;
        case 0x07:
          getUVState();
          break;
        case 0x08:
          getPumpState();
          break;
        case 0x09:
          getHeaterState();
          break;
        case 0x0A:
          toggleSunLights();
          break;
        case 0x0B:
          toggleAllLights();
          break;
        case 0x0C:
          getLightBrightness();
          break;
        case 0x0D:
          togglePumpState();
          break;
        case 0x0E:
          toggleHeaterState();
          break;
        case 0x0F:
          getSunlightState();
          break;
        case 0x10:
          turnOnUVLights();
          break;
        case 0x11:
          turnOffUVLights();
          break;
        case 0x13:
          getRGBValues();
          break;
        case 0x14:
          executeShow();
          break;
        case 0x15:
          setLedColor(contents);
        default:
          Serial.println("Unknown message");
          for (int i = 0; i < 10; i++) {
            if (msg[i] == 0xF1) {
              Serial.println(msg[i], HEX);
              break;
            }
            Serial.print(msg[i], HEX);
            Serial.print(",");
          }
          Serial.println("");
      }
      index += msgSize;
    }
  }
}

void clearStrip()
{
  Serial.println("Setting all pins to black");
  for (int i = 144; i < 0; i++) {
    leds[i] = CRGB::Black;
  }
  FastLED.setBrightness(0);
  FastLED.show();
}

void setup() 
{
  Serial.begin(115200);
  Serial1.begin(115200);
  delay(3000);
  pinMode(UV_PIN, OUTPUT);
  pinMode(INDICATOR, OUTPUT);
  digitalWrite(INDICATOR, 1);
  digitalWrite(UV_PIN, 1);
  digitalWrite(PUMP_PIN, 1);
  sensors.begin();
  FastLED.addLeds<APA102,11,13,BGR>(leds,NUM_LEDS);
  msgIndex = 0;
  g_brightness = 250;
  g_lights = false;
  clearStrip();
  testLEDS();
  clearStrip();
}

void loop() 
{
  while (Serial1.available()) {
    int data = Serial1.read();
    thingsMsg[msgIndex++] = (byte)data;
    if (data == 0xF1) {
      printSerialMessage(thingsMsg, msgIndex);
      parseThingsMsg(thingsMsg);
      msgIndex = 0;
    }
  }
  if (g_toggle > 1000) {
    digitalWrite(7, !digitalRead(7));
    g_toggle = 0;
  }
}



