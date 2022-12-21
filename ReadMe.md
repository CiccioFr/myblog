# Prog. MyBlog

## Inizializzato da [Spring Initializr](https://start.spring.io/)

## Dipendenze:
    1. `spring web`
    2. `spring data jpa`
    3. `mysql Driver`
    4. `lombok`
    5. `validation`

## Struttura
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
1. Entity
2. Repository | Response
  - Response: per query personalizzate anche con join
    - usare JPQL oppure l’SQL nativo per effettuare le query.
  - Repository: per interrogare il DB con query classiche
3. Service
  - logica di business
    - trattare il risultato delle query create nel repository
4. Controller - richiama i metodi nel Service
   - nel controller eseguo un controllo sul valore
   - una sorta di riassunto

---
- se qualcosa va male, vediamo i log a partire dalla scatola più esterna (controller)
  - il log più utile è l'ultimo log (il la scatola più interna)


al token 2 cose devo fare
- se è valido (corrotto / se riesco a leggerlo) -> è malformed
- verifico se scaduto
- se utente nel frattempo non è stato disabilitato

## il logger si può mettere nello yaml 
- ma indica il livello del log
## primo post
"title": "LIBRERIE DEL PORTOGALLO: 3 LIBRERIE DA NON PERDERE FRA LISBONA E PORTO",
"overview": "Leggere è da sempre una delle mie più grandi passioni! Prima di partire per una nuova destinazione mi piace leggere libri che parlano del Paese che visiteremo e delle città che conosceremo, degli usi, i costumi, la storia e le tradizioni. ",
"content": "Lo sapevi che Lisbona è la città con il maggior numero di librerie al mondo? E che ospita la libreria in attività più antica del mondo? Due primati in un colpo solo per la capitale lusitana che ci hanno lasciato a bocca aperta! La libreria in questione è la Livraria Non tutti sanno però che la Livraria Bertrand non è sempre stata dove la vediamo ora. Il palazzo originale andò distrutto durante il terremoto che nel 1755 rase al suolo gran parte del centro storico di Lisbona e da quel momento la sede fu trasferita in Rue Garrett. Fu Peter Faure a ereditare il negozio dai Fratelli Bertrand, ancora oggi una delle attrazioni imperdibili di Lisbona; uno dei luoghi più instagrammabili e ricchi di fascino della città, un’icona impreziosita dalla facciata decorata da azulejos, le piastrelle colorate tipiche dei palazzi portoghesi, che conserva molte delle caratteristiche originarie. Soprattutto nella sala iniziale con libri antichi, alti scaffali e pareti rivestite in legno dove regna un silenzio che incute rispetto. Se durante il tour a caccia delle più belle librerie del Portogallo ti trovi a passare per Lisbona e la Livraria Bertrand ricorda di fare sosta al caffè A Brasileira, in Rue Garrett n° 122, il luogo preferito dallo scrittore e poeta portoghese Fernando Pessoa, per un caffè e un pasteis de nata."

