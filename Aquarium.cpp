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
}

Aquarium::~Aquarium()
{
}

void Aquarium::showEvent(QShowEvent*)
{
	qDebug() << __PRETTY_FUNCTION__;
	m_primary = new MainSelector(this);
	m_primary->setGeometry(0, 0, width(), height());
	m_primary->init();
	setCentralWidget(m_primary);

	connect(m_primary, SIGNAL(activateFishDisplay()), this, SLOT(activateFishDisplay()));
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

}

void Aquarium::closeFishDisplay()
{
	m_fishview->hide();
	delete m_fishview;
	m_primary->show();
}
