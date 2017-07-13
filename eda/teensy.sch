EESchema Schematic File Version 2
LIBS:power
LIBS:device
LIBS:transistors
LIBS:conn
LIBS:linear
LIBS:regul
LIBS:74xx
LIBS:cmos4000
LIBS:adc-dac
LIBS:memory
LIBS:xilinx
LIBS:microcontrollers
LIBS:dsp
LIBS:microchip
LIBS:analog_switches
LIBS:motorola
LIBS:texas
LIBS:intel
LIBS:audio
LIBS:interface
LIBS:digital-audio
LIBS:philips
LIBS:display
LIBS:cypress
LIBS:siliconi
LIBS:opto
LIBS:atmel
LIBS:contrib
LIBS:valves
LIBS:raspberrypi_hat
LIBS:teensy
LIBS:Aquarium-phat-cache
EELAYER 25 0
EELAYER END
$Descr A4 11693 8268
encoding utf-8
Sheet 2 3
Title ""
Date ""
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L Teensy3.1 MCU2
U 1 1 595422EA
P 5600 3200
F 0 "MCU2" H 5600 4700 60  0000 C CNN
F 1 "Teensy3.1" H 5600 1700 60  0000 C CNN
F 2 "teensy:Teensy30_31_32_LC" H 5600 2400 60  0001 C CNN
F 3 "" H 5600 2400 60  0000 C CNN
	1    5600 3200
	1    0    0    -1  
$EndComp
$Comp
L CONN_01X02 J4
U 1 1 595505C3
P 2000 4150
F 0 "J4" H 2000 4300 50  0000 C CNN
F 1 "WTRLVL" V 2100 4150 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-02_02x2.54mm_Straight" H 2000 4150 50  0001 C CNN
F 3 "" H 2000 4150 50  0001 C CNN
	1    2000 4150
	-1   0    0    1   
$EndComp
$Comp
L GND #PWR07
U 1 1 5955469D
P 4100 1900
F 0 "#PWR07" H 4100 1650 50  0001 C CNN
F 1 "GND" H 4100 1750 50  0000 C CNN
F 2 "" H 4100 1900 50  0001 C CNN
F 3 "" H 4100 1900 50  0001 C CNN
	1    4100 1900
	0    1    1    0   
$EndComp
Text GLabel 7400 4000 2    60   Input ~ 0
P5V_HAT
$Comp
L CONN_01X04 J12
U 1 1 595549B5
P 2000 3500
F 0 "J12" H 2000 3750 50  0000 C CNN
F 1 "APA102" V 2100 3500 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-04_04x2.54mm_Straight" H 2000 3500 50  0001 C CNN
F 3 "" H 2000 3500 50  0001 C CNN
	1    2000 3500
	-1   0    0    1   
$EndComp
$Comp
L CONN_01X03 J1
U 1 1 59555674
P 1450 2600
F 0 "J1" H 1450 2800 50  0000 C CNN
F 1 "DS18B20" V 1550 2600 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-03_03x2.54mm_Straight" H 1450 2600 50  0001 C CNN
F 3 "" H 1450 2600 50  0001 C CNN
	1    1450 2600
	-1   0    0    1   
$EndComp
$Comp
L CONN_01X03 J2
U 1 1 595556D2
P 1450 2150
F 0 "J2" H 1450 2350 50  0000 C CNN
F 1 "DS18B20" V 1550 2150 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-03_03x2.54mm_Straight" H 1450 2150 50  0001 C CNN
F 3 "" H 1450 2150 50  0001 C CNN
	1    1450 2150
	-1   0    0    1   
$EndComp
$Comp
L GND #PWR08
U 1 1 59555941
P 2650 3650
F 0 "#PWR08" H 2650 3400 50  0001 C CNN
F 1 "GND" H 2650 3500 50  0000 C CNN
F 2 "" H 2650 3650 50  0001 C CNN
F 3 "" H 2650 3650 50  0001 C CNN
	1    2650 3650
	1    0    0    -1  
$EndComp
Text GLabel 2700 3550 2    60   Input ~ 0
P5V_HAT
$Comp
L R R1
U 1 1 59555C9F
P 3100 4100
F 0 "R1" V 3180 4100 50  0000 C CNN
F 1 "750" V 3100 4100 50  0000 C CNN
F 2 "Resistors_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 3030 4100 50  0001 C CNN
F 3 "" H 3100 4100 50  0001 C CNN
	1    3100 4100
	0    1    1    0   
$EndComp
$Comp
L GND #PWR09
U 1 1 59555D77
P 3400 4500
F 0 "#PWR09" H 3400 4250 50  0001 C CNN
F 1 "GND" H 3400 4350 50  0000 C CNN
F 2 "" H 3400 4500 50  0001 C CNN
F 3 "" H 3400 4500 50  0001 C CNN
	1    3400 4500
	1    0    0    -1  
$EndComp
Text GLabel 2750 4400 3    60   Input ~ 0
P5V_HAT
$Comp
L R R2
U 1 1 59556243
P 3050 2450
F 0 "R2" V 3130 2450 50  0000 C CNN
F 1 "10k" V 3050 2450 50  0000 C CNN
F 2 "Resistors_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 2980 2450 50  0001 C CNN
F 3 "" H 3050 2450 50  0001 C CNN
	1    3050 2450
	-1   0    0    1   
$EndComp
Text GLabel 3050 1850 1    60   Input ~ 0
P5V_HAT
Text GLabel 2300 2700 2    60   Input ~ 0
P5V_HAT
Text GLabel 2300 2250 2    60   Input ~ 0
P5V_HAT
$Comp
L GND #PWR010
U 1 1 5955659C
P 2300 2050
F 0 "#PWR010" H 2300 1800 50  0001 C CNN
F 1 "GND" H 2300 1900 50  0000 C CNN
F 2 "" H 2300 2050 50  0001 C CNN
F 3 "" H 2300 2050 50  0001 C CNN
	1    2300 2050
	0    -1   -1   0   
$EndComp
$Comp
L GND #PWR011
U 1 1 595565F5
P 2300 2500
F 0 "#PWR011" H 2300 2250 50  0001 C CNN
F 1 "GND" H 2300 2350 50  0000 C CNN
F 2 "" H 2300 2500 50  0001 C CNN
F 3 "" H 2300 2500 50  0001 C CNN
	1    2300 2500
	0    -1   -1   0   
$EndComp
NoConn ~ 4500 2500
NoConn ~ 4500 2700
NoConn ~ 4500 2800
NoConn ~ 4500 2900
NoConn ~ 4500 3000
NoConn ~ 4500 3200
NoConn ~ 4500 3300
NoConn ~ 4500 3400
NoConn ~ 4500 3600
NoConn ~ 4500 3700
NoConn ~ 4500 4000
NoConn ~ 4500 4200
NoConn ~ 4500 4300
NoConn ~ 4500 4400
NoConn ~ 4500 4500
NoConn ~ 6700 1900
NoConn ~ 6700 2000
NoConn ~ 6700 2100
NoConn ~ 6700 2200
NoConn ~ 6700 2300
NoConn ~ 6700 2400
NoConn ~ 6700 2500
NoConn ~ 6700 2600
NoConn ~ 6700 2700
NoConn ~ 6700 2800
NoConn ~ 6700 2900
NoConn ~ 6700 3000
NoConn ~ 6700 3100
NoConn ~ 6700 3200
NoConn ~ 6700 3300
NoConn ~ 6700 3400
NoConn ~ 6700 3500
NoConn ~ 6700 3600
NoConn ~ 6700 3700
NoConn ~ 6700 3800
NoConn ~ 6700 3900
NoConn ~ 6700 4100
NoConn ~ 6700 4200
NoConn ~ 6700 4300
NoConn ~ 6700 4400
NoConn ~ 6700 4500
$Comp
L PWR_FLAG #FLG012
U 1 1 59566E83
P 8700 2200
F 0 "#FLG012" H 8700 2275 50  0001 C CNN
F 1 "PWR_FLAG" H 8700 2350 50  0000 C CNN
F 2 "" H 8700 2200 50  0001 C CNN
F 3 "" H 8700 2200 50  0001 C CNN
	1    8700 2200
	1    0    0    -1  
$EndComp
Text GLabel 8700 2700 3    60   Input ~ 0
P5V_HAT
Text GLabel 3900 3500 0    60   BiDi ~ 0
GND
Text GLabel 3900 2100 0    60   Output ~ 0
RX
Text GLabel 3900 2000 0    60   Input ~ 0
TX
Wire Wire Line
	4500 1900 4100 1900
Wire Wire Line
	4500 3500 3900 3500
Wire Wire Line
	6700 4000 7400 4000
Wire Wire Line
	3400 3100 4500 3100
Wire Wire Line
	4500 3800 3900 3800
Wire Wire Line
	4500 2000 3900 2000
Wire Wire Line
	4500 2100 3900 2100
Wire Wire Line
	3400 3100 3400 3350
Wire Wire Line
	3900 3800 3900 3650
Wire Wire Line
	2200 4100 2950 4100
Wire Wire Line
	2500 4100 2500 3900
Wire Wire Line
	2500 3900 4500 3900
Connection ~ 2500 4100
Wire Wire Line
	2200 4200 2750 4200
Wire Wire Line
	2750 4200 2750 4400
Wire Wire Line
	1650 2700 2300 2700
Wire Wire Line
	1650 2600 3650 2600
Wire Wire Line
	1650 2500 2300 2500
Wire Wire Line
	1650 2250 2300 2250
Wire Wire Line
	1650 2150 2900 2150
Connection ~ 3050 2600
Wire Wire Line
	1650 2050 2300 2050
Wire Wire Line
	2900 2150 2900 2600
Connection ~ 2900 2600
Wire Wire Line
	3050 2300 3050 1850
Wire Wire Line
	8700 2200 8700 2700
Wire Wire Line
	4500 2200 3900 2200
Text GLabel 3900 2400 0    60   Output ~ 0
UV_EN1
Wire Wire Line
	3400 3350 2200 3350
Wire Wire Line
	3900 3650 3400 3650
Wire Wire Line
	3400 3650 3400 3450
Wire Wire Line
	3400 3450 2200 3450
Wire Wire Line
	2700 3550 2200 3550
Wire Wire Line
	2650 3650 2200 3650
Wire Wire Line
	3250 4100 3400 4100
Wire Wire Line
	3400 4100 3400 4500
Wire Wire Line
	4500 4100 3950 4100
Text Label 3950 4100 0    60   ~ 0
1WIRE
Text Label 3650 2600 2    60   ~ 0
1WIRE
$Comp
L CONN_01X02 J11
U 1 1 595D312D
P 3900 6600
F 0 "J11" H 3900 6750 50  0000 C CNN
F 1 "PUMP" V 4000 6600 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-02_02x2.54mm_Straight" H 3900 6600 50  0001 C CNN
F 3 "" H 3900 6600 50  0001 C CNN
	1    3900 6600
	0    1    1    0   
$EndComp
$Comp
L CONN_01X02 J13
U 1 1 595D31AC
P 4550 6600
F 0 "J13" H 4550 6750 50  0000 C CNN
F 1 "HEATER" V 4650 6600 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-02_02x2.54mm_Straight" H 4550 6600 50  0001 C CNN
F 3 "" H 4550 6600 50  0001 C CNN
	1    4550 6600
	0    1    1    0   
$EndComp
Wire Wire Line
	4500 6400 4500 5200
Wire Wire Line
	4600 5200 4600 6400
Wire Wire Line
	3950 6400 3950 5200
$Comp
L GND #PWR013
U 1 1 595D3482
P 3950 5200
F 0 "#PWR013" H 3950 4950 50  0001 C CNN
F 1 "GND" H 3950 5050 50  0000 C CNN
F 2 "" H 3950 5200 50  0001 C CNN
F 3 "" H 3950 5200 50  0001 C CNN
	1    3950 5200
	0    -1   -1   0   
$EndComp
$Comp
L GND #PWR014
U 1 1 595D34AC
P 4500 5200
F 0 "#PWR014" H 4500 4950 50  0001 C CNN
F 1 "GND" H 4500 5050 50  0000 C CNN
F 2 "" H 4500 5200 50  0001 C CNN
F 3 "" H 4500 5200 50  0001 C CNN
	1    4500 5200
	0    1    1    0   
$EndComp
Wire Wire Line
	4500 2300 3900 2300
Wire Wire Line
	4500 2400 3900 2400
Text Label 3850 5200 3    60   ~ 0
PUMP_EN
Text Label 4600 5200 0    60   ~ 0
HTR_EN
Text Label 3900 2200 0    60   ~ 0
HTR_EN
Text Label 3900 2300 0    60   ~ 0
PUMP_EN
Wire Wire Line
	3850 5200 3850 6400
Wire Wire Line
	4300 2600 4500 2600
Wire Wire Line
	4300 2600 4300 2800
Wire Wire Line
	4300 2800 3900 2800
Text GLabel 3900 2800 0    60   Output ~ 0
UV_EN2
$EndSCHEMATC