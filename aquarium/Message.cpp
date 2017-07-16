#include "Message.h"

Message::Message()
{
  m_internal[0] = 0xF0;
  m_internal[INDEX_MSG_SIZE] = 3;
  m_internal[INDEX_MSG_COUNT] = 0;
  m_currIndex = 3;
}

void Message::clear()
{
  for (int i = 0; i < 128; i++) {
    m_internal[i] = 0;
    m_currIndex = 0;
  }
}
void Message::printBuffer()
{
  int length = m_internal[INDEX_MSG_SIZE];

  Serial.print("buffer is ");
  Serial.print(length);
  Serial.println(" bytes in size");
  for (int i = 0; i < (length - 1); i++) {
    Serial.print("0x");
    Serial.print(m_internal[i], HEX);
    Serial.print(",");
  }
  Serial.print("0x");
  Serial.print(m_internal[length - 1], HEX);
  Serial.println("");
}

void Message::makeFinal()
{
  m_internal[m_currIndex] = (byte)0xF1;
  m_internal[INDEX_MSG_SIZE] += 1;
}

int Message::getSize()
{
  return m_internal[INDEX_MSG_SIZE];
}

uint8_t * Message::getBuffer()
{
  return m_internal;
}

void Message::hello()
{
  m_internal[m_currIndex++] = 0xAA;
  m_internal[m_currIndex++] = 0x00;
  m_internal[INDEX_MSG_COUNT] += 1;
  m_internal[INDEX_MSG_SIZE] += 2;
}

void Message::setWaterLevel(uint8_t *level, int length)
{
  m_internal[m_currIndex++] = 3;
  m_internal[m_currIndex++] = length;
  m_internal[INDEX_MSG_SIZE] += length + 2;
  m_internal[INDEX_MSG_COUNT] = 1;

  for (int i = 0; i < length; i++) {
    m_internal[m_currIndex++] = level[i];
  }
}

void Message::setTemps(uint8_t *left, int len_left, uint8_t *right, int len_right)
{
  m_internal[m_currIndex++] = 4;
  m_internal[m_currIndex++] = len_left + len_right;
  m_internal[INDEX_MSG_SIZE] += len_left + len_right + 2;
  m_internal[INDEX_MSG_COUNT] = 1;

  for (int i = 0; i < len_left; i++) {
    m_internal[m_currIndex++] = left[i];
  }
  
  for (int i = 0; i < len_right; i++) {
    m_internal[m_currIndex++] = right[i];
  }
}

