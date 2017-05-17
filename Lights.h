/*
 * Lights.h
 *
 *  Created on: May 5, 2017
 *      Author: pete
 */

#ifndef LIGHTS_H_
#define LIGHTS_H_

#include <QtCore>
#include <SunSet.h>
#include <FastLED/FastLED.h>

#define LATITUDE	42.0
#define LONGITUDE	88.0
#define NUM_LEDS	38

const uint8_t _usDSTStart[22] = { 8,13,12,11,10, 8,14,13,12,10, 9, 8,14,12,11,10, 9,14,13,12,11, 9};
const uint8_t _usDSTEnd[22]   = { 1, 6, 5, 4, 3, 1, 7, 6, 5, 3, 2, 1, 7, 5, 4, 3, 2, 7, 6, 5, 4, 2};

class Lights : public QObject {
	Q_OBJECT
public:
	Lights(QObject *parent = 0);
	virtual ~Lights();

public slots:
	void timeout();

protected:
	void runDayTime(int);
	void runNightTime();
	void setColor(CRGB);
	void runSunrise(int);
	void runSunset(int);
	double getSunrise();
	double getSunset();
	void updateTimeZone();

	QTimer *m_timer;
	SunSet *m_sun;
	CRGB leds[NUM_LEDS];
};

#endif /* LIGHTS_H_ */
