/*
 * WebView.h
 *
 *  Created on: May 3, 2017
 *      Author: pete
 */

#ifndef WEBVIEW_H_
#define WEBVIEW_H_

#include <QtCore>
#include <QtWidgets>

#define WEBVIEW_SEARCH_PATH		"/var/www/pages"

class WebView : public QWidget {
	Q_OBJECT
public:
	WebView(QWidget *parent = 0);
	virtual ~WebView();

public slots:
	void exitView();
	void goBack();
	void goForward();

signals:
	void closeWidget();

protected:
	bool event(QEvent*);

private:
	void loadWebContent();
	bool endTouchEvent(QTouchEvent*);
	void beginTouchEvent(QTouchEvent*);
	void swipeTriggered(QSwipeGesture*);

	QPushButton *m_exit;
	QTextBrowser *m_view;
	QVector<QString> m_content;

	int m_position;
	bool m_contentAvailable;
	QPointF m_touchBegin;
	QPointF m_touchEnd;
	bool m_tapEvent;
};

#endif /* WEBVIEW_H_ */
