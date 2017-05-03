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

protected slots:
	void updateTemps();

private:
	QMap<QString, QFile> m_probes;
	QMap<QString, QLabel> m_probe;
	QMap<QString, QLabel> m_probeLabel;
	QTimer *m_updateTemps;
};

#endif /* DATAVIEW_H_ */
