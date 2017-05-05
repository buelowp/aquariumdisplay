/*
 * Lights.h
 *
 *  Created on: May 5, 2017
 *      Author: pete
 */

#ifndef LIGHTS_H_
#define LIGHTS_H_

#include <QtCore>
#include <pixel.hpp>
#include <SunSet.h>

#define LATITUDE	42.0
#define LONGITUDE	88.0

class Lights : public QObject {
	Q_OBJECT
public:
	Lights(int, QObject *parent = 0);
	virtual ~Lights();

public slots:
	void timeout();

protected:
	void runDayTime(int);
	void runNightTime();

	QTimer *m_timer;
	PixelBone_Pixel *m_strip;
	SunSet *m_sun;
	int m_pixels;
};

#endif /* LIGHTS_H_ */
