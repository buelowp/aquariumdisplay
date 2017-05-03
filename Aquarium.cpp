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
	m_primary = new MainSelector(this);
	setCentralWidget(m_primary);
}

Aquarium::~Aquarium()
{
}
