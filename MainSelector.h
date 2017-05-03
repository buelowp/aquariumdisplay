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

protected slots:
	void showEvent(QShowEvent*);

private:
    QPushButton *m_fish;
    QPushButton *m_data;
    QLabel *m_fishLabel;
    QLabel *m_dataLabel;
    QGridLayout *m_layout;
};

#endif /* MAINSELECTOR_H_ */
