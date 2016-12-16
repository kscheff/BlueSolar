## BlueSolar - Solar Computer mit Bluetooth Interface

Das Projekt wurde inspiertiert durch das www.nuggetforum.de und www.poesslforum.de. Das System besteht aus einer kleinen Hardware und passender Software für ein Smartphone. Alle wichtigen Informationen zu der Solaranlage werden aufgezeichnet und mit dem Smartphone dann angezeigt. Damit entfällt eine zusätzliche Anzeige und die Installation wird dadurch wesentlich vereinfacht.

![Bild](https://github.com/kscheff/BlueSolar/blob/master/FullSizeRender-4.jpg)

Weitere spezifische Informationen sind im Forum direkt hier zu finden:
* [Nuggetforum Thread](https://www.nuggetforum.de/forum/2-allgemeines/78722-solarcomputer-mit-bluetooth-im-eigenbau)
* [Pösslforum Thread](https://poesslforum.de/forum/elektrik/2705-solarcomputer-mit-bluetooth-im-eigenbau?start=30#34903)


### BlueSolar System

Das Sytem besteht aus einerm kleinen Gerät welches an dem Solarladeregler angeschlossen wird. Zum Anschluss werden nur 3 dünne Leitungen benötigt:
* +12 V der Batterie
* EBL Ausgang des Ladereglers
* Masse

![Bild](https://github.com/kscheff/BlueSolar/blob/master/FullSizeRender.jpg)

Der BlueSolar Computer erfasst die Spannung der Boardbatterie and den Ladestrom (via EBL) des Solarladereglers. Diese Daten werden dann kontinuierlich erfasst und daraus wichtige Kenndaten der Solaranlage errechnet.

Errechnet werden folgende Parameter:
* Momentane Spannung der Boardbatterie (Volt)
* Momentaner Ladestrom und Leistung (Ampere, Watt)
* Bisherige Tagesleistung (Ah, Wh)
* Tägliches Minimum and Maximum der Batteriespannung
* Tägliche maximale Solarleistung (Watt
* Tägliche Ladezeit (und Dunkelzeit) (Stunden:Minuten)

Zudem speichert BlueSolar täglich die wichtigsten Daten ab. Gespeichert werden mindestens die letzten 30 Tage (derzeit sind es 220 Tage).
* Ladeenergie (Wh)
* Maximale Solarleisting (Watt)
* Ladezeit (Stunden:Minuten)
* min/max Batteriespannung

