# wot-MotionAI-Backend-MigliettaDurante

## Descrizione del progetto
Il progetto consiste in un backend per un'applicazione Android, composto da due microservizi:

- **authenticationservice**:  
  Microservizio responsabile della gestione della registrazione e del login degli utenti, nonché della gestione dei dati relativi al proprio account.

- **predictionservice**:  
  Microservizio che consente di:
    - Inserire il riconoscimento dell'attività effettuata dall'utente.
    - Ottenere una stima settimanale delle attività svolte, confrontandole con quelle della settimana precedente.
    - Eliminare tutti i riconoscimenti registrati.
    - Recuperare uno storico dei riconoscimenti effettuati, incluse le relative durate.

---

## Tecnologie utilizzate
- **Java**: linguaggio di programmazione impiegato per lo sviluppo del backend.
- **Spring Boot**: framework utilizzato per la realizzazione dei microservizi.
- **MongoDB**: database scelto per la memorizzazione dei dati relativi agli utenti.
- **Docker**: strumento per la containerizzazione dei microservizi.
- **Docker Compose**: strumento per la gestione e orchestrazione dei container.

---

## Requisiti
- **Java 11**: versione di Java necessaria per il funzionamento del progetto.
- **Maven**: strumento per la gestione delle dipendenze del progetto.
- **Docker**: indispensabile per la containerizzazione dei microservizi.

---

## Link al sito web della GitHub Page
[wot-MotionAI-Presentation-MigliettaDurante](https://unisalento-idalab-iotcourse-2023-2024.github.io/wot-MotionAI-Presentation-MigliettaDurante/)
