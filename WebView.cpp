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
	m_view = new QWebEngineView(this);
	m_view->setGeometry(0, 0, 800, 480);
	m_exit = new QPushButton("X", this);
	m_forward = new QPushButton("->", this);
	m_back = new QPushButton("<-", this);
	m_exit->setGeometry(770, 10, 25, 25);
	m_forward->setGeometry(740, 350, 25, 25);
	m_back->setGeometry(10, 350, 25, 25);

	connect(m_exit, SIGNAL(clicked()), this, SLOT(exitView()));
	connect(m_forward, SIGNAL(clicked()), this, SLOT(goForward()));
	connect(m_back, SIGNAL(clicked()), this, SLOT(goBack()));

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
	switch (e->type()) {
	case QEvent::Show:
		{
			QUrl u("https://en.wikipedia.org/wiki/Neon_tetra");
			m_view->load(u);
			m_view->show();
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

void WebView::gestureEvent(QGestureEvent *e)
{
	qDebug() << __PRETTY_FUNCTION__;
    if (QGesture *swipe = e->gesture(Qt::SwipeGesture))
        swipeTriggered(static_cast<QSwipeGesture *>(swipe));
}

void WebView::swipeTriggered(QSwipeGesture *gesture)
{
    if (gesture->state() == Qt::GestureFinished) {
        if (gesture->horizontalDirection() == QSwipeGesture::Left) {
        	goBack();
            qDebug() << "swipeTriggered(): swipe to previous";
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

	foreach (QFileInfo item, dirp.entryInfoList()) {
		qDebug() << __PRETTY_FUNCTION__ << item.fileName();
		m_content.push_back(item.absoluteFilePath());
	}
}

void WebView::exitView()
{
	qDebug() << __PRETTY_FUNCTION__;
	emit closeWidget();
}

void WebView::goForward()
{
	if (m_position + 1 < m_content.size())
		m_position++;
	else
		m_position = 0;

	qDebug() << __PRETTY_FUNCTION__ << ":" << m_position;
	QUrl u(QString("file:///") + m_content[m_position]);
	m_view->load(u);
}

void WebView::goBack()
{
	if (m_position > 0)
		m_position--;
	else
		m_position = m_content.size() - 1;

	qDebug() << __PRETTY_FUNCTION__ << ":" << m_position;
	QUrl u(QString("file:///") + m_content[m_position]);
	m_view->load(u);
}
