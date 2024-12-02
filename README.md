# Human Activity Recognition mediante SensorTile.box PRO di STMicroelectronics (Backend)

## Descrizione del progetto
Il progetto Human Activity Recognition (HAR) implementa un sistema di riconoscimento delle attività 
umane basato su Edge Computing, con l'uso della SensorTile.box PRO di STMicroelectronics. Il sistema 
è in grado di rilevare in tempo reale attività come camminare, correre, fermarsi e guidare effettuando 
l'inferenza direttamente sulla SensorTile.box PRO e/o sullo smartphone sul quale si sta eseguendo 
l'applicazione. In particolare, all'interno di questa repository è presente il backend del progetto, 
realizzato mediante un'architettura a microservizi. Esso è composto da due microservizi:

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
