# Konfiguracja pokojówki

## Docker


### Update apt
`sudo apt update`
`sudo apt upgrade`

### Instalacja Dockera
`sudo apt install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin`

#### Test Dockera
`sudo docker run hello-world`

### Serwis Dockera
`sudo systemctl enable docker`
`sudo systemctl start docker`

## Zmienne środowiskowe

### Zmień zmnienne w [compose.yaml](./compose.yaml)

## Kontener Pokojówki

### Zbudowanie Image'a Pokojówki
`sudo docker build .`

### Utworzenie Kontenera
`sudo docker compose up -d`

### Sprawdzenie Kontenera
`sudo docker ps`

#### Sprawdź, czy nie ma errorów

#### Wszystko powinno działać