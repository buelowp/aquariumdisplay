/*
 * DataView.cpp
 *
 *  Created on: May 3, 2017
 *      Author: pete
 */

#include "DataView.h"

DataView::DataView(QWidget *parent) : QWidget(parent)
{
	m_updateTemps = new QTimer(this);
	connect(m_updateTemps, SIGNAL(timeout()), this, SLOT(updateTemps()));
	m_updateTemps->setInterval(1000);
}

DataView::~DataView()
{
}

bool DataView::setTempDeviceName(QString n)
{
	QFile f(n);

	if (f.exists()) {
		QString bn = n.right(n.length() - n.lastIndexOf("/"));
		m_probes[bn] = f;
		QLabel *t = new QLabel(this);
		QLabel *l = new QLabel(this);
		l->setText(bn);
	}
}
