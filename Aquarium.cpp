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
}
