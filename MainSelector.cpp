/*
 * MainSelector.cpp
 *
 *  Created on: May 2, 2017
 *      Author: pete
 */

#include "MainSelector.h"

MainSelector::MainSelector(QWidget *parent) : QWidget(parent)
{
	m_fish = new QPushButton(this);
	m_fish->setIcon(QIcon(":/images/fish.png"));
	m_data = new QPushButton(this);
	m_fishLabel = new QLabel(this);
	m_fishLabel->setText("See the Fish");
	m_dataLabel = new QLabel(this);
	m_dataLabel->setText("See Tank Data");
	m_layout = new QGridLayout(this);
	m_layout->addWidget(m_fish, 0, 0);
	m_layout->addWidget(m_fishLabel, 1, 0);
	m_layout->addWidget(m_data, 0, 1);
	m_layout->addWidget(m_dataLabel, 1, 1);
}

MainSelector::~MainSelector()
{
}

void MainSelector::showEvent(QShowEvent*)
{
}
