/*
 * WebView.cpp
 *
 *  Created on: May 3, 2017
 *      Author: pete
 */

#include "WebView.h"

WebView::WebView(QWidget *parent) : QWidget(parent)
{
	m_position = 0;
	m_view = new QTextBrowser(this);
	m_view->setGeometry(0, 0, 800, 480);
	m_view->setReadOnly(true);
	m_exit = new QPushButton("X", this);
	m_forward = new QPushButton("->", this);
	m_back = new QPushButton("<-", this);
	m_exit->setGeometry(770, 10, 25, 25);
	m_forward->setGeometry(740, 350, 25, 25);
	m_back->setGeometry(10, 350, 25, 25);

	m_contentAvailable = false;

	connect(m_exit, SIGNAL(clicked()), this, SLOT(exitView()));
	connect(m_forward, SIGNAL(clicked()), this, SLOT(goForward()));
	connect(m_back, SIGNAL(clicked()), this, SLOT(goBack()));

	setAttribute(Qt::WA_AcceptTouchEvents);
	grabGesture(Qt::SwipeGesture);

	loadWebContent();
}

WebView::~WebView()
{
	qDebug() << __PRETTY_FUNCTION__;
	delete m_back;
	delete m_forward;
	delete m_exit;
	delete m_view;
}

bool WebView::event(QEvent *e)
{
	if (e->type() == QEvent::Show) {
		if (m_contentAvailable) {
			QUrl u(m_content[0]);
			m_view->setSource(u);
			m_view->show();
		}
		return true;
	}
	if (e->type() == QEvent::Gesture) {
		gestureEvent(static_cast<QGestureEvent*>(e));
		return true;
	}

	return false;
}

void WebView::gestureEvent(QGestureEvent *e)
{
	QGesture *swipe = NULL;

	qDebug() << __PRETTY_FUNCTION__;
	if ((swipe = e->gesture(Qt::SwipeGesture)) != NULL) {
		qDebug() << __PRETTY_FUNCTION__ << "Swipe";
		swipeTriggered(static_cast<QSwipeGesture *>(swipe));
	}
	else if ((swipe = e->gesture(Qt::PanGesture)) != NULL) {
		qDebug() << __PRETTY_FUNCTION__ << "Pan";
	}
	else if ((swipe = e->gesture(Qt::PinchGesture)) != NULL) {
		qDebug() << __PRETTY_FUNCTION__ << "Pinch";
	}
	else if ((swipe = e->gesture(Qt::CustomGesture)) != NULL) {
		qDebug() << __PRETTY_FUNCTION__ << "Custom";
	}
	else if ((swipe = e->gesture(Qt::TapGesture)) != NULL) {
		qDebug() << __PRETTY_FUNCTION__ << "Tap";
	}
	else if ((swipe = e->gesture(Qt::TapAndHoldGesture)) != NULL){
		qDebug() << __PRETTY_FUNCTION__ << "Tap and hold";
	}
	else
		qDebug() << __PRETTY_FUNCTION__ << ": e->type() is" << e->type();
}

void WebView::swipeTriggered(QSwipeGesture *gesture)
{
    if (gesture->state() == Qt::GestureFinished) {
        if (gesture->horizontalDirection() == QSwipeGesture::Left) {
            qDebug() << "swipeTriggered(): swipe to previous";
		goBack();
        }
        else if (gesture->horizontalDirection() == QSwipeGesture::Right) {
            qDebug() << "swipeTriggered(): swipe to next";
            goForward();
        }
    }
}

void WebView::loadWebContent()
{
	QDir dirp(WEBVIEW_SEARCH_PATH);
	QStringList webfiles;

	webfiles << "*.html";

	dirp.setFilter(QDir::Files);
	dirp.setNameFilters(webfiles);

	if (dirp.exists()) {
		foreach (QFileInfo item, dirp.entryInfoList()) {
			m_content.push_back(item.absoluteFilePath());
			m_contentAvailable = true;
		}
	}
	else
		m_contentAvailable = false;
}

void WebView::exitView()
{
	qDebug() << __PRETTY_FUNCTION__;
	emit closeWidget();
}

void WebView::goForward()
{
	if (m_contentAvailable) {
		if (m_position + 1 < m_content.size())
			m_position++;
		else
			m_position = 0;

		qDebug() << __PRETTY_FUNCTION__ << ":" << m_position;
		QUrl u(QString("file:///") + m_content[m_position]);
		m_view->setSource(u);
	}
}

void WebView::goBack()
{
	if (m_contentAvailable) {
		if (m_position > 0)
			m_position--;
		else
			m_position = m_content.size() - 1;

		qDebug() << __PRETTY_FUNCTION__ << ":" << m_position;
		QUrl u(QString("file:///") + m_content[m_position]);
		m_view->setSource(u);
	}
}
