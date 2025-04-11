# SmartBank-ATM-System


Projekt neve: SmartBank ATM System

1. Egyedtípusok (Entities):

Felhasználó (User)

ID (Long, automatikus)

Név (String)

E-mail (String, egyedi)

Jelszó (String, titkosított)

Számlák listája (kapcsolat a BankAccount entitással)

Bankszámla (BankAccount)

ID (Long, automatikus)

Számlaszám (String, egyedi)

Egyenleg (BigDecimal)

Tulajdonos (kapcsolat a User entitással)

Tranzakciók listája (kapcsolat a Transaction entitással)

Tranzakció (Transaction)

ID (Long, automatikus)

Típus (ENUM: DEPOSIT, WITHDRAWAL, TRANSFER)

Összeg (BigDecimal)

Dátum és idő (LocalDateTime)

Kapcsolat a BankAccount entitással

2. Funkcionalitások:

Felhasználói regisztráció és bejelentkezés (Spring Security)

Számlanyitás és számlalistázás

ATM műveletek:

Egyenleglekérdezés

Pénzfelvétel

Pénzbefizetés

Átutalás másik számlára

Tranzakciók listázása

CI/CD integráció GitHub Actions segítségével

3. Technológiai stack:

Backend: Java, Spring Boot (Spring Data JPA, Spring Security, Spring Web)

Adatbázis: PostgreSQL

Build eszköz: Maven

Tesztelés: JUnit 5, Mockito, Jacoco

CI/CD: GitHub Actions, Docker, Artifactory

4. Alapvető REST API végpontok:

Felhasználók kezelése

POST /api/users/register - Regisztráció

POST /api/users/login - Bejelentkezés

Bankszámla műveletek

GET /api/accounts - Felhasználó összes számlájának lekérése

POST /api/accounts - Új bankszámla létrehozása

ATM műveletek

GET /api/accounts/{id}/balance - Egyenleglekérdezés

POST /api/accounts/{id}/deposit - Befizetés

POST /api/accounts/{id}/withdraw - Készpénzfelvétel

POST /api/accounts/transfer - Átutalás

Tranzakciók kezelése

GET /api/transactions/{accountId} - Adott bankszámla tranzakcióinak lekérése

5. Minőségi követelmények:

Verziókövetés folyamatos commitokkal (GitHub)

CI/CD pipeline beállítása (build, teszt, deploy)

Kódminőség biztosítása (Checkstyle)

Legalább 60%-os tesztlefedettség a service rétegben

6. Kiegészítő lehetőségek (haladó funkciók):

Kétlépcsős azonosítás (2FA)

Websocket alapú értesítések a tranzakciókról

Admin felület a tranzakciók nyomon követésére

Ez a dokumentáció egy jól strukturált beadandót segít összeállítani, amely megfelel az elvárásoknak.

