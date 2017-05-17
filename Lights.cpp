/*
 * Lights.cpp
 *
 *  Created on: May 5, 2017
 *      Author: pete
 */

#include "Lights.h"

Lights::Lights(QObject *parent) : QObject(parent)
{
	m_timer = new QTimer(this);
	m_timer->setInterval(1000);
	connect(m_timer, SIGNAL(timeout()), this, SLOT(timeout()));
	m_timer->start();

	QDateTime dt = QDateTime::currentDateTime();
	m_sun = new SunSet(LATITUDE, LONGITUDE, -5);

	FastLED.addLeds<WS2801, 0, 0, RGB>(leds, NUM_LEDS);
}

Lights::~Lights()
{
}

void Lights::updateTimeZone()
{
	QDateTime dt = QDateTime::currentDateTime();
	m_sun->setTZOffset(dt.offsetFromUtc());
}

void Lights::setColor(CRGB c)
{
	for (int i = 0; i < NUM_LEDS; i++) {
		leds[i] = c;
	}
}

double Lights::getSunrise()
{
	double sr = m_sun->calcSunrise();
	if (sr < 360)
		sr = (double)360.0;

	if (sr > 420)
		sr = (double)420.0;

	return sr;
}

double Lights::getSunset()
{
	double ss = m_sun->calcSunset();
	if (ss > 1260)
		ss = (double)1260.0;

	if (ss < 1200)
		ss = (double)1200.0;

	return ss;
}

void Lights::runSunrise(int now)
{
	int when = now - getSunrise() - 30;
	int bright = (when * 4) + 10;
	CRGB c = CRGB::White;

	if (bright <= m_sun->moonPhase()) {
		bright = m_sun->moonPhase();
		c = CRGB::DeepSkyBlue;
	}

	FastLED.setBrightness(bright);
	setColor(c);
	FastLED.show();
}

void Lights::runSunset(int now)
{
	int when = now - getSunset() - 30;
	int bright = 250 - (when * 4);
	CRGB c = CRGB::White;

	if (bright <= m_sun->moonPhase()) {
		bright = m_sun->moonPhase();
		c = CRGB::DeepSkyBlue;
	}

	FastLED.setBrightness(bright);
	setColor(c);
	FastLED.show();
}

void Lights::timeout()
{
	QDateTime midnight;
	QDateTime now = QDateTime::currentDateTime();
	midnight.setDate(now.date());
	midnight.setTime(QTime(0, 0, 0));
	int nowminutes = midnight.secsTo(now) / 60;

	if ((nowminutes > (getSunrise() - 30)) && (nowminutes < (getSunrise() + 30)))
		runSunrise(nowminutes);
	else if ((nowminutes > (getSunset() - 30)) && (nowminutes < (getSunset() + 30)))
		runSunset(nowminutes);
	else if ((nowminutes > (getSunrise() + 30)) && (nowminutes < (getSunset() - 30))) {
		runDayTime(nowminutes);
	}
	else {
		runNightTime();
	}

	updateTimeZone();
}

void Lights::runDayTime(int now)
{
	setColor(CRGB::White);
	FastLED.setBrightness(255);
	FastLED.show();
}

void Lights::runNightTime()
{
	FastLED.setBrightness(m_sun->moonPhase());
	setColor(CRGB::DeepSkyBlue);
	FastLED.show();
}
