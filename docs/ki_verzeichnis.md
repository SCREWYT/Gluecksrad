---
title: KI-Verzeichnis & Quellen
nav_order: 6
---

## KI-Verzeichnis

## 1. Netzwerkanfrage ( Unter Settings_Activity.java )

# Prompt:

Erstelle mir eine Java-Klasse SettingsActivity für eine Android-App mit folgenden Anforderungen:

- Die Activity enthält einen Button „Netzwerkverbindung testen“ und ein TextView zur Anzeige des Ergebnisses.
- Beim Klick auf den Button soll eine HTTP-GET-Anfrage an http://10.0.2.2:8080/test gesendet werden.
- Die Anfrage wird in einem Hintergrundthread (AsyncTask) ausgeführt.
- Verwende HttpURLConnection für die Verbindung und BufferedReader + InputStreamReader, um die Serverantwort auszulesen.
- Setze StringBuilder ein, um die Antwort Zeile für Zeile zusammenzusetzen.
- Nach erfolgreicher Antwort soll das Ergebnis im TextView angezeigt und zusätzlich ein Toast eingeblendet werden.
- Bei einem Fehler (z. B. keine Verbindung) soll der Fehlertext ebenfalls angezeigt werden.
- Verwende sinnvolle Kommentare zu jedem Schritt im Code.

Quelle zum Chat: https://chatgpt.com/share/6857f7c9-6ab0-800b-8b72-d4de8f46f1ed

---

## 2. Feldauswahl: ( Unter Main_Activity.java )

# Prompt:

Schreibe mir in Java eine Methode getSectorUnderPointer, die für ein rotierbares Glücksrad mit gleichmäßig verteilten Feldern berechnet, auf welches Feld der Zeiger gerade zeigt.

Anforderungen:

Die Methode bekommt als Parameter den aktuellen Rotationswinkel des Glücksrads (float wheelRotation) und die Anzahl der Sektoren (int sectorCount)

Der Zeiger ist immer oben bei 180°

Die Methode soll die Rotation normalisieren (z. B. bei negativen Werten) und dann das korrekte Feld ermitteln

Der Rückgabewert ist ein int mit dem Index des getroffenen Felds (zwischen 0 und sectorCount - 1)

Kommentiere die wichtigsten Schritte im Code

Verwende zur Berechnung auch den Modulo-Operator %, um den Winkel zu begrenzen

Nutze float degreesPerSector = 360f / sectorCount; zur Aufteilung des Rads

Quelle zum Chat: https://chatgpt.com/share/6857fc4a-f364-800b-b0f8-504c819daff1

---

## 3. Platzierung der Felder auf dem Rad (Unter Main_Activity.java )

# Prompt:

Erstelle mir in Java eine Methode positionNumbersOnCircle, die TextView-Felder eines Glücksrads gleichmäßig im Kreis anordnet.

Anforderungen:

Die Methode verwendet ein FrameLayout (wheelContainer) als Mittelpunkt des Kreises

Die Felder (fields) sind in einem Array gespeichert und sollen im Kreis um das Zentrum verteilt werden

Der Radius ist wheelContainer.getWidth() / 2 - 150

Mittelpunkt des Kreises: centerX = wheelContainer.getWidth() / 2, centerY = wheelContainer.getHeight() / 2

Die Platzierung erfolgt über Polar-Koordinaten: angle = 2 * PI * i / fields.length - PI / 2

Für jedes TextView im Array:

Berechne x- und y-Koordinaten mit Math.cos() und Math.sin() (jeweils angepasst um die Breite/Höhe des Felds)

Setze die Position mit setX() und setY()

Zeige die passende Zahl aus einem spinValues-Array mit setText(...) an

Kommentiere die Schritte im Code

Quelle zum Chat: https://chatgpt.com/share/6857fd20-43cc-800b-800a-a34dac4b47b6




