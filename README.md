<a id="readme-top"></a>
<div align="center">
<h1>School Project MAT - Esame FBK</h1>
<i><h3>Progetto 2: Analisi chiamate app Spring Cloud.</h3></i>
</div>
<br>
<hr>


## Indice
1. [Richiesta Progetto](#project-request)
2. [Componenti](#components)
    - [Server](#server)
    - [Client](#client)
    - [Eureka Server](#eureka-server)
    - [Gateway](#gateway)
    - [Order](#order)
    - [Catalog](#catalog)
3. [Collaboratori](#contributors)
4. [Docker e Kubernetes](#docker-kubernetes)


<hr>

<a id="project-request"></a>
## Richiesta Progetto
### Baseline
- Progetto “shop” realizzato durante le lezioni che contiene due servizi, “shop-catalog” e
  “shop-purchase”.
- Componenti infrastrutturali di Spring Cloud:
  o Registry
  o Gateway
  o Distributed tracing.

### Attività
1. **Simulatore chiamate API**
- Creare uno strumento (es., in Java) che genera chiamate multiple alle API dell’app (ricerca,
  acquisto) durante un periodo del tempo predefinito (ad esempio, per 2 ore). Strumento
  deve permettere le distribuzioni diverse del carico: con intervalli randomizzati, con modello
  preimpostato (esempio, carico basso intenso all’inizio, poi carico alto, poi carico medio).
2. **Estensione proxy per analisi dati**
- Creare filtri all’interno di Proxy per abilitare monitoraggio ed analisi delle chiamate API.
- Salvare gli eventi necessari per analisi usando file di log o DB o strumento esterno.
3. **Visualizzazione dei dati di analisi**
- Creare una (o piu’ dashboard) per rappresentare le informazioni su utilizzo delle API. In
  particolare,
  o Distribuzione delle chiamate per il periodo monitorato per ogni metodo API
  o Istogrammi delle distribuzioni per vari intervalli (es., per ore di giorno se il periodo
  di analisi copre più giorni)
  o Confronto di utilizzo delle chiamate diverse (diversi API)
  o Distribuzione del tempo di risposta per vari intervalli per diversi API

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="components"></a>
## Componenti

<a id="server"></a>
### Configuration Server
Il ConfigurationServer è un'applicazione che fornisce la configurazione centralizzata per altri microservizi. Questa configurazione viene recuperata da un repository Git e può includere proprietà come URL, credenziali e parametri personalizzati.

#### Codice e Funzionalità Principali
1. **ConfigurationServiceApplication** Annotato con: <br>
   - **@SpringBootApplication:** Abilita le funzionalità Spring Boot.
   - **@EnableConfigServer:** Trasforma l'applicazione in un Spring Cloud Config Server.
   - **Metodo main:** Avvia il server Config.
2. **SecurityConfig** <br>
   Configura la sicurezza HTTP con:
   Basic Authentication e form-based login.
   Esenzioni per endpoint specifici (/encrypt/, /decrypt/) dalla protezione CSRF.
   Le credenziali di accesso sono definite nei file di configurazione "application.properties"
3. **Crittografia:**<br>
Utilizza un file **config-server.jks** per proteggere le configurazioni sensibili con un keystore.
4. **Flusso di Funzionamento**<br>
Il Config Server legge le configurazioni dal repository Git configurato (https://github.com/MauroZn/config-repo).<br>
I microservizi client si connettono a questo server per recuperare le loro configurazioni.

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="client"></a>
### Configuration Client
Il ConfigurationClient è un'applicazione che si collega al ConfigurationServer per ottenere configurazioni centralizzate. Consente di accedere dinamicamente ai valori delle configurazioni memorizzati nel server Config.

#### Codice e Funzionalità Principali
1. **ConfigurationClientApplication**<br>
   Annotato con:
   - **@SpringBootApplication:** Avvia l'applicazione Spring Boot.
   - **@RestController:** Rende la classe capace di gestire richieste HTTP.<br>
   Include un endpoint semplice (es. /role) per testare l'accesso ai valori configurati.
2. **TestController**<br>
   Annotato con:
   - **@RestController:** Permette di definire endpoint REST.
   - **@RefreshScope:** Consente di aggiornare dinamicamente le configurazioni quando il server Config viene aggiornato.<br>
   
   Usa l'annotazione @Value per leggere configurazioni dal ConfigurationServer:
   - **user.role:** Ruolo dell'utente con un valore predefinito guest.
   - **user.password:** Password dell'utente (deve essere definita nel repository Config).<br>
   
   Espone l'**endpoint /role**:
   - Risponde con un messaggio che include i valori di user.role e user.password.

#### Flusso di Funzionamento
- All'avvio, il client si connette al ConfigurationServer specificato in spring.config.import.
- Recupera le configurazioni centralizzate corrispondenti al proprio nome (config-client) e profilo attivo (development).
- Questi valori sono accessibili tramite @Value nelle classi Java.
- L'endpoint /role restituisce un messaggio che mostra i valori recuperati dal server Config.

#### Funzionalità di Aggiornamento Dinamico
Grazie a @RefreshScope, le configurazioni possono essere aggiornate senza riavviare il client.
Usando l'endpoint /actuator/refresh, le configurazioni vengono ricaricate dinamicamente dal server Config.

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="eureka-server"></a>
### Eureka Server
Il ShopEurekaServer è un'applicazione Spring Boot che funge da Eureka Server, una componente chiave di Spring Cloud Netflix per il servizio di Service Discovery. 
Gestisce la registrazione e il monitoraggio dei microservizi, consentendo loro di comunicare tra loro dinamicamente senza conoscere in anticipo le rispettive posizioni (indirizzi IP o porte).

#### Funzionalità di ShopEurekaServer
1. **Service Discovery:**
   - I microservizi client (come Order, Catalog, etc.) si registrano con questo server, specificando il proprio nome e posizione.
   - Il server mantiene un registro aggiornato dei servizi attivi e delle loro posizioni.
2. **Interfaccia Web:**
   - Il server Eureka espone una dashboard web accessibile su http://localhost:8761.
   - Mostra lo stato dei servizi registrati, tra cui:
     - Nome dei servizi.
     - Indirizzi e porte.
     - Stato (UP, DOWN, etc.).

3. **Gestione della Comunicazione:**
- I microservizi non devono conoscere le posizioni reciproche.
- Possono scoprire gli altri servizi tramite richieste al server Eureka.

#### Flusso di Funzionamento
1. **Avvio:**  
    - Il server Eureka si avvia sulla porta configurata (8761).
2. **Registrazione dei Client:**
   - I microservizi (ad esempio, Order o Catalog) si registrano usando eureka.client.service-url.defaultZone.
3. **Discovery:**
   - I microservizi possono interrogare il server Eureka per scoprire altri servizi attivi, semplificando la comunicazione tra le componenti.

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="gateway"></a>
### Gateway
Il ShopGateway è un'applicazione Spring Cloud Gateway che funge da punto centrale di ingresso per tutte le richieste client verso i microservizi. 
Fornisce funzionalità di routing, resilienza, logging, e gestione del traffico.

### 1. Configurazioni
    Resilience4JConfig
Questo file configura la resilienza delle chiamate tramite Resilience4J.

- **Circuit Breaker Config:**
  - failureRateThreshold(50): Se il 50% delle richieste fallisce, il circuito si apre.
  - waitDurationInOpenState(Duration.ofMillis(1000)): Tempo di attesa prima di provare a ripristinare il circuito.
  - slidingWindowSize(2): Utilizza un buffer di due richieste per calcolare il tasso di errore.

- **Time Limiter Config:**
  - timeoutDuration(Duration.ofSeconds(4)): Le richieste che superano i 4 secondi scatenano un timeout.
  
Questo garantisce che il gateway sia in grado di gestire errori temporanei nei servizi sottostanti e di proteggere i clienti da ritardi prolungati.

#### Funzionalità
- Integra Resilience4J per il controllo di resilienza.
- Migliora la tolleranza ai guasti dei microservizi downstream.

### 2. Controller
    FallbackController
Gestisce i fallback per i servizi non disponibili.
- Endpoint /fallback-catalog
  - Restituisce un messaggio JSON quando il servizio Catalog non è raggiungibile.
  - Output:


    {
    "message": "We regret to inform service catalog is currently unavailable. please try again later"
    }

### 3. Funzionalità del Gateway
- Routing:
    - Reindirizza le richieste verso i microservizi downstream (Order, Catalog).
- Resilienza:
  - Utilizza Resilience4J per gestire errori temporanei e limiti di tempo.
- Fallback:
  - Fornisce endpoint di fallback quando i servizi non sono disponibili.
- Logging:
  - Registra informazioni diagnostiche nel database MongoDB (quando configurato).
- Service Discovery:
  - Interroga il server Eureka per determinare le posizioni dei microservizi.

### 4. Punti Importanti
- Il gateway agisce da API Gateway, aggregando più microservizi dietro un unico punto d'accesso.
- Le configurazioni di resilienza e fallback migliorano l'affidabilità complessiva dell'architettura.
- La connessione MongoDB supporta l'analisi delle richieste per il monitoraggio e l'auditing.

<a id="new-part-gateway"></a>
## 5. Nuove Aggiunte:
> [!IMPORTANT]
> Novità rispetto al codice base.

### Configurazioni
    GatewayConfig
Questo file di configurazione definisce le regole di routing e i filtri per l'applicazione Spring Cloud Gateway.
#### Caratteristiche principali:
- **Definizioni di rotte:**
  - **catalog:** Reindirizza le richieste con il percorso /products/** al servizio catalog.
    - Aggiunge un circuit breaker con un URI di fallback (/fallback-catalog).
    - Utilizza il filtro stripPrefix per rimuovere il primo segmento del percorso URI.
  - **order:** Reindirizza le richieste con il percorso /purchases/** al servizio order.
    - Applica il filtro stripPrefix.

- **Load Balancing:**
  - Le rotte utilizzano URI con prefisso lb://, permettendo l'integrazione con Eureka per il service discovery.


    LoggingFilter

Questo filtro globale consente di registrare i dettagli delle richieste e delle risposte, con l'ulteriore funzionalità di salvare i log in un database MongoDB.
#### Caratteristiche principali:
- **Registrazione dei log:**
    - Registra il percorso della richiesta in ingresso e la durata dell'elaborazione.
    - Registra eventuali errori, includendo il percorso e il messaggio di errore.
- Integrazione con MongoDB:
  - Salva i log nella collezione logs di MongoDB.
  - Ogni log include:
    - Il percorso della richiesta.
    - Il codice di stato della risposta.
    - Il tempo di risposta (in millisecondi).
    - Il timestamp dell'evento.
  - Ordine di esecuzione:
    - Il filtro viene eseguito tra i primi grazie al metodo getOrder() che restituisce -1.

### Models
    ApiLog
Questa classe rappresenta il modello per i log salvati in MongoDB.
#### Dettagli:
- Annotata con @Document, specifica che i documenti saranno memorizzati nella collezione logs.
- Campi principali:
  - id: Identificativo univoco del log.
  - requestPath: Il percorso della richiesta HTTP.
  - statusCode: Codice di stato della risposta.
  - responseTime: Tempo impiegato per l'elaborazione della richiesta.
  - timestamp: Timestamp automatico della creazione del log.

### Repository
    ApiLogRepository
Questa interfaccia è un repository MongoDB per la gestione dei documenti di tipo ApiLog.
#### Caratteristiche:
- Estende MongoRepository, fornendo operazioni CRUD pronte all'uso.
- Consente la persistenza e il recupero dei log salvati.

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="order"></a>
### Order



<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="catalog"></a>
### Catalog

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="api-call-simulator"></a>
### Api Call Simulator

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="contributors"></a>
## Contributors
- Mauro Zanotti
- Elton Bakia
- Francesco Bonomi
- Alessio Tomasini

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="docker-kubernetes"></a>
## Docker e Kubernetes

Guida Installazione Docker SOLO su LINUX: https://docs.docker.com/engine/install/ubuntu/ 

Guida creazione Dockerfile per una singola App: https://spring.io/guides/gs/spring-boot-docker

Guida Kubernetes: https://kubernetes.io/docs/concepts/

Guida minikube (Local Kubernetes): https://minikube.sigs.k8s.io/docs/start/?arch=%2Flinux%2Fx86-64%2Fstable%2Fbinary+download

Nel folder backend da terminale, scrivere: "docker compose up --build" per far partire docker compose.
Per fermarlo: docker compose down <br>
(Docker non c'è ancora in questo progetto)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


