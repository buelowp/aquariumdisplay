/*
 * MainSelector.h
 *
 *  Created on: May 2, 2017
 *      Author: pete
 */

#ifndef MAINSELECTOR_H_
#define MAINSELECTOR_H_

#include <QtCore>
#include <QtWidgets>

class MainSelector : public QWidget {
	Q_OBJECT
public:
	MainSelector(QWidget *parent = 0);
	virtual ~MainSelector();
	void init();

protected slots:
	void showEvent(QShowEvent*);
	void paintEvent(QPaintEvent*);

	void fishButtonPressed();
	void dataButtonPressed();

signals:
	void activateFishDisplay();
	void activateDataDisplay();

private:
    QPushButton *m_fish;
    QPushButton *m_data;
    QLabel *m_fishLabel;
    QLabel *m_dataLabel;
};

#endif /* MAINSELECTOR_H_ */