# Projet-SPIE

## Index

- [Git-Use-and-Rules](https://docs.google.com/document/d/1nyr2jiTDT-A0yVJztQiWG9veQD9bFvgRDdMQ8quiciQ/edit?hl=fr&tab=t.0)
- [Trello](https://trello.com/b/Vq1Tad50/projet-pld-agile)
- [Backlog](https://docs.google.com/document/d/1r-AfEu88P8UFrHhzVa6V-Ib7-K1yhyPsC5GfzGUBNdg/edit?tab=t.0)
- [Presentation](https://docs.google.com/presentation/d/1XE5vXV9gx4LZSCDFPsrbhRKLgKerEzuvFPR6eMnrPbw/edit?usp=sharing)

## Ressources

- [SpringBoot Starter](https://medium.com/@mutahirmanzoor1/getting-started-with-spring-boot-a-beginners-guide-9dcd38c2cd8c)

## Description

Ce projet est une application de gestion de livraisons optimisée utilisant Spring Boot pour le backend. L’application permet de gérer des livraisons, de calculer des tournées optimales pour les livreurs et d’importer des fichiers de configuration pour les cartes de la ville et les livraisons.

## Fonctionnalités principales

- Import de cartes de la ville et de listes de livraisons au format XML.
- Initialisation et gestion des livreurs.
- Calcul de tournées optimales pour les livreurs en fonction des données de livraison et de la carte.
- Interface utilisateur en HTML et JavaScript pour gérer les livraisons et visualiser les tournées.

## Prérequis

- **Java 17** ou version supérieure
- **Maven** (pour la gestion des dépendances)
- **IntelliJ IDEA**

## Installation et exécution du projet

### Étape 1 : Cloner le dépôt

Clonez le dépôt GitHub sur votre machine locale :

```bash
git clone https://github.com/votre-utilisateur/nom-du-projet.git
cd nom-du-projet
```

### Étape 2 : Importer le projet dans IntelliJ

1. Lancez IntelliJ IDEA.
2. Allez dans `File > Open` et sélectionnez le dossier du projet cloné.
3. Confirmez l'importation du projet en tant que projet Maven pour que toutes les dépendances soient correctement chargées.

### Étape 3 : Configurer et tester l'application dans IntelliJ

1. Ouvrez le fichier principal de votre application (probablement situé dans `src/main/java/_4if.pld_agile_4if/PldAgile4IfApplication.java`) et cliquez sur `Run` pour démarrer le serveur Spring Boot.
2. L'application devrait démarrer sur `http://localhost:8080`. Accédez-y dans votre navigateur pour vérifier que l'application fonctionne.

## Compilation en JAR exécutable

Pour exécuter l'application en dehors d'IntelliJ, nous devons créer un fichier JAR exécutable. Voici comment procéder :

### Générer le JAR avec Maven

Dans le terminal d'IntelliJ ou dans un terminal à l'extérieur d'IntelliJ :

```bash
mvn clean install
```

Cela crée un fichier JAR exécutable dans le dossier `target` du projet.

### Lancer le JAR

Vous pouvez maintenant exécuter l'application en ligne de commande :

```bash
java -jar target/nom-du-projet-1.0.0.jar
```

Assurez-vous de remplacer `nom-du-projet-1.0.0.jar` par le nom du fichier JAR généré dans le dossier `target`.

## Documentation API

L'application offre plusieurs endpoints principaux :

- `POST /initializeCouriers` : Initialise les livreurs avec un nombre défini.
- `POST /uploadMap` : Importer un fichier XML pour la carte de la ville.
- `POST /uploadTour` : Charger un fichier XML des livraisons.
- `GET /optimalTour` : Calculer la tournée optimale pour un livreur donné.
- `GET /deliveries` : Obtenir la liste des livraisons.

Pour plus de détails, référez-vous à la documentation des contrôleurs et des services dans le code source.

## Technologies utilisées

- **Spring Boot** : Framework pour le backend.
- **Maven** : Gestionnaire de dépendances.
- **Leaflet.js** : Librairie JavaScript pour la visualisation de la carte dans l'interface utilisateur.

## Auteurs

- Membre 1 - `@Antoine-Aubut`
- Membre 2 - `@NeyNess `
- Membre 3 - `@A-Allaz`
- Membre 4 - `@kuangezhang`
