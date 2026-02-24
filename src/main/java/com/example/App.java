package com.example;

import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;
import com.example.repository.SalleRepository;
import com.example.repository.SalleRepositoryImpl;
import com.example.service.SalleService;
import com.example.service.SalleServiceImpl;
import com.example.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        // Création de l'EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestion-reservations");
        EntityManager em = emf.createEntityManager();

        try {
            // Initialisation des repositories et services
            SalleRepository salleRepository = new SalleRepositoryImpl(em);
            SalleService salleService = new SalleServiceImpl(em, salleRepository);

            // Initialisation des données de test
            initializeTestData(em);

            // TEST 1 : Vérification des salles libres
            System.out.println("\n TEST 1 : Vérification des salles libres ");
            testAvailableRooms(salleService);

            // TTEST 2 : Recherche avec filtres
            System.out.println("\nTEST 2 : Recherche avec filtres");
            testMultiCriteriaSearch(salleService);

            // TEST 3 : Test de pagination
            System.out.println("\nTEST 3 : Test de pagination");
            testPagination(salleService);

        } finally {
            em.close();
            emf.close();
        }
    }

    private static void initializeTestData(EntityManager em) {
        em.getTransaction().begin();

        // Création des équipements
        Equipement projecteur = new Equipement("VidéoProjecteur", "Projecteur 4k");
        Equipement ecran = new Equipement("Écran numérique", "Écran interactif tactile");
        Equipement visioconference = new Equipement("les equipements de visioconférence", "Caméra + micro HD");

        em.persist(projecteur);
        em.persist(ecran);
        em.persist(visioconference);

        // Création des utilisateurs
        Utilisateur user1 = new Utilisateur("Moi", "Othmane", "Othmane.m@example.com");
        Utilisateur user2 = new Utilisateur("Salah", "Marbouh", "salah.ma@example.com");

        em.persist(user1);
        em.persist(user2);

        // Création des salles
        Salle salle1 = new Salle("Salle 25", 50);
        salle1.setDescription("Salle standard pour réunions");
        salle1.setBatiment("Bâtiment X");
        salle1.setEtage(2);
        salle1.addEquipement(projecteur);

        Salle salle2 = new Salle("Salle I7", 25);
        salle2.setDescription("Petite salle ");
        salle2.setBatiment("Bâtiment Y");
        salle2.setEtage(4);
        salle2.addEquipement(ecran);

        Salle salle3 = new Salle("Salle 11", 70);
        salle3.setDescription("Grande salle de conférence");
        salle3.setBatiment("Bâtiment Principale");
        salle3.setEtage(1);
        salle3.addEquipement(projecteur);
        salle3.addEquipement(visioconference);

        Salle salle4 = new Salle("Amphi 3", 20);
        salle4.setDescription("Salle de formation");
        salle4.setBatiment("Bâtiment X");
        salle4.setEtage(1);
        salle4.addEquipement(projecteur);
        salle4.addEquipement(ecran);

        Salle salle5 = new Salle("Salle biblio", 100);
        salle5.setDescription("Salle polyvalente");
        salle5.setBatiment("Bâtiment Principale");
        salle5.setEtage(3);
        salle5.addEquipement(visioconference);

        em.persist(salle1);
        em.persist(salle2);
        em.persist(salle3);
        em.persist(salle4);
        em.persist(salle5);

        // Création des réservations
        LocalDateTime now = LocalDateTime.now();

        // Réservation pour salle1
        Reservation res1 = new Reservation(
                now.plusDays(1).withHour(9).withMinute(0),
                now.plusDays(1).withHour(11).withMinute(0),
                "Réunion interne"
        );
        res1.setUtilisateur(user1);
        res1.setSalle(salle1);

        // Réservation pour salle2
        Reservation res2 = new Reservation(
                now.plusDays(2).withHour(14).withMinute(0),
                now.plusDays(2).withHour(16).withMinute(0),
                "Entretien RH"
        );
        res2.setUtilisateur(user2);
        res2.setSalle(salle2);

        // Réservation pour salle3
        Reservation res3 = new Reservation(
                now.plusDays(3).withHour(10).withMinute(0),
                now.plusDays(3).withHour(12).withMinute(0),
                "Présentation commerciale"
        );
        res3.setUtilisateur(user1);
        res3.setSalle(salle3);

        em.persist(res1);
        em.persist(res2);
        em.persist(res3);

        em.getTransaction().commit();
        System.out.println("Données de test initialisées correctement !");
    }

    private static void testAvailableRooms(SalleService salleService) {
        LocalDateTime now = LocalDateTime.now();

        // Créneau 1: Demain de 9h à 11h (salle1 est réservée)
        LocalDateTime start1 = now.plusDays(1).withHour(9).withMinute(0);
        LocalDateTime end1 = now.plusDays(1).withHour(11).withMinute(0);

        System.out.println("Créneau testé de : " + start1 + " à " + end1);
        List<Salle> availableRooms1 = salleService.findAvailableRooms(start1, end1);

        if (availableRooms1.isEmpty()) {
            System.out.println("Aucune salle disponible pour le moment");
        } else {
            for (Salle salle : availableRooms1) {
                System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() + ")");
            }
        }

        // Créneau 2: Dans 5 jours (aucune réservation)
        LocalDateTime start2 = now.plusDays(5).withHour(14).withMinute(0);
        LocalDateTime end2 = now.plusDays(5).withHour(16).withMinute(0);

        System.out.println("\nSalles disponibles pour le créneau: " + start2 + " à " + end2);
        List<Salle> availableRooms2 = salleService.findAvailableRooms(start2, end2);

        if (availableRooms2.isEmpty()) {
            System.out.println("Aucune salle disponible pour ce créneau.");
        } else {
            for (Salle salle : availableRooms2) {
                System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() + ")");
            }
        }
    }

    private static void testMultiCriteriaSearch(SalleService salleService) {
        // Test 1: Recherche par capacité minimale
        Map<String, Object> criteria1 = new HashMap<>();
        criteria1.put("capaciteMin", 30);

        System.out.println("Recherche des salles avec capacité >= 30:");
        List<Salle> result1 = salleService.searchRooms(criteria1);

        for (Salle salle : result1) {
            System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() + ")");
        }

        // Test 2: Recherche par bâtiment et équipement
        Map<String, Object> criteria2 = new HashMap<>();
        criteria2.put("batiment", "Bâtiment A");

        System.out.println("\nRecherche des salles dans le Bâtiment A:");
        List<Salle> result2 = salleService.searchRooms(criteria2);

        for (Salle salle : result2) {
            System.out.println("- " + salle.getNom() + " (bâtiment: " + salle.getBatiment() + ")");
        }

        // Test 3: Recherche combinée (capacité, étage)
        Map<String, Object> criteria3 = new HashMap<>();
        criteria3.put("capaciteMin", 20);
        criteria3.put("capaciteMax", 40);
        criteria3.put("etage", 2);

        System.out.println("\nRecherche des salles avec capacité entre 20 et 40, à l'étage 2:");
        List<Salle> result3 = salleService.searchRooms(criteria3);

        for (Salle salle : result3) {
            System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() +
                    ", étage: " + salle.getEtage() + ")");
        }
    }

    private static void testPagination(SalleService salleService) {
        int pageSize = 2; // Nombre d'éléments par page

        // Récupération du nombre total de pages
        int totalPages = salleService.getTotalPages(pageSize);
        System.out.println("Total pages disponibles : " + totalPages);

        // Affichage de chaque page
        for (int page = 1; page <= totalPages; page++) {
            System.out.println("\nAffichage page" + page + ":");

            List<Salle> sallesPage = salleService.getPaginatedRooms(page, pageSize);

            for (Salle salle : sallesPage) {
                System.out.println("- " + salle.getNom() + " (capacité: " + salle.getCapacite() +
                        ", bâtiment: " + salle.getBatiment() + ")");
            }
        }

        // Création d'un objet PaginationResult pour la première page
        long totalItems = salleService.getAllRooms().size();
        List<Salle> firstPageItems = salleService.getPaginatedRooms(1, pageSize);

        PaginationResult<Salle> paginationResult = new PaginationResult<>(
                firstPageItems, 1, pageSize, totalItems
        );

        System.out.println("\nRésumé pagination :");
        System.out.println("Page actuelle : " + paginationResult.getCurrentPage());
        System.out.println("Taille page: " + paginationResult.getPageSize());
        System.out.println("Total pages :" + paginationResult.getTotalPages());
        System.out.println("Éléments totaux : " + paginationResult.getTotalItems());
        System.out.println("Page suivante ? " + paginationResult.hasNext());
        System.out.println("Page précédente ? " + paginationResult.hasPrevious());
    }
}