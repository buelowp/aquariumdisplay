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
	connect(m_updateTemps, SIGNAL(timeout()), this, SLOT(updateDevices()));
	m_updateTemps->setInterval(1000);
	m_widgetIndex = 0;
	m_isMetric = false;

	parent->setStyleSheet("background-color: white");

	m_pump = new QPushButton(this);
	m_pumpLabel = new QLabel(this);
	m_pumpLabel->setText("Pump State");
	m_pumpLabel->setAlignment(Qt::AlignCenter);
	QFont f("Roboto");
	f.setPixelSize(20);
	m_pumpLabel->setFont(f);
	QIcon icon(":/images/pump-off.jpg");
	m_pump->setIcon(icon);
	connect(m_pump, SIGNAL(clicked()), this, SLOT(pumpToggled()));
	m_pump->setStyleSheet("QPushButton { \
		    background-color: white; \
		    border-style: outset; \
		    border-width: 2px; \
		    border-radius: 10px; \
		    border-color: white; \
		    font: bold 14px; \
		    min-width: 10em; \
		    padding: 6px; \
		}");
	m_pumpGpio = NULL;

	m_exit = new QPushButton(this);
	connect(m_exit, SIGNAL(clicked()), this, SLOT(exitButtonActivated()));

	m_layout = new QHBoxLayout();
	setLayout(m_layout);
}

DataView::~DataView()
{
	m_updateTemps->stop();
	delete m_updateTemps;

	if (m_probe.size()) {
		QMapIterator<QString, QLabel*> i(m_probe);
		while (i.hasNext()) {
			i.next();
			QLabel *l = i.value();
			delete l;
		}
	}
	if (m_probeLabel.size()) {
		QMapIterator<QString, QLabel*> i(m_probeLabel);
		while (i.hasNext()) {
			i.next();
			QLabel *l = i.value();
			delete l;
		}
	}
}

void DataView::paintEvent(QPaintEvent*)
{
    QStyleOption opt;
    opt.init(this);
    QPainter p(this);
    style()->drawPrimitive(QStyle::PE_Widget, &opt, &p, this);
}

bool DataView::event(QEvent *e)
{
	switch (e->type()) {
	case QEvent::Show:
		{
			QVBoxLayout *layout = new QVBoxLayout();
			m_pump->setIconSize(QSize(200, 200));
			m_exit->setGeometry(750, 25, 50, 50);
			m_exit->setText("X");
			layout->addWidget(m_pump);
			layout->addWidget(m_pumpLabel);
			m_layout->addLayout(layout);
		}
		break;
	case QEvent::Gesture:
		gestureEvent(static_cast<QGestureEvent*>(e));
		break;
	default:
		break;
	}

	return true;
}

void DataView::gestureEvent(QGestureEvent *e)
{
	qDebug() << __PRETTY_FUNCTION__;
    if (QGesture *swipe = e->gesture(Qt::SwipeGesture))
        swipeTriggered(static_cast<QSwipeGesture *>(swipe));
}

void DataView::swipeTriggered(QSwipeGesture *gesture)
{
    if (gesture->state() == Qt::GestureFinished) {
        if (gesture->horizontalDirection() == QSwipeGesture::Left) {
        	emit exitButtonActivated();
            qDebug() << "swipeTriggered(): exit";
        }
    }
}

bool DataView::setPumpDevice(QString n)
{
	m_pumpGpio = new QFile(n);

	if (m_pumpGpio->exists()) {
		return true;
	}

	return false;
}

bool DataView::setTempDeviceName(QString n)
{
	QFile f(n);

	if (f.exists()) {
		QVBoxLayout *layout = new QVBoxLayout();

		QString bn = n.right(n.length() - n.lastIndexOf("/"));
		m_probes.insert(bn, n);
		QLabel *t = new QLabel(this);
		QLabel *l = new QLabel(this);
		m_probe.insert(bn, t);
		m_probeLabel.insert(bn, l);
		l->setText(bn);
		layout->addWidget(t);
		layout->addWidget(l);
		m_layout->insertLayout(m_widgetIndex++, layout);

		f.close();
		return true;
	}

	return false;
}

void DataView::updateTemps()
{
	QMapIterator<QString, QLabel*> i(m_probe);
	QString s;
	int pos;

	while (i.hasNext()) {
		i.next();
		QFile f(i.value());
		f.open(QIODevice::ReadOnly);
		s = i.key();

		QByteArray ba = f.readAll();
		if ((pos = ba.indexOf("t=")) != -1) {
			pos += 2;	// skip t=
			double temp = ba.mid(pos, 5).toDouble();
			if (!m_isMetric) {
				temp = ((temp * 1.8) + 32);
			}
			if (m_probe.contains(s)) {
				QLabel *l = m_probe[s];
				l->setText(QString("%1").arg(temp));
			}
		}
	}
	i.toFront();
}

bool DataView::updatePumpState()
{
	if (!m_pumpGpio->open(QIODevice::ReadOnly|QIODevice::Text)) {
		return false;
	}

	QByteArray ba = m_pumpGpio->readAll();
	if (ba.toInt() == 0) {
		QIcon icon(":/images/pump-off.jpg");
		m_pump->setIcon(icon);
	}
	if (ba.toInt() == 1) {
		QIcon icon(":/images/pump-on.jpg");
		m_pump->setIcon(icon);
	}

	return true;
}

void DataView::updateDevices()
{
	if (m_probes.size() > 0)
		updateTemps();

	if (m_pumpGpio != NULL)
		updatePumpState();
}

void DataView::exitButtonActivated()
{
	emit closeWidget();
}

void DataView::pumpToggled()
{
	qDebug() << __PRETTY_FUNCTION__;
}
