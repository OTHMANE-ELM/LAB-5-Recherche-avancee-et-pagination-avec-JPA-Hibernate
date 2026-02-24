# Lab 5 – Recherche avancée et pagination avec JPA/Hibernate

## 🎯 Objectif

Ce laboratoire a pour but de :

- Implémenter une requête pour trouver les salles disponibles selon un créneau horaire
- Créer une recherche multi-critères dynamique pour les salles
- Mettre en place un système de pagination pour afficher les résultats par page

---

## 🛠 Technologies utilisées

- Java
- Maven
- JPA / Hibernate
- Base de données H2 (en mémoire)

---

## 📌 Fonctionnalités implémentées

### 1️⃣ Recherche des salles disponibles par créneau

Une requête permet de récupérer toutes les salles qui ne sont pas réservées
entre deux dates (startDate et endDate).

✔ Vérification des conflits de réservation  
✔ Utilisation de requêtes JPQL  

---

### 2️⃣ Recherche multi-critères

Mise en place d’une recherche dynamique selon plusieurs paramètres :

- Capacité minimale
- Capacité maximale
- Bâtiment
- Étage

Les critères sont optionnels et combinables.

✔ Construction dynamique de requête  
✔ Filtrage flexible  

---

### 3️⃣ Pagination des résultats

Implémentation d’un système permettant :

- De définir le nombre d’éléments par page
- De récupérer une page spécifique
- De calculer le nombre total de pages

✔ Méthodes :
- getPaginatedRooms(page, size)
- getTotalPages(size)

✔ Classe utilitaire : `PaginationResult<T>`

---
## ▶ Exécution du projet



https://github.com/user-attachments/assets/f49cc971-fff8-4561-a8c4-a256c5875626



