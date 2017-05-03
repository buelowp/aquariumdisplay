/*
 * MainSelector.cpp
 *
 *  Created on: May 2, 2017
 *      Author: pete
 */

#include "MainSelector.h"

MainSelector::MainSelector(QWidget *parent) : QWidget(parent)
{
	m_fish = NULL;
	m_data = NULL;
	m_fishLabel = NULL;
	m_dataLabel = NULL;
}

MainSelector::~MainSelector()
{
}

void MainSelector::init()
{
	m_fish = new QPushButton(this);
	m_data = new QPushButton(this);
	m_fishLabel = new QLabel(this);
	m_fishLabel->setText("Get to know what's in the Aquarium");
	m_dataLabel = new QLabel(this);
	m_dataLabel->setText("How is the tank doing?");
	m_fishLabel->setAlignment(Qt::AlignCenter);
	m_dataLabel->setAlignment(Qt::AlignCenter);

	connect(m_fish, SIGNAL(clicked()), this, SLOT(fishButtonPressed()));
	connect(m_data, SIGNAL(clicked()), this, SLOT(dataButtonPressed()));

	QFont f("Roboto");
	f.setPixelSize(20);
	m_fishLabel->setFont(f);
	m_dataLabel->setFont(f);

	m_fish->setStyleSheet("QPushButton { \
	    background-color: black; \
	    border-style: outset; \
	    border-width: 2px; \
	    border-radius: 10px; \
	    border-color: white; \
	    font: bold 14px; \
	    min-width: 10em; \
	    padding: 6px; \
	}");
	m_data->setStyleSheet("QPushButton { \
	    background-color: black; \
	    border-style: outset; \
	    border-width: 2px; \
	    border-radius: 10px; \
	    border-color: white; \
	    font: bold 14px; \
	    min-width: 10em; \
	    padding: 6px; \
	}");
}

void MainSelector::paintEvent(QPaintEvent*)
{
    QStyleOption opt;
    opt.init(this);
    QPainter p(this);
    style()->drawPrimitive(QStyle::PE_Widget, &opt, &p, this);
}

void MainSelector::showEvent(QShowEvent *e)
{
	QIcon fish(":/images/fish.png");
	QIcon data(":/images/data.png");

	if (e->type() == QEvent::Show) {
		m_fish->setGeometry(50, 50, 300, 300);
		m_fish->setIcon(fish);
		m_fish->setIconSize(QSize(200, 200));
		m_data->setGeometry(450, 50, 300, 300);
		m_data->setIcon(data);
		m_data->setIconSize(QSize(200, 200));
		m_fishLabel->setGeometry(0, 350, 400, 50);
		m_dataLabel->setGeometry(400, 350, 400, 50);
		setStyleSheet("QWidget { background-color: white; }");
	}
}

void MainSelector::fishButtonPressed()
{
	qDebug() << __PRETTY_FUNCTION__;
	emit activateFishDisplay();
}

void MainSelector::dataButtonPressed()
{
	qDebug() << __PRETTY_FUNCTION__;
}
