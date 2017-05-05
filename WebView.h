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

#define WEBVIEW_SEARCH_PATH		"/Users/pete/pages"

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
	void gestureEvent(QGestureEvent*);
	void swipeTriggered(QSwipeGesture*);

	QPushButton *m_exit;
	QPushButton *m_forward;
	QPushButton *m_back;
	QTextBrowser *m_view;
	QVector<QString> m_content;

	int m_position;
};

#endif /* WEBVIEW_H_ */
