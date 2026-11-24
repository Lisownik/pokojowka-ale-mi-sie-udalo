# Konfiguracja smart LEDzika na esp32/rpi pico

## Wgranie firmware'u micro pythona
- [Micro Python Rpi pico](https://micropython.org/download/RPI_PICO/)

- Pobierz plik najnowszy plick micropythona .uf2

- Podłącz RPi pico do komputera

- Przeciągnij na dysk rpi pico pobrany plik

- Zainstalowany

## Przesłanie plików na urządzenie

### Wypełnij .env

Upewnij się, że plik [env.py](env.py) został poprawnie wypełniony według formatu pliku [env.sample.py](env.py)

### Upewnij się jakim interfejsem jest podłączone RPi pico

Prawdopodobnie będzie on wyglądał tak:

- Windows - COM4

- Linux i MacOS - /dev/ttyUSB0

### Pobierz mpremote

Używając środowiska pythona zainstaluj mpremote

``` bash
python -m venv .venv 
```

``` bash
source .venv/bin/activate
```

``` bash
pip install mpremote
```

mpremote powinno być zainstalowane

### Prześlij pliki
Dla interfejsu /dev/ttyUSB0
(Testowane tylko na linuxie :3)

#### plik main.py

``` bash
mpremote connect /dev/ttyUSB0 cp main.py :main.py
```

#### plik env.py
``` bash
mpremote connect /dev/ttyUSB0 cp env.py :env.py
```

#### plik data/id.txt
``` bash
mpremote connect /dev/ttyUSB0 mkdir data
mpremote connect /dev/ttyUSB0 cp data/id.txt :data/id.txt
```


## Uruchomienie programu
Program powinien automatycznie się uruchomić

Lecz dla podglądu możemy uruchomić go / monitorować output dwoma sposobami

``` 
mpremote connect /dev/ttyUSB0 
// tylko daje podgląd
```
``` 
mpremote connect /dev/ttyUSB0 run main.py
// uruchamia go ponownie i daje podgląd
```


Teraz main.py powinno być w autostarcie i działać poprawenie
