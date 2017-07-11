#include <FastLED.h>
#include <DallasTemperature.h>
#include <OneWire.h>

#define SERIES_RESISTOR     748    // Value of the series resistor in ohms.    
#define SENSOR_PIN          14      // Analog pin which is connected to the sensor. 
#define UV_PIN              2
#define TEMP_PIN            16
#define PUMP_PIN            3
#define HEATER_PIN          4
#define NUM_LEDS            60

#define ZERO_VOLUME_RESISTANCE    804.14    // Resistance value (in ohms) when no liquid is present.
#define CALIBRATION_RESISTANCE    0.00    // Resistance value (in ohms) when liquid is at max line.
#define CALIBRATION_VOLUME        0.00    // Volume (in any units) when liquid is at max line.

elapsedMillis sincePrint;
byte thingsMsg[128] = {};
OneWire ds(TEMP_PIN);
DallasTemperature sensors(&ds);
CRGB leds[NUM_LEDS];

typedef union {
  float f;
  byte b[4];
} CVT;

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
  byte response[] = {0xF0, 0x01, 0xAA, 0xF1};
  Serial1.write(response, 4);
  Serial.println("Sending back Hello");
}

void shutdownDevice()
{
  Serial.println("Request to shutdown");
}

void restartProgram()
{
  
}

void setLedColor(byte r, byte g, byte b)
{
  Serial.println("Request to set the LED color");
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i].r = r;
    leds[i].g = g;
    leds[i].b = b;
  }
  FastLED.show();
}

void setLedBrightness(byte b)
{
  Serial.print("Request to set LED strip to brightness value ");
  Serial.println(b);
  FastLED.setBrightness(b);
  FastLED.show();
}

void getWaterLevel()
{
  CVT convert;
  byte response[8];
  
  Serial.println("Rquest to check water level");
  float resistance = readResistance(SENSOR_PIN, SERIES_RESISTOR);
  convert.f = resistance;
  response[0] = 0xF0;
  response[1] = 0x04;
  response[2] = 0x03;
  for (int i = 0; i < 4; i++) {
    response[i + 3] = convert.b[i];
  }
  response[7] = 0xF1;
  Serial1.write(response, 8);
}

void getTemps()
{
  CVT convertRight;
  CVT convertLeft;
  byte response[12];
  
  Serial.println("Request to check water temperature");
  sensors.requestTemperatures();
  convertRight.f = sensors.getTempFByIndex(0);
  convertLeft.f = sensors.getTempFByIndex(1);

  response[0] = 0xF0;
  response[1] = 0x08;
  response[2] = 0x04;
  for (int i = 0; i < 4; i++) {
    response[3 + i] = convertRight.b[i];
    response[7 + i] = convertLeft.b[i];
  }
  response[11] = 0xF1;
  Serial1.write(response, 12);
}

void toggleUV(byte flag)
{
  
  if (flag == 0x01) {
    Serial.println("Turning on UV lights");
    digitalWrite(UV_PIN, 1);
  }
  else {
    Serial.println("Turning off UV lights");
    digitalWrite(UV_PIN, 0);
  }
}

void getUVState()
{
  byte response[5];

  response[0] = 0xF0;
  response[1] = 0x01;
  response[2] = 0x07;
  response[3] = digitalRead(UV_PIN);
  response[4] = 0xF1;
  Serial1.write(response, 5);
}

void getPumpState()
{
  byte response[5];

  response[0] = 0xF0;
  response[1] = 0x01;
  response[2] = 0x07;
  response[3] = digitalRead(PUMP_PIN);
  response[4] = 0xF1;
  Serial1.write(response, 5);
}

void getHeaterState()
{
  byte response[5];

  response[0] = 0xF0;
  response[1] = 0x01;
  response[2] = 0x07;
  response[3] = digitalRead(HEATER_PIN);
  response[4] = 0xF1;
  Serial1.write(response, 5);
}

void turnOffSunLights()
{
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB::Black;
  }
  FastLED.show();
}

void toggleAllLights()
{
  turnOffSunLights();
  toggleUV(0);
}

void getSunLightState()
{
  byte response[5];

  response[0] = 0xF0;
  response[1] = 0x01;
  response[2] = 0x0C;
  if (leds[0] == CRGB::Black)
    response[3] = 0x00;
  else
    response[3] = 0x01;
    
  response[4] = 0xF1;
  Serial1.write(response, 5);
}

void parseThingsMsg(byte msg[])
{
  if (msg[0] == 0xF0) {
    switch (msg[2]) {
      case 0xAA:
        replyHello();
        break;
      case 0xFF:
        shutdownDevice();
        break;
      case 0x01:
        setLedColor(msg[3], msg[4], msg[5]);
        break;
      case 0x02:
        setLedBrightness(msg[3]);
        break;
      case 0x03:
        getWaterLevel();
        break;
      case 0x04:
        getTemps();
        break;
      case 0x05:
        toggleUV(msg[3]);
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
        turnOffSunLights();
        break;
      case 0x0B:
        toggleAllLights();
        break;
      case 0x0C:
        getSunLightState();
        break;
      case 0x0D:
        togglePumpState();
        break;
      case 0x0E:
        toggleHeaterState();
        break;
      default:
        Serial.println("Unknown message");
        for (int i = 0; i < 10; i++) {
          if (msg[i] == 0xF1) {
            Serial.println(msg[i], HEX);
            break;
          }
          Serial.print(msg[i], HEX);
          Serial.print(" ");
        }
    }
  }
}

void setup() 
{
  Serial.begin(115200);
  Serial1.begin(115200);
  pinMode(UV_PIN, OUTPUT);
  digitalWrite(UV_PIN, 0);
  sensors.begin();
  FastLED.addLeds<APA102>(leds, NUM_LEDS);
}

void loop() 
{
  int i = 0;
  
  while (Serial1.available()) {
    int data = Serial1.read();
    if (i == 0 && data != 0xF0)
      continue;
      
    if (data != -1) {
      thingsMsg[i++] = (byte)data;
    }
  }
  if (i > 1) {
    parseThingsMsg(thingsMsg);
  }
//  float resistance = readResistance(SENSOR_PIN, SERIES_RESISTOR);
//  Serial.print("Resistance: "); 
//  Serial.print(resistance, 2);
//  Serial.println(" ohms");
  // Map resistance to volume.
//  float volume = resistanceToVolume(resistance, ZERO_VOLUME_RESISTANCE, CALIBRATION_RESISTANCE, CALIBRATION_VOLUME);
//  Serial.print("Calculated volume: ");
//  Serial.println(volume, 5);
  // Delay for a second.
//  delay(1000);
}



