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
Sheet 3 3
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
L CP C1
U 1 1 59550B4B
P 7000 3950
F 0 "C1" H 7025 4050 50  0000 L CNN
F 1 "1000ohm" H 7025 3850 50  0000 L CNN
F 2 "Capacitors_THT:CP_Radial_Tantal_D10.5mm_P5.00mm" H 7038 3800 50  0001 C CNN
F 3 "" H 7000 3950 50  0001 C CNN
	1    7000 3950
	1    0    0    -1  
$EndComp
$Comp
L CONN_01X02 J8
U 1 1 59550D29
P 4750 2700
F 0 "J8" H 4750 2850 50  0000 C CNN
F 1 "UV_LED_STRIP" V 4850 2700 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-02_02x2.54mm_Straight" H 4750 2700 50  0001 C CNN
F 3 "" H 4750 2700 50  0001 C CNN
	1    4750 2700
	0    -1   -1   0   
$EndComp
$Comp
L R R10
U 1 1 59550DDE
P 6000 1650
F 0 "R10" V 6080 1650 50  0000 C CNN
F 1 "1k" V 6000 1650 50  0000 C CNN
F 2 "Resistors_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 5930 1650 50  0001 C CNN
F 3 "" H 6000 1650 50  0001 C CNN
	1    6000 1650
	1    0    0    -1  
$EndComp
Connection ~ 4800 3800
Connection ~ 4800 4100
Wire Wire Line
	6000 1800 6000 2400
Wire Wire Line
	6000 1500 7300 1500
Text GLabel 5350 2700 0    60   BiDi ~ 0
GND
Text GLabel 7600 4100 2    60   BiDi ~ 0
GND
Text GLabel 7300 3800 2    60   Output ~ 0
P5V_HAT
Text GLabel 7300 1500 2    60   Input ~ 0
UV_EN1
Wire Wire Line
	4100 4100 5500 4100
Connection ~ 4100 4000
Wire Wire Line
	4100 3900 4100 4100
Wire Wire Line
	3900 4000 4100 4000
$Comp
L BARREL_JACK J5
U 1 1 59550B1D
P 3600 3900
F 0 "J5" H 3600 4095 50  0000 C CNN
F 1 "12V" H 3600 3745 50  0000 C CNN
F 2 "Connectors:BARREL_JACK" H 3600 3900 50  0001 C CNN
F 3 "" H 3600 3900 50  0001 C CNN
	1    3600 3900
	1    0    0    -1  
$EndComp
Wire Wire Line
	3900 3900 4100 3900
Wire Wire Line
	3900 3800 5700 3800
$Comp
L CONN_01X02 J10
U 1 1 59552408
P 5900 3850
F 0 "J10" H 5900 4000 50  0000 C CNN
F 1 "12V_OUT" V 6000 3850 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-02_02x2.54mm_Straight" H 5900 3850 50  0001 C CNN
F 3 "" H 5900 3850 50  0001 C CNN
	1    5900 3850
	1    0    0    -1  
$EndComp
$Comp
L CONN_01X02 J7
U 1 1 5955246C
P 6400 3850
F 0 "J7" H 6400 4000 50  0000 C CNN
F 1 "5V_IN" V 6500 3850 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-02_02x2.54mm_Straight" H 6400 3850 50  0001 C CNN
F 3 "" H 6400 3850 50  0001 C CNN
	1    6400 3850
	-1   0    0    1   
$EndComp
Wire Wire Line
	5500 3900 5700 3900
Wire Wire Line
	5500 4100 5500 3900
$Comp
L 2N3904 Q3
U 1 1 59565062
P 6000 2600
F 0 "Q3" H 6200 2675 50  0000 L CNN
F 1 "2N3904" H 6200 2600 50  0000 L CNN
F 2 "TO_SOT_Packages_THT:TO-92_Molded_Narrow" H 6200 2525 50  0001 L CIN
F 3 "" H 6000 2600 50  0001 L CNN
	1    6000 2600
	0    1    1    0   
$EndComp
Wire Wire Line
	5350 2700 5800 2700
Wire Wire Line
	4900 3250 6600 3250
Wire Wire Line
	6600 3250 6600 2700
Wire Wire Line
	6600 2700 6200 2700
Wire Wire Line
	4900 3250 4900 2450
Wire Wire Line
	4900 2450 4450 2450
Wire Wire Line
	4450 2450 4450 2900
Wire Wire Line
	4450 2900 4700 2900
Wire Wire Line
	4800 2900 4800 3800
$Comp
L 2N3904 Q4
U 1 1 595D7721
P 8100 2600
F 0 "Q4" H 8300 2675 50  0000 L CNN
F 1 "2N3904" H 8300 2600 50  0000 L CNN
F 2 "TO_SOT_Packages_THT:TO-92_Molded_Narrow" H 8300 2525 50  0001 L CIN
F 3 "" H 8100 2600 50  0001 L CNN
	1    8100 2600
	0    1    1    0   
$EndComp
$Comp
L R R3
U 1 1 595D775E
P 8100 1750
F 0 "R3" V 8180 1750 50  0000 C CNN
F 1 "1k" V 8100 1750 50  0000 C CNN
F 2 "Resistors_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal" V 8030 1750 50  0001 C CNN
F 3 "" H 8100 1750 50  0001 C CNN
	1    8100 1750
	1    0    0    -1  
$EndComp
$Comp
L CONN_01X02 J14
U 1 1 595D7936
P 9450 2700
F 0 "J14" H 9450 2850 50  0000 C CNN
F 1 "UV_LED_STRIP" V 9550 2700 50  0000 C CNN
F 2 "Connectors_Molex:Molex_KK-6410-02_02x2.54mm_Straight" H 9450 2700 50  0001 C CNN
F 3 "" H 9450 2700 50  0001 C CNN
	1    9450 2700
	0    -1   -1   0   
$EndComp
Wire Wire Line
	4800 3400 9500 3400
Wire Wire Line
	9500 3400 9500 2900
Connection ~ 4800 3400
Wire Wire Line
	9400 2900 9400 3000
Wire Wire Line
	9400 3000 8600 3000
Wire Wire Line
	8600 3000 8600 2700
Wire Wire Line
	8600 2700 8300 2700
Wire Wire Line
	7900 2700 7400 2700
Wire Wire Line
	8100 2400 8100 1900
Wire Wire Line
	8100 1600 8100 1500
Wire Wire Line
	8100 1500 9200 1500
Text GLabel 9200 1500 2    60   Input ~ 0
UV_EN2
Text GLabel 7400 2700 0    60   BiDi ~ 0
GND
Wire Wire Line
	6600 3900 6700 3900
Wire Wire Line
	6700 3900 6700 4100
Wire Wire Line
	6700 4100 7600 4100
Connection ~ 7000 4100
Wire Wire Line
	6600 3800 7300 3800
Connection ~ 7000 3800
$EndSCHEMATC
