# Déduplication de Fichiers avec CDC

## 👤️ Authors 👤

- Nicolas Marsan ([@NicoooM](https://github.com/NicoooM))<br />
- Benoît Favrie ([@benoitfvr](https://github.com/benoitfvr))<br />
- Julian Laballe ([@Triips-TheCoder](https://github.com/Triips-TheCoder))<br />
- Lucas ([@lucasboucher](https://github.com/lucasboucher))<br />
- Paul Mazeau ([@PaulMazeau](https://github.com/PaulMazeau))

## Description
Ce projet implémente un système de déduplication de fichiers utilisant la technique du Content-Defined Chunking (CDC). Il permet de stocker efficacement les fichiers en découpant les données en morceaux uniques et en évitant les duplications.

## Fonctionnalités
- Découpage intelligent des fichiers en chunks
- Compression des chunks avec Zstd
- Stockage des chunks dans une base de données SQLite  
- Reconstruction des fichiers à partir des chunks stockés

## Prérequis
- Java 17
- Maven
- SQLite

## Installation

## Cloner le repo
```bash
git clone https://github.com/NicoooM/hetic-file-chunking-java.git
```

## Aller dans le dossier
```bash
cd hetic-file-chunking-java
```

## Installer les dépendances
```bash
mvn install
```

## Lancer l'application
```bash
./run.sh
```

## Tests

### Lancer les tests
```bash
mvn test
```

## Notes de développement
Le choix de la taille de fenêtre (48 bytes) et des tailles min/max des chunks (64/128 bytes) a été fait après plusieurs tests empiriques. C'est un bon compromis entre performance et efficacité de déduplication.

L'utilisation de Zstd pour la compression était un choix évident - il offre un excellent ratio compression/vitesse comparé à d'autres algorithmes comme LZ4.

TODO: Ajouter un système de cache pour éviter de recompresser les chunks fréquemment utilisés.

## Structure du projet
Le projet suit une architecture simple et modulaire :
- SimpleCDC.java : Logique de découpage
- DatabaseManager.java : Gestion de la base de données
- Reconstructor.java : Reconstruction des fichiers
- FileReader.java : Lecture des fichiers

## Licence
MIT
