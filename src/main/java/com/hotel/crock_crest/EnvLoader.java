package com.hotel.crock_crest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    public static void load() {
        // Percorso del file .env nella directory radice del progetto
        String envPath = ".env";

        try (BufferedReader reader = new BufferedReader(new FileReader(envPath))) {
            Properties properties = new Properties();
            String line;
            while ((line = reader.readLine()) != null) {
                // Salta righe vuote o commenti
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                // Dividi la riga in chiave e valore
                int separatorIndex = line.indexOf('=');
                if (separatorIndex > 0) {
                    String key = line.substring(0, separatorIndex).trim();
                    String value = line.substring(separatorIndex + 1).trim();
                    // Imposta la proprietà di sistema
                    System.setProperty(key, value);
                    System.out.println("Loaded system property: " + key);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load .env file. Running with default configurations.");
            // Non lanciare un'eccezione critica, l'applicazione può usare
            // configurazioni alternative o fallire più tardi se necessario.
        }
    }

}
