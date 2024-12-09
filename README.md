<a id="readme-top"></a>
<div align="center">
<h1>School Project MAT - Esame FBK</h1>
<i><h3>Progetto 2: Analisi chiamate app Spring Cloud.</h3></i>
</div>
<br>
<hr>


## Indice
1. [Richiesta Progetto](#project-request)
2. [Versioni (Da avere o installare) (**IMPORTANT!**)](#versions)
3. [Componenti](#components)
    - [Server](#server)
    - [Client](#client)
    - [Eureka Server](#eureka-server)
    - [Gateway](#gateway)
        - [Logs (**NEW!**)](#new-part-gateway)
    - [Order](#order)
    - [Catalog](#catalog)
    - [ApiCallSimulator (**NEW!**) ](#api-call-simulator)
4. [Collaboratori](#contributors)
5. [Docker e Kubernetes](#docker-kubernetes)


<hr>

<a id="project-request"></a>
## Richiesta Progetto :scroll:

> [!NOTE]
> Richiesta esame MAT FBK 2024.

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

<a id="versions"></a>
> [!CAUTION]
> **Versioni da installare (specifiche non versioni più nuove)** <br>
*Se avete versioni più nuove potete o fare un downgrade ad una versione precedente o impostare di default la versione più vecchia (scritta qua sotto).* <br>

**Le seguenti guide sono per ***Linux***** <br>
*SE usate **Windows** ci sono guide e **comandi diversi**, in caso cercate online per le versioni corrette (Occhio alle versioni che installate come scritto sopra)*<br>

**1. BACKEND**
- **Java 21**
    - https://www.oracle.com/it/java/technologies/downloads/#jdk21-linux
- **Maven**
#### 
    sudo apt-get install maven

**2. FRONTEND:**
- **Node Versione 18.20.4**
    - Download: [NodeJs V18.20.4](https://nodejs.org/dist/v18.20.4/node-v18.20.4-linux-x64.tar.xz)
    - Guida:
      - Step 1: Update System Repositories
        ####
            sudo apt update

      - Step 2: Install the package xz-utils
        ####
            sudo apt install xz-utils

      - Step 3: To Extract the .tar.xz file
        ####
            sudo tar -xvf node-v18.20.4-linux-x64.tar.xz

      - Step 4:
        ####
            sudo cp -r node-v18.20.4-linux-x64/{bin,include,lib,share} /usr/

      - Step 5: Check the node version
        ####
            node --version

- **Angular Versione 14.2.13** <br>
    - Installazione Angular ([Guida](https://www.npmjs.com/package/@angular/cli/v/14.2.13)):
      #### 
            npm i @angular/cli@14.2.13
    - Start Angular Frontend:
      ####
            ng serve

*Ve lo dirà sul terminale ma nel dubbio per vedere la homepage del progetto dovrete andare su localhost:4200*

<hr>

<a id="components"></a>
## Componenti :gear:

> [!IMPORTANT]
> Lista componenti e descrizione.

### Panoramica delle Componenti:
- [Configuration Server](#server): 
  - Gestisce configurazioni centralizzate per tutti i microservizi, memorizzate in un repository Git.
- [Configuration Client](#client): 
  - Si connette al server di configurazione per ottenere parametri di runtime, aggiornabili dinamicamente.
- [Eureka Server](#eureka-server): 
  - Fornisce il service discovery, consentendo ai microservizi di individuarsi reciprocamente senza conoscere in anticipo indirizzi o porte.
- [Gateway](#gateway): 
  - Punto di ingresso centrale per tutte le richieste client, responsabile del routing, resilienza (con Resilience4J), logging e gestione dei fallback.
- [Order](#order): 
  - Microservizio per la gestione degli ordini, con integrazione con il servizio Catalog per la disponibilità dei prodotti.
- [Catalog](#catalog): 
  - Microservizio per la gestione dei prodotti, inclusi elenchi, creazione, aggiornamento e ricerca avanzata.
- [ApiCallSimulator](#api-call-simulator): 
  - Simula richieste al Catalog e agli ordini per testare il comportamento dei microservizi sotto carico.


<a id="server"></a>
## Configuration Server :triangular_flag_on_post:
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
## Configuration Client :triangular_flag_on_post:
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

## Eureka Server :triangular_flag_on_post:
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
## Gateway :triangular_flag_on_post:
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
- **Integrazione con MongoDB:**
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
- **Campi principali:**
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

### Controller
    LogController
Lo scopo principale è gestire la restituzione dei log delle API memorizzati in MongoDB.<br>
Il controller è progettato per supportare il monitoraggio delle richieste API effettuate attraverso il gateway. Questa funzionalità è cruciale per la raccolta di metriche, debugging e auditing. Il frontend può consumare i dati esposti da /api/logs e visualizzarli in modo utile (ad esempio, rappresentazioni grafiche o dettagli per richieste individuali).
#### Analisi del Codice

1. Annotazioni principali:
    - @RestController: Marca questa classe come un controller Spring per API REST. I metodi all'interno restituiscono direttamente dati (tipicamente in formato JSON).
    - @RequestMapping("/api"): Specifica che tutti gli endpoint definiti in questa classe avranno un prefisso comune /api.
    - @CrossOrigin(origins = "http://localhost:4200"): Abilita le richieste Cross-Origin (CORS) da un'origine specifica, in questo caso il frontend in esecuzione su http://localhost:4200.
2. Dipendenza di MongoTemplate:
   - La classe usa MongoTemplate (autowired) per interagire con il database MongoDB.
   - **MongoTemplate** è un'astrazione fornita da Spring Data per eseguire operazioni di lettura/scrittura in MongoDB.
3. Endpoint /logs:
   - Annotato con @GetMapping("/logs"): Esso risponde a richieste HTTP GET sull'endpoint /api/logs.
   - Metodo getLogs(): Usa MongoTemplate.findAll(ApiLog.class) per recuperare tutti i documenti della collezione MongoDB che corrispondono alla classe modello ApiLog.
   - Ritorna una lista di oggetti ApiLog, che verranno automaticamente convertiti in JSON per essere restituiti al client.
4. Scopo principale:
   - Restituisce tutti i log registrati (oggetti ApiLog) nel database MongoDB.
   - Abilita il frontend (ad esempio un'app Angular, dato l'indirizzo localhost:4200) a consumare questi dati per visualizzarli, ad esempio, in tabelle o grafici.
<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="order"></a>
## Order :triangular_flag_on_post:

### Flusso Generale
- Un utente fa una richiesta per acquistare un prodotto tramite il OrderController.
- OrderServiceImpl riceve la richiesta e verifica la disponibilità del prodotto tramite Feign.
- Se il prodotto è disponibile, crea un nuovo ordine, lo salva nel database e aggiorna la disponibilità del prodotto nel servizio Catalog.
- Se qualcosa va storto durante la comunicazione con Catalog (ad esempio, il servizio è inaffidabile), il sistema si affida al circuit breaker per prevenire ulteriori errori e gestire il fallimento in modo controllato.

### 1. Configurazione di Order
Il modulo Order è configurato per gestire gli ordini degli utenti, interagendo con il servizio Catalog per verificare la disponibilità dei prodotti e aggiornare le quantità acquistate.

    OrderConfig
- Questa classe configura un RestTemplate che permette di fare chiamate HTTP ad altri microservizi (ad esempio, per interagire con il servizio Catalog).
- Load Balancing: Grazie all'annotazione @LoadBalanced, ogni richiesta inviata tramite il RestTemplate sarà automaticamente bilanciata tra le istanze disponibili dei microservizi.
        

        Resilience4JConfig
- Configura un circuit breaker e un time limiter per migliorare la resilienza delle chiamate verso i microservizi esterni (ad esempio, Catalog).
- Circuit Breaker: Gestisce i guasti in modo che, se una parte del sistema smette di rispondere correttamente, le chiamate future vengano interrotte, evitando ulteriori errori.
- Time Limiter: Impone un timeout nelle chiamate ai servizi esterni, garantendo che non ci siano chiamate che impiegano troppo tempo.

### 2.Controller degli Ordini
    OrderController
- **Questo controller gestisce le richieste HTTP relative agli ordini degli utenti.**
  - Get User Purchases (/purchases/{userId}): Restituisce la lista degli ordini di un determinato utente.
  - Get Purchase (/purchases/{userId}/{id}): Restituisce un ordine specifico dato l'ID dell'utente e dell'ordine.
  - Post Buy (/purchases/buy): Consente a un utente di acquistare un prodotto. La richiesta viene ricevuta come un OrderRequest contenente l'ID dell'utente, la quantità e l'ID del prodotto.

Discovery Client: Anche se non attivo (commentato), il controller fa uso di DiscoveryClient per interagire con il servizio Catalog tramite il meccanismo di Service Discovery di Spring Cloud (Eureka).

### 3.Modelli di Dati
    OrderRequest
Rappresenta la richiesta che l'utente invia per acquistare un prodotto. Contiene l'ID dell'utente, la quantità e l'ID del prodotto.
    
    Order
Rappresenta un ordine effettivo salvato nel database. Include informazioni come l'ID dell'ordine, l'ID del prodotto, il titolo del prodotto, la categoria, la quantità acquistata, il prezzo totale e l'ID dell'utente.

    Product (modello condiviso)
Rappresenta un prodotto del catalogo. Include attributi come ID, codice, titolo, categoria, descrizione, prezzo e disponibilità.

### 4.Repository
   
    OrderRepository
Interfaccia che estende MongoRepository per fornire metodi per interagire con il database MongoDB e recuperare gli ordini. Include metodi per trovare ordini in base all'ID dell'utente o una combinazione dell'ID utente e ID ordine.

### 5.Servizi
    OrderService (Interfaccia)
**Definisce i metodi necessari per la gestione degli ordini.** Include:
- getUserPurchases: Restituisce gli ordini di un utente.
- getPurchase: Restituisce un ordine specifico dato l'ID dell'utente e dell'ordine.
- buy: Consente di acquistare un prodotto, verificando la disponibilità e aggiornando il catalogo.


    OrderServiceImpl 
- Implementazione del servizio OrderService. Gestisce la logica effettiva dietro le operazioni sugli ordini:
  - Verifica la disponibilità del prodotto tramite una chiamata al servizio Catalog usando Feign (per evitare di scrivere manualmente la logica delle richieste HTTP).
  - Se il prodotto è disponibile, crea un nuovo ordine, lo salva nel database e aggiorna la disponibilità del prodotto nel catalogo.
- Circuit Breaker: Se la comunicazione con Catalog fallisce, il servizio può interrompere automaticamente le chiamate grazie al meccanismo di circuit breaker configurato precedentemente.
- Feign: Utilizza Feign per semplificare le chiamate a Catalog. Le richieste vengono inviate a Catalog per ottenere i dettagli del prodotto e aggiornare la sua disponibilità.

<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="catalog"></a>
## Catalog :triangular_flag_on_post:

Il microservizio Catalog è responsabile della gestione dei prodotti. Fornisce endpoint per elencare, creare, cercare e aggiornare le informazioni sui prodotti, incluso il loro livello di disponibilità. È collegato a un database MongoDB e utilizza Eureka per la registrazione e la scoperta dei servizi.
### Flusso Generale
**Elenco dei Prodotti:**
- Un client invia una richiesta GET /.
- ProductController chiama ProductService.getProducts(), che utilizza ProductRepository.findAll() per recuperare tutti i prodotti dal database.

**Creazione di un Prodotto:**
- Un client invia una richiesta POST / con un prodotto come payload JSON.
- ProductController chiama ProductService.createProduct(), che salva il prodotto nel database tramite ProductRepository.save().

**Aggiornamento della Disponibilità:**
- Un client invia una richiesta PUT /{id}/availability/{value}.
- ProductController chiama ProductService.updateAvailability(), che cerca il prodotto tramite il repository, aggiorna il campo availability, e lo salva.

**Ricerca Avanzata:**
- Le richieste GET /category/{category} e GET /code/{code} permettono di cercare prodotti specifici in base a categoria o codice, utilizzando metodi predefiniti del repository.

### 1. Configurazione del Microservizio
    CatalogApplication
**Classe principale del microservizio, dove viene avviata l'applicazione Spring Boot.**
   - @EnableDiscoveryClient: Permette al microservizio di registrarsi con Eureka per essere individuato da altri servizi.
   - Configura un bean per il client MongoDB con il supporto al tracing tramite Brave, che consente di tracciare le operazioni MongoDB per migliorare l'osservabilità.

### 2. Controller dei Prodotti
    ProductController
**Espone gli endpoint REST per interagire con i prodotti. Gli endpoint includono:**
   - GET /: Restituisce l'elenco di tutti i prodotti.
   - GET /{id}: Restituisce un prodotto specifico dato il suo ID.
   - GET /category/{category}: Cerca i prodotti in base alla categoria.
   - GET /code/{code}: Cerca un prodotto in base al suo codice univoco.
   - POST /: Crea un nuovo prodotto.
   - PUT /{id}/availability/{value}: Aggiorna la disponibilità di un prodotto dato il suo ID e il nuovo valore.
### 4. Repository
    ProductRepository
**Interfaccia che estende MongoRepository per interagire con il database MongoDB.**
   - Metodi principali:
     - findByCode(String code): Cerca un prodotto in base al suo codice.
     - findByCategory(String category): Cerca prodotti che appartengono a una specifica categoria.
### 5. Servizi
    ProductService (Interfaccia)
**Definisce i metodi necessari per la gestione dei prodotti, tra cui:**
- Elencare i prodotti.
- Cercare un prodotto per ID o codice.
- Creare un nuovo prodotto.
- Aggiornare la disponibilità di un prodotto.


    ProductServiceImpl
**Implementazione del servizio ProductService. Gestisce la logica aziendale per i prodotti.**
 Metodi principali:
 - getProducts: Restituisce tutti i prodotti presenti nel catalogo.
 - getProductById: Cerca un prodotto nel catalogo dato il suo ID.
 - getProductByCode: Cerca un prodotto in base al suo codice.
 - getProductsByCategory: Restituisce i prodotti appartenenti a una categoria specifica.
 - createProduct: Salva un nuovo prodotto nel database.
 - updateAvailability: Modifica la disponibilità di un prodotto. Se il prodotto non esiste, lancia un'eccezione.
### 6. Configurazione del Database
   #### MongoDB
   Utilizza MongoDB come database per memorizzare i prodotti.
   - La configurazione avviene tramite application.properties:
     - URI: Il collegamento al database è configurato tramite spring.data.mongodb.uri, che include credenziali per il database remoto.
     - Tracing: Con il supporto di Brave, le operazioni MongoDB vengono tracciate per migliorare l'osservabilità.



<p align="right">(<a href="#readme-top">back to top</a>)</p>
<hr>

<a id="api-call-simulator"></a>
## Api Call Simulator :triangular_flag_on_post:
**Il microservizio ApiCallSimulator simula richieste API verso due altri microservizi:**
- Catalog: Per simulare ricerche di prodotti.
- Order (o Purchase): Per simulare ordini di acquisto.<br>
L'obiettivo è testare il comportamento di questi servizi e monitorarne la risposta sotto carico simulato. Le chiamate vengono generate a intervalli casuali e possono essere interrotte dopo un certo periodo di tempo.

#### Modifica della durata della simulazione
La durata della simulazione può essere configurata modificando il parametro nel metodo startSimulation:
    
    simulator.startSimulation(1, TimeUnit.MINUTES);

### Flusso Generale
- Avvio del microservizio:
  - Quando l'applicazione viene avviata, il metodo CommandLineRunner.run crea un'istanza di ApiCallService.
  - La simulazione delle chiamate API inizia immediatamente e dura 1 minuto (configurabile).
  
- Simulazione delle richieste:
  - Catalog:
  Effettua una richiesta GET al servizio Catalog utilizzando un ID prodotto predefinito (1701).
  Stampa lo stato della risposta (es. 200 OK).
  - Order:
  Crea un oggetto OrderRequest con dettagli predefiniti sull'acquisto.
  Invia una richiesta POST al servizio Order per simulare una transazione.
  Stampa lo stato della risposta (es. 201 Created).
  
- Intervalli casuali:
  - Ogni chiamata API viene eseguita con un intervallo casuale tra 1 e 5 secondi, evitando richieste troppo ravvicinate.

- Arresto della simulazione:
  - Dopo il periodo definito (es. 1 minuto), tutte le richieste pianificate vengono fermate, e il simulatore viene arrestato.

### Servizio Principale
    ApiCallService
- Inizializzata con:
    - catalogUrl: URL per le chiamate al servizio Catalog.
    - purchaseUrl: URL per le chiamate al servizio Order.
    - RestTemplate: Per effettuare le richieste HTTP.
    - ScheduledExecutorService: Per pianificare l'esecuzione periodica delle chiamate API.
    - Random: Per generare intervalli casuali tra le chiamate.

#### Funzionalità principali:

1. Simulazione di una ricerca nel catalogo:
    - Invoca un endpoint di Catalog tramite una richiesta GET.
    - Esempio: GET /products/1701, dove 1701 è l'ID di un prodotto.

2. Simulazione di un acquisto:
  - Invoca un endpoint di Order tramite una richiesta POST.
  - Esempio:
    - Endpoint: /purchases/buy.
    - Payload: Un oggetto JSON che rappresenta un ordine, contenente:
    - userId: ID dell'utente che effettua l'acquisto.
    - count: Numero di prodotti da acquistare.
    - productId: ID del prodotto da acquistare.

3. Avvio della simulazione:
- Metodo startSimulation:
  - Avvia due tipi di attività schedulate:
    1. Ricerca nel catalogo: Eseguita a intervalli casuali.
    2. Acquisto di prodotti: Eseguito a intervalli casuali.
- La simulazione termina automaticamente dopo un periodo definito.

4. Arresto della simulazione:
- Metodo stopSimulation:
  - Arresta il pianificatore (Scheduler) e ferma tutte le attività in corso.

#### Punti chiave del codice:
- Gestione degli errori: Entrambi i metodi di simulazione (simulateCatalogSearch e simulatePurchase) catturano eventuali eccezioni e stampano un messaggio di errore nel log.
- Pianificazione parallela: Utilizza un pool di thread per eseguire simultaneamente richieste di ricerca e acquisto.

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


