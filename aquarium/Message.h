#ifndef __MESSAGE_H__
#define __MESSAGE_H__

#include <Arduino.h>
#include <stdint.h>

#define INDEX_MSG_SIZE  1
#define INDEX_MSG_COUNT 2

class Message {
public:
  Message();

  void hello();
  void setTemps(uint8_t*, int, uint8_t*, int);
  void setWaterLevel(uint8_t*, int);
  void makeFinal();
  uint8_t* getBuffer();
  int getSize();
  void printBuffer();
  void clear();
  void setUVState(uint8_t);
  void setPumpState(uint8_t);
  void setHeaterState(uint8_t);

private:
  uint8_t m_internal[128];
  int m_currIndex;
};

#endif

