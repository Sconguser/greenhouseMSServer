Projekt jest PRYWATNY, został udostępniony publicznie na czas rekrutacji do pracy

Greenhouse to projekt studencki, który ma na celu umożliwienie obsługiwania "smart szklarni" w Makerspace@UW na wydziale fizyki. Szklarnie będą fizycznie stworzone i postawione w budynku Makerspace.
Ten projekt to serwer, obsługujący zapytania wysyłane z aplikacji mobilnej lub webowej. Jego zadaniem jest walidacja zapytań, obsługa bazy danych oraz przekierowywanie zapytań do RaspberryPi położonych w szklarniach oraz w drugą stronę.

Aby uruchomić projekt wymagana jest baza postgresql o nazwie greenhouse

Wymagane zmienne środowiskowe to:
JWT_SECRET_KEY - klucz do generowania tokenów JWT
JWT_EXPIRATION_TIME - czas ważności tokenów JWT w milisekundach
FIREBASE_SETTINGS - nazwa pliku .json z konfiguracją FCM