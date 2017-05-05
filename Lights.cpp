/*
 * Lights.cpp
 *
 *  Created on: May 5, 2017
 *      Author: pete
 */

#include "Lights.h"

Lights::Lights(int pixels, QObject *parent) : QObject(parent)
{
	m_pixels = pixels;
	m_strip = new PixelBone_Pixel(m_pixels);
	m_timer = new QTimer(this);
	m_timer->setInterval(1000);
	m_timer->start();
	connect(m_timer, SIGNAL(timeout()), this, SLOT(timeout()));

	QDateTime dt = QDateTime::currentDateTime();
	m_sun = new SunSet(LATITUDE, LONGITUDE, -5);
}

Lights::~Lights()
{
}

void Lights::timeout()
{
	QDateTime midnight;
	QDateTime now = QDateTime::currentDateTime();
	midnight.setDate(now.date());
	midnight.setTime(QTime(0, 0, 0));
	int nowminutes = midnight.secsTo(now) / 60;

	if ((nowminutes >= m_sun->calcSunrise() - 30) && (nowminutes <= m_sun->calcSunset() + 30)) {
		runDayTime(nowminutes);
	}
	else {
		runNightTime();
	}
}

void Lights::runDayTime(int now)
{
	Q_UNUSED(now)
}

void Lights::runNightTime()
{
	int mp = m_sun->moonPhase();
	qDebug() << __PRETTY_FUNCTION__ << ": The moon is" << mp << "bright now";
}
