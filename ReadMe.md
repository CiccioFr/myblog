# Prog. MyBlog

- Inizializzato da [Spring Initializr](https://start.spring.io/)
- Dipendenze:
    1. `spring web` - org.springframework.boot:spring-boot-starter-web
       - org.springframework.boot:spring-boot-starter-test7 (insieme a starter-web per default)
    2. `spring data jpa` - org.springframework.boot:spring-boot-starter-data-jpa
    3. `mysql Driver` - com.mysql:mysql-connector-j
    4. `lombok` - org.projectlombok:lombok
    5. `validation` - org.springframework.boot:spring-boot-starter-validation
- Struttura
- controller
- entity
- payload
  - request
  - response
- repository
- service

  ---
  8. dto - Data Transfer Object
  9. dao - Data Access Object 
10. 
---
la scatola più interna 
1. entità
2. repository - interpreta


4. controller - richiama i metodi nel Service
   - nel controller eseguo un controllo sul valore
   - una sorta di riassunto
category chiama repository
- se qualcosa va male, vediamo i log a partire dalla scatola più esterna (controller)
  - il log più utile è l'ultimo log (il la scatola più interna)


al token 2 cose devo fare
- se è valido (corrotto / se riesco a leggerlo) -> è malformed
- verifico se scaduto
- se utente nel frattempo non è stato disabilitato

## il logger si può mettere nello yaml 
- ma indica il livello del log