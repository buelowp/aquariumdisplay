TARGET = aquarium
TEMPLATE = app
QT += gui network widgets
CONFIG += debug

MOC_DIR = .moc
OBJECTS_DIR = .obj

SOURCES = main.cpp \
        Aquarium.cpp \
	MainSelector.cpp

HEADERS = Aquarium.h \
	MainSelector.h

RESOURCES = aquarium.qrc


