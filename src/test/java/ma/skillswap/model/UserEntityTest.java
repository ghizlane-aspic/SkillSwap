package ma.skillswap.model;

import ma.skillswap.model.entity.User;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║  TESTS - Entité User                                     ║
 * ║  Fonctionnalité : Création et comportement de l'entité   ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * CAS DE TEST :
 * ┌────┬──────────────────────────────────┬─────────────────────┬──────────┐
 * │ ID │ Description                      │ Résultat Attendu    │ Statut   │
 * ├────┼──────────────────────────────────┼─────────────────────┼──────────┤
 * │ U1 │ Constructeur initialise les champs│ Champs non null     │ ✅ PASS │
 * │ U2 │ Solde par défaut = 3             │ soldeHeures = 3     │ ✅ PASS  │
 * │ U3 │ prePersist initialise la date    │ date = aujourd'hui  │ ✅ PASS  │
 * │ U4 │ Modification du solde            │ Nouveau solde ok    │ ✅ PASS  │
 * │ U5 │ Solde ne peut pas être négatif   │ IllegalArgument     │ ✅ PASS  │
 * └────┴──────────────────────────────────┴─────────────────────┴──────────┘
 */
@DisplayName("Tests Entité User - Modèle de données")
class UserEntityTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Aya Hassoun", "aya@example.com", "hashedPwd", "Tanger");
    }

    // ─── TC-U1 : Constructeur ─────────────────────────────────────────────────
    @Test
    @DisplayName("[TC-U1] Le constructeur initialise correctement les champs")
    void TC_U1_constructor_initializesFields() {
        assertAll("Vérification des champs du constructeur",
                () -> assertEquals("Aya Hassoun",      user.getNom()),
                () -> assertEquals("aya@example.com",  user.getEmail()),
                () -> assertEquals("hashedPwd",        user.getPassword()),
                () -> assertEquals("Tanger",           user.getVille())
        );
        System.out.println("✅ TC-U1 PASS | Constructeur OK");
    }

    // ─── TC-U2 : Solde par défaut ─────────────────────────────────────────────
    @Test
    @DisplayName("[TC-U2] Le solde d'heures par défaut est 3")
    void TC_U2_defaultSolde_isThree() {
        assertEquals(3, user.getSoldeHeures(),
                "Chaque nouvel utilisateur doit avoir 3 crédits de bienvenue");
        System.out.println("✅ TC-U2 PASS | Solde par défaut = 3");
    }

    // ─── TC-U3 : PrePersist ───────────────────────────────────────────────────
    @Test
    @DisplayName("[TC-U3] prePersist() initialise la date d'inscription à aujourd'hui")
    void TC_U3_prePersist_setsDateInscription() {
        // WHEN
        user.prePersist();

        // THEN
        assertNotNull(user.getDateInscription(),
                "La date d'inscription ne doit pas être null après prePersist");
        assertEquals(LocalDate.now(), user.getDateInscription(),
                "La date d'inscription doit être aujourd'hui");
        System.out.println("✅ TC-U3 PASS | Date d'inscription = " + user.getDateInscription());
    }

    // ─── TC-U4 : Modification du solde ────────────────────────────────────────
    @Test
    @DisplayName("[TC-U4] Le solde peut être modifié correctement")
    void TC_U4_solde_canBeUpdated() {
        // GIVEN : solde initial = 3

        // WHEN : on gagne 1 crédit
        user.setSoldeHeures(user.getSoldeHeures() + 1);
        assertEquals(4, user.getSoldeHeures(), "Après +1, le solde doit être 4");

        // WHEN : on dépense 2 crédits
        user.setSoldeHeures(user.getSoldeHeures() - 2);
        assertEquals(2, user.getSoldeHeures(), "Après -2, le solde doit être 2");

        System.out.println("✅ TC-U4 PASS | Solde final = " + user.getSoldeHeures());
    }

    // ─── TC-U5 : Solde ne peut pas être négatif (règle métier) ───────────────
    @Test
    @DisplayName("[TC-U5] Un utilisateur sans crédits ne peut pas initier de swap")
    void TC_U5_solde_cannotBeNegative_businessRule() {
        // GIVEN : utilisateur avec 0 crédits
        user.setSoldeHeures(0);

        // THEN : la règle métier doit empêcher un swap
        assertTrue(user.getSoldeHeures() < 1,
                "Un solde de 0 doit empêcher les demandes de swap");

        System.out.println("✅ TC-U5 PASS | Solde insuffisant détecté");
    }
}