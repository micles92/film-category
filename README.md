### README

#### Projekt: Film Service

**Film Service** to aplikacja odpowiedzialna za zarządzanie filmami oraz obsługę ich rankingów. System integruje się z zewnętrzną usługą **Digikat**, która dostarcza dane o rankingach oraz ocenach krytyków. Aplikacja wykorzystuje asynchroniczne komunikaty do obsługi zdarzeń związanych z dodawaniem i aktualizacją rankingów.

---

### **Flow aplikacji**

#### 1. **Dodawanie filmu**

1. Użytkownik inicjuje proces dodania filmu do systemu.
2. Usługa **Film** wysyła żądanie REST do usługi **Digikat**, aby pozyskać dane rankingowe i oceny krytyków:
    - **W przypadku braku odpowiedzi od usługi Digikat**:
        - Dane rankingowe oraz oceny krytyków nie są uzupełniane.
        - Film oraz ścieżka pliku są zapisywane w bazie danych.
    - **W przypadku powodzenia odpowiedzi od usługi Digikat**:
        - Usługa **Film** oblicza ranking na podstawie otrzymanych danych.
        - Film wraz z danymi rankingowymi i ocenami krytyków zostaje zapisany w bazie danych.

#### 2. **Dodanie rankingu w usłudze Digikat**

1. Podczas dodawania danych rankingowych do usługi **Digikat**:
    - Generowany jest event **AddRankingEvent**.
    - Event zostaje opublikowany na kolejce **add-ranking**.
2. Listener w usłudze **Film** odbiera event i oblicza ranking na podstawie danych zawartych w evencie.

#### 3. **Aktualizacja rankingu w usłudze Digikat**

1. Usługa **Digikat** inicjuje proces aktualizacji rankingu, publikując event **UpdateRankingEvent**.
2. Event jest wysyłany na kolejkę **update-ranking**.
3. Listener w usłudze **Film** odbiera event, odnajduje odpowiedni film w bazie danych, a następnie oblicza nowy ranking na podstawie ocen krytyków przesłanych w evencie.

---

### **Dane techniczne**

#### Usługa Film:
- **Port aplikacji**: `8080`
- **Swagger**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **H2 Console**: [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/)

#### Usługa Digikat:
- **Port aplikacji**: `9092`
- **Swagger**: [http://localhost:9092/swagger-ui/index.html](http://localhost:9092/swagger-ui/index.html)
- **H2 Console**: [http://localhost:9092/h2-console/](http://localhost:9092/h2-console/)

---

### **Uruchamianie aplikacji**

Przed uruchomieniem aplikacji należy:
1. Upewnić się, że plik `docker-compose.yml` został skonfigurowany poprawnie.
2. Uruchomić plik `docker-compose`, aby uruchomić obraz kolejki **RabbitMQ**:
   ```bash
   docker compose up -d
   ```

