// BlueSolar_V107
// Version information needs to be reflexted to BLE Name
// BlueSolar 14.12.2016 (C) Kai Scheffer, Switzerland
//
// 08.04.2017 changed voltage multiplier back to 33337, for byte assignements removed % 256
// 04.04.2017 added Version to BLE Name
// 28.03.2017 averaging over 8 * 250ms and adjusted voltage multiplier
// 27.03.2017 V105 added log of Ah
// 14.12.2016 V103_1 implementation continues log with explicit clear

// table of fixed global variables used:
// A ADC Voltage mV
// B ADC Current mA
// C GATT NOTIFY of mV.mA (2 bytes each)
// D GATT READ day counter
// E GATT NOTIFY of dAh.Wh (2 bytes each)
// F 
// G 
// H 
// I
// J min voltage per day
// K max voltage per day
// L summed charge time per day in 2 sec
// M summed current each 2 sec
// N summed current in mAh per day
// O max PV per day
// P 
// Q summed watt each 2 sec
// R summed watt in mWh per day
// S 
// T GATT READ WRITE Ticks
// U() GATT READ read log data
// V
// W calculated Watts
// X
// Y 
// Z log read counter
//
// Timer 0: Data collection every 2 seconds
// Timer 1: One shot delay to generate log entry
// Timer 2: One shot delay for welcome message
// Timer 3: 

10 PINMODE P0(0) INPUT ADC 
20 PINMODE P0(1) INPUT ADC 
30 PINMODE P0(2) INPUT ADC 
40 PINMODE P0(3) INPUT ADC 
50 PINMODE P0(4) OUTPUT 
60 PINMODE P0(7) OUTPUT 
70 ANALOG REFERENCE, AVDD
80 ANALOG RESOLUTION, 14
90 CONFIG POWER, 0
// DCDC ON MEANS REGULATED 2.1V / off is 3.3V
// 1: On / 0: off
100 P0(7) = 0 
// init variables
200 J = 0XFFFF
210 DIM U(16)

// always start with an empty file "A"
280 OPEN 0, TRUNCATE "A"

//  setup BLE 
900 ADVERT GENERAL
910 ADVERT "105a84de-40bd-428b-bf06-698e5e422cd9"
920 ADVERT END 
930 SCAN NAME "BlueSolar_V107"
940 SCAN END

// setup GATT
//1000 GATT SERVICE "105a84de-40bd-428b-bf06-698e5e422cd9" ONCONNECT GOSUB 4000
1000 GATT SERVICE "105a84de-40bd-428b-bf06-698e5e422cd9"
1010 GATT CHARACTERISTIC "4B616901-40bd-428b-bf06-698e5e422cd9" "sec"
1020 GATT READ WRITE T
1050 GATT CHARACTERISTIC "4B616907-40bd-428b-bf06-698e5e422cd9" "log"
1060 GATT READ WRITE U ONREAD GOSUB 2000 ONWRITE GOSUB 2500
1090 GATT CHARACTERISTIC "4B616910-40bd-428b-bf06-698e5e422cd9" "mV.mA"
1100 GATT READ NOTIFY C
1130 GATT CHARACTERISTIC "4B616911-40bd-428b-bf06-698e5e422cd9" "dAh.Wh"
1140 GATT READ NOTIFY E
1190 GATT END


// schedule log timer and enter idle loop
1300 TIMER 0, 250 REPEAT GOSUB 1350
1310 DELAY 10000
1320 GOTO 1310
//------------- end --------------------------------

// aquire data 8x oversampling with average
1350 X = (X + 1) & 0x7
1360 A = A + P0(0) + 3
1370 B = B + P0(2) + 3
1380 IF X
1390  RETURN
1395 END

1396 A = (A * 33337) >> 16
1397 B = ((B * 33000) >> 16) - 10
// subtract 10 mA for our own consumption

// Aquire and process data
// if T rolls over a day it schedules a log entry
// T COUNTs in seconds
// A mV 14 BIT
// B mA 14 BIT
// L sum charge time in seconds
// J mV min Voltage
// K mV max Voltage
// M sum mAh per tick
// N sum mAh
// W mW
// O max mW
// Q sum mW per tick
// R sum mWh
// G 
// temp V(12) is used for scan data
1400 T = T + 2
// hardware v103 has external 18 KOhm / 2 KOhm voltage devider
// the external reference is a 3.3V nominal regulator
// data sheet CC2541 list ADC with negative -3 count offset and a 176 KOhm input resistance
// 2K || 176K = 1.9775280899 K 
// (18 + 1.9775) / 1.9775 * 3300 = 33337
//1405 A = (P0(0)+3) * 33337 / 8192
// EBL input uses 2KOhm resistor to GND and series resistance of 1KOhm
// The EBL interface says 1mA current for 20A charge
// so we get 2V per 20A
//1410 B = (P0(2)+3) * 33000 / 8192 
// LED on
1420 P0(4) = 1
// due to ADC offset ignore small currents < 45 mA
1430 IF (B < 45)
1440  B = 0
1450 END 
1460 IF (B > 20)
1470  L = L + 2
//1475 ELSE
//1476  G = G + 2
1480 END 
1490 IF (A < J)
1500  J = A
1510 END 
1520 IF (A > K)
1530  K = A
1540 END 
1550 M = M + B
1560 N = N + M / 1800
1570 M = M % 1800
1580 W = A * B / 1000
1590 IF (W > O)
1600  O = W
1610 END 
1620 Q = Q + W
1630 R = R + Q / 1800
1640 Q = Q % 1800
// send GATT NOTIFY values C and E
1650 C = (A << 16) | (B & 0XFFFF)
1660 E = ((N / 100) << 16) | ((R / 1000) & 0XFFFF)
1700 DIM U(12)
1710 U(0) = A >> 8
1720 U(1) = A
1730 U(2) = B >> 8
1740 U(3) = B
1750 U(4) =(N >> 24)
1760 U(5) =(N >> 16)
1770 U(6) =(N >> 8)
1780 U(7) = N
1790 U(8) =(R >> 24)
1800 U(9) =(R >> 16)
1810 U(10) =(R >> 8)
1820 U(11) = R
1830 SCAN CUSTOM "FF" U
1840 SCAN END
// LED OFF
1850 P0(4) = 0
1860 A = 0
1870 B = 0
// now we check to generate a log entry
1900 IF (T >= 86400)
//1905  G = 0
1910  T = T % 86400
1920  TIMER 1, 100 GOSUB 3000
1930 END
1999 RETURN 

// read log data GATT char.
// FILE #3 keeps to be open in order not to loose read position
2000 IF 0 = D
2010  RETURN
2020 END
2030 IF 0 = Z
2040  OPEN 3, READ "A"
2050 END
2060 READ #3, U(0), U(1), U(2), U(3), U(4), U(5), U(6), U(7), U(8), U(9), U(10), U(11), U(14), U(15)
2065 U(12) = D / 256
2066 U(13) = D
2070 Z = (Z + 1) % D
2080 RETURN

// Write to log clears the log file!
2500 OPEN 0, TRUNCATE "A"
2510 CLOSE 0
2520 D = 0
2530 Z = 0
2540 RETURN

// create log entry and reset
// consumption counters R, L
// and peak values O,J,K
// increase day counter D
// File "A" gets appended by log entry
// logging stops when Days > 180
3000 IF D > 180
3010  PRINT "Log full"
3020  RETURN
3030 END
// generate new log data
3040 DIM U(14)
3080 U(0) = R / 256000 
3090 U(1) = R / 1000
3100 R = 0
3110 U(2) = O / 256000
3120 U(3) = O / 1000
3130 O = 0
3140 U(4) = J / 256
3150 U(5) = J
3160 J = 0xFFFF
3170 U(6) = K / 256
3180 U(7) = K
3190 K = 0
3200 U(8) = L / 15360
3210 U(9) = L / 60
3220 U(10) = D / 256
3230 U(11) = D
3240 L = 0
3245 U(12) = N / 25600
3246 U(13) = N / 100
3247 N = 0
// write newest log entry
3250 OPEN 0, APPEND "A"
3260 WRITE # 0, U(0), U(1), U(2), U(3), U(4), U(5), U(6), U(7), U(8), U(9), U(10), U(11), U(12), U(13)
3360 CLOSE 0
// increase day counter
3415 PRINT "Log day: ", D  
3420 D = D + 1
3499 RETURN

// ONCONNECT reset log pointer for read
4000 Z = 0
4010 RETURN

