---
title: Architektur & Aufbau
nav_order: 3
---

# Architektur & Projektstruktur

Dieses Projekt ist eine Java-Anwendung mit grafischer Benutzeroberfläche (GUI), SQLite-Datenbank, Netzwerkkommunikation und modularem Aufbau. Es orientiert sich am DAO-Muster und verwendet mehrere klar getrennte Pakete für die jeweiligen Aufgabenbereiche.

---

## 📁 Projektstruktur

Die Anwendung ist in verschiedene Module und Klassen aufgeteilt:

| Paket/Ordner | Inhalt                                                                                                         |
|--------------|----------------------------------------------------------------------------------------------------------------|
| `database`   |` DBHelper`,` ScoreDao`, `UserDao`
| `model`      | Datenmodelle wie `User`                                                                                        |
| `ui`         | GUI-Komponenten wie `LoginActivity`, `MainActivity`, `RegisterAcivity`, `SettingsActivity`, `UpgradesActivity` ||                                                                |

---

## 🧱 Aufbau der Anwendung (Layer-Architektur)

Das Projekt folgt dem Prinzip der **logischen Schichtung**, um Wiederverwendbarkeit und Wartbarkeit zu fördern:

GUI
↓
Anwendungslogik (z. B. Punktberechnung, Upgrades)
↓
DAO-Schicht (ScoreDAO, UserDAO)
↓
SQLite-Datenbank (lokal auf Gerät)


---

## 🧩 DAO-Struktur

Für jede Datenstruktur existiert eine zugehörige DAO-Klasse:

- `ScoreDAO` – Verwalten des Punktestands (Punkte können nicht negativ werden)
- `UserDAO` – Registrierung und Login-Validierung
- `DatabaseHelper` – Stellt Verbindung zur SQLite-Datenbank her und initialisiert sie

---

## 🌐 Netzwerkkommunikation

Für die Netzwerkkomponente wird `HttpURLConnection` genutzt. Diese Funktion wird im `SettingsActivity` getestet.

**Ablauf:**

1. Der Nutzer klickt auf „Verbindung testen“
2. Es wird eine GET-Anfrage an einen lokalen Server oder eine feste URL gesendet
3. Die Antwort wird verarbeitet und angezeigt
4. Fehler (z. B. keine Verbindung) werden per `try-catch` abgefangen

---

## 🖥️ Benutzeroberfläche (GUI)

Die App enthält folgende grafische Oberflächen (Screens):

- `LoginActivity` – Registrierung und Login
- `MainActivity` – Glücksrad, aktueller Punktestand, Drehbutton
- `UpgradesActivity` – Anzeige und Kauf von Upgrades
- `SettingsActivity` – Test der Netzwerkverbindung, App-Infos

---

## 🔁 Ablauf: Beispiel Nutzeraktion „Rad drehen“

1. Nutzer klickt auf den „Drehen“-Button
2. Zufälliges Feld wird ausgewählt
3. Punktwert wird mit Upgrade-Multiplikator multipliziert (nur bei positiven Feldern)
4. Neue Punktzahl wird in Datenbank gespeichert (`ScoreDAO`)
5. GUI zeigt aktuellen Punktestand und Feldname an

---

## 🧠 Besondere Logik

- **Multiplikator-Upgrade:** Erhöht Punkte auf positiven Feldern. Startet bei 1.0 und erhöht sich um 0.1 pro Kauf.
- **Feldwert-Upgrade:** Verstärkt alle Felder: positive Felder werden positiver, negative Felder negativer.
- Beide Upgrades werden teurer (Kosten steigen um 10% bei jedem Kauf).
- Der Punktestand kann niemals negativ werden.

---

## ✅ Fazit

Die Architektur ist modular, klar getrennt und erfüllt die Anforderungen:

- GUI
- SQLite-Datenbank mit DAO-Muster
- Netzwerkfunktion
- Saubere Fehlerbehandlung
- Trennung von Daten, Logik und Darstellung


