TARGET = aquarium
TEMPLATE = app
QT += gui network widgets webenginewidgets
CONFIG += debug

MOC_DIR = .moc
OBJECTS_DIR = .obj

SOURCES = main.cpp \
        Aquarium.cpp \
	MainSelector.cpp \
	WebView.cpp \
	DataView.cpp

HEADERS = Aquarium.h \
	MainSelector.h \
	WebView.h \
	DataView.h

RESOURCES = aquarium.qrc


