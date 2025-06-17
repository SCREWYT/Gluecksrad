package com.example.gluecksrad.util;

import java.io.BufferedReader;           // Zum effizienten Zeilen-lesen von Text (z. B. Serverantwort)
import java.io.InputStreamReader;       // Wandelt den Datenstrom (Bytes) in lesbaren Text (Zeichen)
import java.net.HttpURLConnection;     // Stellt eine Verbindung zu einem Webserver her (HTTP)
import java.net.URL;                  // Repräsentiert eine Webadresse (z. B. "http://10.0.2.2:8080/")

public class NetworkUtil {

     // Holt Text-Daten von einer angegebenen URL (z. B. vom lokalen Server)
    // Static sorgt hier dafür, dass von überall drauf zugegriffen werden kann, ohne ein Util Objekt zu erzeugen
    public static String getDummyDataFromServer(String urlString) { // Einfach die Webadresse als Text quasi
        StringBuilder result = new StringBuilder(); // Container, der Zeichen schnell aneinanderhängt

        try { // Bei Fehlern springt das Programm in den catch Block unten
            URL url = new URL(urlString); // Macht aus dem String eine echte URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Öffnet eine Verbindung
            conn.setRequestMethod("GET"); // Sagt: Wir wollen Daten lesen (nicht senden)

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()) // Antwort lesen
            );

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line); // Antwort aufbauen
            }
            reader.close();
        } catch (Exception e) {
            result.append("Fehler: ").append(e.getMessage()); // Fehler abfangen
        }

        return result.toString(); // Ergebnis zurückgeben
    }
}
