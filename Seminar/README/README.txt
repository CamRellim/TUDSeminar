Big Data Regression 02/07/2017

INFORMATIONEN ZU DEN REGRESSIONEN
----------------------------------
Das Programm berechnet kontinuierlich zwei verschiedene
Regressionen. Die 'Batch-Regression' berechnet in einem von
dem Benutzer festgelegten Zeitintervall eine Regression
auf dem gesamten Datenbestand. Die zweite Regression
beginnt mit der Berechnung einer Regression anhand der von dem
Benutzer gewählten Anzahl der zuletzt eingetroffenen
Bestellungen. Diese fließt anschließend gewichtet in die
'Batch-Regression' ein und passt diese fortlaufend bis zu
ihrer nächsten Berechnung an.
Der simulierte Datenstream besteht aus eingehenden
Bestellungen eines Online-Händlers. Eine Bestellung besteht
aus den Preisen und Mengen verschiedener Produktkategorien.
Das Ziel der Regression ist es dem Händler eine Übersicht zu
geben, welche Kategorien aktuell mehr und welche weniger von
den Kunden gekauft werden. Somit wird der durchschnittliche 
Warenkorbwert auf die Kosten der jeweiligen Produktkategorien
regressiert. Der Betreiber des Online-Handels kann an den
einzelnen Faktoren erkennen, wieviel ein Produkt aus den
gewählten Kategorien durschnittlich einbringt.


BEDIENUNG DER BENUTZEROBERFLÄCHE
---------------------------------
Der Button 'Start Streaming' startet den simulierten Datenstrom
sowie die Berechnung der 'Batch-Regression' und der
'Adjusted-Regression'. Dieser Vorgang kann zu jeder Zeit durch
ein erneutes Betätigen dieses Buttons, welcher nun die Aufschrift
'Stop Streaming' trägt, beendet werden.
Durch das Klicken des Buttons 'Settings' ist es dem Benutzer
möglich die Anzahl der zu betrachtenden Bestellungen (ORDER_SIZE)
für die 'Adjusted-Regression' zu verändern. Zudem ist es möglich
Einstellungen bezüglich des Zeitintervalls der Berchnung der
'Batch-Regression' vorzunehmen. Die Ergebnisse der beiden
Regressionen werden ausgegeben über  die Felder 'Batch Regression
Params' für die Regression auf dem gesamten Datenbestand und
'Adjusted Regression Params' für die kontinuierlich angepasste
Regression.


DATENBANKZUGRIFF
-----------------
Der Zugriff auf die in der MongoDB gespeicherten Daten ist
über den Start der Windows Eingabeaufforderung im '/bin'
Ordner der MongoDB möglich. Zunächst wird mit dem Befehl
'mongo' eine MongoDB Shell gestartet, anschließend mit
'use TUDSeminar' die benötige Datenbank ausgewählt und
zuletzt durch 'db.orders.find()' auf die Elemente in der
Collection 'orders' zugegriffen.

BACK-END INFORMATIONEN
-----------------------
Die Big-Data-Verarbeitung erfolgt durch Apache Kafka sowie
Apache Zookeeper. Der simulierte Datenstream wird mittels
einem Kafka-Producer erzeugt, welcher diese Daten an die
Topic 'orders' sendet. Ein Kafka Consumer liest diese 
Daten aus der Topic und führt anschließend die oben
erklärten Regressionen durch.



=======================================================
Kontakt:
Yannick Pferr: Yannick@Pferr.de
Ludwig Koch: Ludwig-Koch@gmx.net