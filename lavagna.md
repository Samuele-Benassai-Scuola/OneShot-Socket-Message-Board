

# 🧩 Lavagna Condivisa – Client/Server TCP in Java

## 🎯 Obiettivo
Realizzare un’applicazione **client/server TCP** in Java che permetta di gestire una **lavagna condivisa di messaggi testuali**.  
I client possono aggiungere, leggere e cancellare messaggi.  
Il server gestisce i dati condivisi e applica un **protocollo testuale** per la comunicazione.

---

## ⚙️ Descrizione generale
Il server mantiene una **lista di messaggi**, ognuno con un ID univoco, autore e testo.

I client si connettono tramite socket TCP e inviano comandi testuali.  
Il server interpreta i comandi e risponde in base al protocollo.

Per ogni comando, sono mandate N linee, fino ad un terminatore (riga vuota), o fino a che il comando non termina naturalmente (come per `LIST`).

---

## 🧾 Protocollo di comunicazione

### 2️⃣ Comandi disponibili

| Comando | Descrizione | Esempio |
|----------|--------------|----------|
| `ADD <user> "<testo 1>" "<test 2>" ...` | Aggiunge un messaggio alla lavagna | `ADD nicco "Oggi verifica di TPSIT" "Verifica alle 08:00"` |
| `LIST` | Mostra tutti i messaggi con ID e autore | `LIST` |
| `DEL <user> <id 1> <id 2> ...` | Cancella un messaggio se è dell’utente che l’ha scritto | `DEL nicco 2 5` |
| `QUIT` | Disconnette il client | `QUIT` |

### 3️⃣ Risposte del server

| Caso | Risposta |
|------|-----------|
| Messaggio aggiunto | `OK ADDED id` |
| Messaggio cancellato | `OK DELETED` |
| Gestione errori | `ERR <CODICE>` dove `<CODICE>` è uno tra `SYNTAX`, `UNKNOWNCMD`, `LOGINREQUIRED`, ecc. (vedi sezione **Gestione errori e casi limite**) |
| Disconnessione | `BYE` |

---

## 🧠 Gestione del comando LIST
Dopo `LIST`, il server invia:
```
BOARD:
[id] autore: testo
[id] autore: testo
...
END
```

Il client legge le righe una per una fino a ricevere `END`.

Esempio:
```
CLIENT → LIST
SERVER → BOARD:
SERVER → [1] Anna: Buongiorno
SERVER → [2] Marco: TPSIT alle 10:30
SERVER → END
```
Se la lavagna è vuota il server risponde comunque con:
```
BOARD:
END
```
Così il client sa che la lista è stata ricevuta e può tornare al prompt.

---

## 🧩 Struttura dati consigliata

Classe `Messaggio.java`:
```java
public class Messaggio {
    int id;
    String autore;
    String testo;

    public Messaggio(int id, String autore, String testo) {
        this.id = id;
        this.autore = autore;
        this.testo = testo;
    }
}
```

## 🧪Test

Per i test manuali è possibile usare `telnet`:

```
telnet localhost 12345
```

Ciò permette di provare rapidamente tutto il protocollo senza scrivere il client.

---

## 🧱 Regole di implementazione consigliate

- Usare comunicazione testuale basata su stream:
  ```java
  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
  ```
- Parsing dei comandi con `split(" ", 2)`.
- Utilizzare una lista condivisa:
  ```java
  ArrayList<Messaggio> lavagna = new ArrayList<>();
  ```
- ID autoincrementale per ogni messaggio.
- Il server gestisce **un client alla volta** nella versione base.
- (Opzionale) Variante multithread:
  ```java
  new Thread(new ClientHandler(socket)).start();
  ```
  con lista sincronizzata:
  ```java
  List<Messaggio> lavagna = Collections.synchronizedList(new ArrayList<>());
  ```

---

## 📚 Promemoria di sintassi utile (offline)
- **Divisione di una stringa in comando e argomento**  
  ```java
  String linea = in.readLine();            // esempio di input: "ADD Ciao a tutti"
  String[] parti = linea.split(" ", 2);    // parti[0] = "ADD", parti[1] = "Ciao a tutti"
  ```
  Il secondo parametro indica il numero massimo di segmenti; se non c'è testo aggiuntivo `parti.length` sarà 1.

- **Conversione da testo a intero con gestione errori**  
  ```java
  try {
      int id = Integer.parseInt(parti[1]);
  } catch (NumberFormatException ex) {
      // NumberFormatException se la stringa non rappresenta un intero valido
  }
  ```

---

## 🚧 Gestione errori e casi limite
Un errore può essere inviato in qualunque momento (anche non alla riga terminatore).
Quando inviato, la connessione termina.

- Comandi sconosciuti → `ERR UNKNOWNCMD`
- Sintassi incompleta (es. `ADD` senza testo, `DEL` senza ID numerico) → `ERR SYNTAX`
- Operazioni sulle risorse vuote (es. `LIST` su lavagna vuota) comunque restituiscono `BOARD:` seguito da `END`
- Login ripetuto o tentativo di azione prima del login → `ERR LOGINREQUIRED`
- Richiesta di cancellazione su ID inesistente → `ERR NOTFOUND`
- Cancellazione di messaggi non propri o qualunque azione vietata dal protocollo → `ERR PERMISSION`

---

## 🖥️ Estensioni opzionali proposte agli studenti
- **Server multiclient**: usare `Thread` dedicati per ogni connessione e proteggere la lista con `Collections.synchronizedList` o blocchi `synchronized` mirati.
- **Client CLI completo**: realizzare un piccolo programma a linea di comando che gestisca login, parsing dei messaggi e visualizzazione formattata della lavagna.

---

## 🧭 Criteri di valutazione
1. **Protocollo**: rispetto rigoroso di comandi, risposte e gestione errori.
2. **Robustezza**: nessun crash su input malformati, gestione corretta delle disconnessioni.
3. **Qualità del codice**: chiarezza delle classi, metodi ben separati, commenti essenziali.
4. **Estensioni** (bonus): multithreading funzionante o client CLI opzionale.
