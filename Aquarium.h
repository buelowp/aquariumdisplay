//
//  Aquarium.hpp
//  aquarium
//
//  Created by Peter Buelow on 5/2/17.
//
//

#ifndef Aquarium_hpp
#define Aquarium_hpp

#include <QtCore>
#include <QtWidgets>

#include <MainSelector.h>
#include <WebView.h>

class Aquarium : public QMainWindow
{
    Q_OBJECT
public:
    Aquarium();
    virtual ~Aquarium();

public slots:
	void activateFishDisplay();
	void activateDataDisplay();
	void closeFishDisplay();

protected:
    void showEvent(QShowEvent*);
    
private:
    MainSelector *m_primary;
    WebView *m_fishview;
};

#endif /* Aquarium_hpp */
