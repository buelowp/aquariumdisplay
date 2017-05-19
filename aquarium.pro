TARGET = aquarium
TEMPLATE = app
QT += gui network widgets
CONFIG += debug c++11

INCLUDEPATH = /usr/local/include /usr/local/include/FastLED
LIBS += -L/usr/local/lib -lfastled -lsunset

MOC_DIR = .moc
OBJECTS_DIR = .obj

SOURCES = main.cpp \
        Aquarium.cpp \
	MainSelector.cpp \
	WebView.cpp \
	DataView.cpp \
	Lights.cpp

HEADERS = Aquarium.h \
	MainSelector.h \
	WebView.h \
	DataView.h \
	Lights.h

RESOURCES = aquarium.qrc


