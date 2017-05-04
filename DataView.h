/*
 * DataView.h
 *
 *  Created on: May 3, 2017
 *      Author: pete
 */

#ifndef DATAVIEW_H_
#define DATAVIEW_H_

#include <QtCore>
#include <QtWidgets>

class DataView : public QWidget {
	Q_OBJECT
public:
	DataView(QWidget *parent = 0);
	virtual ~DataView();

	bool setTempDeviceName(QString);
	bool setPumpDevice(QString);

signals:
	void closeWidget();

protected:
	void paintEvent(QPaintEvent*);
	bool event(QEvent*);

protected slots:
	void updateDevices();
	void exitButtonActivated();
	void pumpToggled();

private:
	void updateTemps();
	bool updatePumpState();
	void gestureEvent(QGestureEvent*);
	void swipeTriggered(QSwipeGesture*);

	QMap<QString, QString> m_probes;
	QMap<QString, QLabel*> m_probe;
	QMap<QString, QLabel*> m_probeLabel;
	QTimer *m_updateTemps;
	QHBoxLayout *m_layout;
	QLabel *m_pumpLabel;
	QPushButton *m_exit;
	QPushButton *m_pump;
	QFile *m_pumpGpio;
	int m_widgetIndex;
	bool m_isMetric;
};

#endif /* DATAVIEW_H_ */
