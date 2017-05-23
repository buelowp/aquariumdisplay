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
	m_exit->setGeometry(770, 10, 25, 25);

	m_contentAvailable = false;

	connect(m_exit, SIGNAL(clicked()), this, SLOT(exitView()));

	setAttribute(Qt::WA_AcceptTouchEvents);

	loadWebContent();
}

WebView::~WebView()
{
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
	if (e->type() == QEvent::TouchBegin) {
		beginTouchEvent(static_cast<QTouchEvent*>(e));
		return true;
	}
	if (e->type() == QEvent::TouchEnd) {
		return endTouchEvent(static_cast<QTouchEvent*>(e));
	}

	return false;
}

void WebView::beginTouchEvent(QTouchEvent *e)
{
	QList<QTouchEvent::TouchPoint> points = e->touchPoints();
	QTouchEvent::TouchPoint pos = points.at(0);
	m_touchBegin = pos.pos();
}

bool WebView::endTouchEvent(QTouchEvent *e)
{
	QList<QTouchEvent::TouchPoint> points = e->touchPoints();
	QTouchEvent::TouchPoint pos = points.at(0);
	m_touchEnd = pos.pos();

	if (m_touchBegin == m_touchEnd) {
		if (m_tapEvent) {
			m_tapEvent = false;
			exitView();
		}
		else
			m_tapEvent = true;

	}
	else if ((m_touchEnd.y() > m_touchBegin.y() - 100) && (m_touchEnd.y() < m_touchBegin.y() + 100)) {
		if (m_touchBegin.x() - m_touchEnd.x() > 200) {
			goBack();
		}
		else if ((m_touchEnd.x() - m_touchBegin.x()) > -200) {
			goForward();
		}
		return true;
	}
	return false;
}

void WebView::loadWebContent()
{
	QDir dirp(WEBVIEW_SEARCH_PATH);
	QStringList webfiles;

	webfiles << "*.html";

	dirp.setFilter(QDir::Files);
	dirp.setNameFilters(webfiles);

	qDebug() << __PRETTY_FUNCTION__ << ": Searching" << WEBVIEW_SEARCH_PATH;
	if (dirp.exists()) {
		foreach (QFileInfo item, dirp.entryInfoList()) {
			qDebug() << __PRETTY_FUNCTION__ << ":" << item.absoluteFilePath();
			m_content.push_back(item.absoluteFilePath());
			m_contentAvailable = true;
		}
	}
	else
		m_contentAvailable = false;
}

void WebView::exitView()
{
	emit closeWidget();
}

void WebView::goForward()
{
	if (m_contentAvailable) {
		if (m_position + 1 < m_content.size())
			m_position++;
		else
			m_position = 0;

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

		QUrl u(QString("file:///") + m_content[m_position]);
		m_view->setSource(u);
	}
}
