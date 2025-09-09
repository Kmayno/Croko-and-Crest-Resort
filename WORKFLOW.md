# Documentazione del Flusso di Lavoro - Croko & Crest Resort

Ciao! Questo documento è stato creato per aiutarti a capire come funziona il progetto. Non preoccuparti se alcuni concetti sono nuovi, qui troverai una spiegazione semplice e chiara.

## 1. Architettura Generale

Il progetto è diviso in due parti principali:

-   **Frontend:** È il sito web con cui l'utente interagisce. È costruito con HTML, CSS e JavaScript e si trova nella cartella `CrokoFront/`. Quando avvii l'applicazione, Spring Boot rende queste pagine accessibili dal browser.
-   **Backend:** È il "cervello" dell'applicazione, costruito con **Spring Boot**. Gestisce tutte le operazioni complesse come salvare le prenotazioni, verificare i dati e comunicare con il database. Espone delle "API" che il frontend può chiamare.
-   **Database:** È dove vengono salvati tutti i dati in modo permanente (informazioni sui clienti, sulle camere, sulle prenotazioni, ecc.).

Il flusso è questo: l'**utente** usa il **frontend** -> il **frontend** chiama il **backend** per chiedere o salvare dati -> il **backend** parla con il **database**.

## 2. Struttura del Backend (Spring Boot)

All'interno di `src/main/java/com/hotel/crock_crest`, noterai 4 cartelle principali. Ecco a cosa servono:

### `model`
Contiene le "classi modello" che rappresentano i dati della nostra applicazione.
-   **Entities** (es. `Cliente.java`, `Camera.java`, `Prenotazione.java`): Sono la rappresentazione Java delle tabelle del database. Ogni oggetto di queste classi corrisponde a una riga in una tabella.
-   **DTO (Data Transfer Object)** (es. `PrenotazioneRequest.java`): Sono oggetti speciali usati per trasferire dati tra il frontend e il backend in un formato pulito e sicuro. Ad esempio, quando fai una richiesta di prenotazione, i dati JSON vengono impacchettati in un oggetto `PrenotazioneRequest`.

### `repository`
Contiene le "interfacce repository" (es. `ClienteRepository.java`, `CamereRepository.java`).
Queste interfacce sono un po' magiche: definisci solo i metodi (es. `findById`, `findAll`) e Spring Data JPA scrive automaticamente il codice per parlare con il database. Il loro unico scopo è leggere e scrivere dati dal/nel database.

### `service`
Contiene le "classi di servizio" (es. `PrenotazioneService.java`).
Questa è la parte più importante, dove si trova la **logica di business**. I servizi orchestrano le operazioni. Ad esempio, il `PrenotazioneService`:
-   Riceve i dati dal controller.
-   Usa i repository per verificare se un cliente o una camera esistono.
-   Controlla se una camera è disponibile in certe date.
-   Calcola il prezzo totale.
-   Dice al repository di salvare la nuova prenotazione.

### `controller`
Contiene le "classi controller" (es. `ControllerPrenotazione.java`).
I controller sono la porta d'ingresso del backend. Definiscono gli URL (endpoint) dell'API che il frontend può chiamare.
-   Gestiscono le richieste HTTP in arrivo (es. `GET`, `POST`, `PUT`).
-   Prendono i dati dalla richiesta (es. il JSON di una nuova prenotazione).
-   Chiamano il metodo di servizio appropriato per eseguire l'azione richiesta.
-   Restituiscono una risposta al frontend (spesso in formato JSON).

---

## 3. Flusso di Creazione di una Nuova Prenotazione

Vediamo passo dopo passo cosa succede quando un utente prenota una camera, come nel caso che stavi provando.

**Endpoint:** `POST /api/prenotazioni`

1.  **Frontend (Utente):** L'utente compila il modulo di prenotazione sul sito e clicca "Prenota".
2.  **Frontend (JavaScript):** Il codice JavaScript della pagina raccoglie i dati del modulo, li trasforma in un oggetto JSON e invia una richiesta HTTP `POST` all'URL del backend: `http://localhost:8080/api/prenotazioni`.
3.  **Backend (Controller):**
    -   Il `ControllerPrenotazione` riceve la richiesta.
    -   Grazie all'annotazione `@PostMapping`, sa di dover eseguire il metodo `creaPrenotazione`.
    -   Spring Boot converte automaticamente il JSON del corpo della richiesta in un oggetto Java di tipo `PrenotazioneRequest`.
4.  **Backend (Service):**
    -   Il controller chiama il `prenotazioneService` passandogli l'oggetto `PrenotazioneRequest`.
    -   Il servizio inizia la sua logica:
        -   **Validazione 1:** Controlla che le date siano valide (es. la data di inizio non è dopo la data di fine).
        -   **Validazione 2:** Chiama il `clienteService` per chiedere se il cliente con l'ID specificato esiste. Il `clienteService` a sua volta usa il `clienteRepository` per cercarlo nel database.
        -   **Validazione 3:** Fa la stessa cosa per la camera con il `camereService` e il `camereRepository`.
        -   **Validazione 4:** Controlla se la camera è effettivamente disponibile per le date richieste (chiama il metodo `verificaDisponibilita`).
        -   **Calcolo:** Se tutto è valido, calcola il prezzo totale della prenotazione.
5.  **Backend (Repository):**
    -   Il `prenotazioneService` crea un nuovo oggetto `Prenotazione` con tutti i dati.
    -   Chiama il metodo `save()` del `prenotazioneRepository`, passandogli il nuovo oggetto. Il repository traduce questa operazione in un comando SQL `INSERT` e salva la nuova prenotazione nel database.
6.  **Ritorno della Risposta:**
    -   Il repository restituisce l'oggetto `Prenotazione` appena salvato (ora con un ID univoco) al servizio.
    -   Il servizio lo restituisce al controller.
    -   Il controller impacchetta la risposta in un oggetto `PrenotazioneResponse`, imposta lo stato HTTP a `201 Created` (che significa "risorsa creata con successo") e la invia come risposta al frontend.
7.  **Frontend (Feedback):** Il JavaScript del frontend riceve la risposta positiva e mostra all'utente un messaggio di conferma.

### Diagramma di Flusso Testuale

Ecco una rappresentazione visuale del flusso:

```
[Frontend]                                [Backend]                                                     [Database]
    |                                           |                                                          |
Utente compila form                             |                                                          |
e clicca "Prenota"                              |                                                          |
    |---------------------------------------->  |                                                          |
    |                                  POST /api/prenotazioni                                              |
    |                                           |                                                          |
    |                                  [ControllerPrenotazione]                                            |
    |                                  (Metodo creaPrenotazione)                                           |
    |                                           |                                                          |
    |                                  [PrenotazioneService]                                               |
    |                                           |                                                          |
    |                                  - Valida i dati                                                     |
    |                                  - Chiama ClienteService -> ClienteRepository -> (Cerca cliente)     |
    |                                  - Chiama CamereService  -> CamereRepository  -> (Cerca camera)      |
    |                                  - Verifica disponibilità                                            |
    |                                  - Calcola prezzo                                                    |
    |                                  - Salva prenotazione --> PrenotazioneRepository -> (Salva dati)     |
    |                                           |                                                          |
    |                                  Restituisce la prenotazione salvata                                 |
    |<---------------------------------------   |                                                          |
    |                                  Risposta HTTP 201 Created                                           |
    |                                           |                                                          |
Mostra conferma all'utente                      |                                                          |
    |                                           |                                                          |

```

Spero che questa guida ti sia d'aiuto! Salvala e consultala ogni volta che hai un dubbio. Se qualcosa non è chiaro, chiedi pure.
