//
//  Aquarium.cpp
//  aquarium
//
//  Created by Peter Buelow on 5/2/17.
//
//

#include "Aquarium.h"

Aquarium::Aquarium()
{
	m_primary = NULL;
	m_fishview = NULL;
	m_dataview = NULL;

	m_lights = new Lights(this);
	setWindowFlags(Qt::Window | Qt::FramelessWindowHint | Qt::WindowCloseButtonHint);
}

Aquarium::~Aquarium()
{
}

void Aquarium::showEvent(QShowEvent*)
{
	m_primary = new MainSelector(this);
	m_primary->setGeometry(0, 0, width(), height());
	m_primary->init();
	setCentralWidget(m_primary);

	connect(m_primary, SIGNAL(activateFishDisplay()), this, SLOT(activateFishDisplay()));
	connect(m_primary, SIGNAL(activateDataDisplay()), this, SLOT(activateDataDisplay()));
}

void Aquarium::activateFishDisplay()
{
	qDebug() << __PRETTY_FUNCTION__;

	m_fishview = new WebView(this);
	m_fishview->setGeometry(0, 0, 800, 480);
	connect(m_fishview, SIGNAL(closeWidget()), this, SLOT(closeFishDisplay()));
	m_primary->hide();
	m_fishview->show();
}

void Aquarium::activateDataDisplay()
{
	qDebug() << __PRETTY_FUNCTION__;

	m_dataview = new DataView(this);
	m_dataview->setGeometry(0, 0, 800, 480);
	connect(m_dataview, SIGNAL(closeWidget()), this, SLOT(closeDataDisplay()));
	m_primary->hide();
	m_dataview->show();
}

void Aquarium::closeFishDisplay()
{
	m_fishview->hide();
	delete m_fishview;
	m_primary->show();
}

void Aquarium::closeDataDisplay()
{
	m_dataview->hide();
	delete m_dataview;
	m_primary->show();
}
