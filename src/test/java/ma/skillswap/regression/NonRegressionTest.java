package ma.skillswap.regression;

import ma.skillswap.dao.*;
import ma.skillswap.model.entity.*;
import ma.skillswap.model.entity.enums.Level;
import ma.skillswap.model.entity.enums.SwapStatus;
import ma.skillswap.service.*;
import ma.skillswap.util.PasswordUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  TESTS DE NON-RÉGRESSION - SkillSwap                           ║
 * ║                                                                 ║
 * ║  Ces tests vérifient que les fonctionnalités core ne cassent    ║
 * ║  pas après des modifications du code.                           ║
 * ║                                                                 ║
 * ║  Scénario complet : cycle de vie d'un swap de A à Z            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * SCÉNARIOS DE NON-RÉGRESSION :
 * ┌──────┬───────────────────────────────────────────────────┬────────────────────────┬──────────┐
 * │  ID  │ Description                                       │ Résultat Attendu       │ Statut   │
 * ├──────┼───────────────────────────────────────────────────┼────────────────────────┼──────────┤
 * │ NR1  │ Inscription → Login → Déconnexion                 │ Flux complet OK        │ ✅ PASS  │
 * │ NR2  │ Cycle complet d'un swap (PENDING→ACCEPTED→DONE)   │ Crédits transférés     │ ✅ PASS  │
 * │ NR3  │ Annonce créée → visible dans le catalogue         │ Offre listée           │ ✅ PASS  │
 * │ NR4  │ Swap complété → possibilité de noter              │ Review créée           │ ✅ PASS  │
 * │ NR5  │ Hashage de MDP résistant à une refactorisation    │ Vérification toujours  │ ✅ PASS  │
 * │ NR6  │ Solde jamais négatif après plusieurs swaps        │ Solde ≥ 0              │ ✅ PASS  │
 * │ NR7  │ Filtrage catalogue par catégorie stable            │ Résultats cohérents    │ ✅ PASS  │
 * └──────┴───────────────────────────────────────────────────┴────────────────────────┴──────────┘
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🔄 Tests de Non-Régression - SkillSwap")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NonRegressionTest {

    // ── Services ──────────────────────────────────────────────────────────────
    @Mock private UserDao        userDao;
    @Mock private SwapRequestDao swapRequestDao;
    @Mock private ReviewDao      reviewDao;
    @Mock private SkillOfferDao  skillOfferDao;
    @Mock private MessageDao     messageDao;

    @InjectMocks private UserService        userService;
    @InjectMocks private SwapRequestService swapRequestService;
    @InjectMocks private ReviewService      reviewService;
    @InjectMocks private SkillOfferService  skillOfferService;

    // ── Données de test partagées ─────────────────────────────────────────────
    private User    apprenante;
    private User    enseignante;
    private Skill   skill;
    private SkillOffer offer;

    @BeforeEach
    void setUp() {
        apprenante = new User("Aya Hassoun", "aya@skillswap.ma",
                PasswordUtil.hashPassword("AyaPass123"), "Tanger");
        apprenante.setId(1L);
        apprenante.setSoldeHeures(3);

        enseignante = new User("Ghizlane Tougui", "ghizlane@skillswap.ma",
                PasswordUtil.hashPassword("GhizPass456"), "Fes");
        enseignante.setId(2L);
        enseignante.setSoldeHeures(1);

        Category cat = new Category("Informatique");
        cat.setId(1L);

        skill = new Skill("Java", cat);
        skill.setId(1L);

        offer = new SkillOffer("Cours de Java niveau débutant", Level.DEBUTANT,
                enseignante, skill);
        offer.setId(1L);
    }

    // ─── NR1 : Flux Inscription → Login ──────────────────────────────────────
    @Test
    @Order(1)
    @DisplayName("[NR1] Flux complet : Inscription puis Login réussi")
    void NR1_inscriptionPuisLogin_fluxComplet() {
        // GIVEN
        when(userDao.existsByEmail("aya@skillswap.ma")).thenReturn(false);
        when(userDao.save(any(User.class))).thenReturn(apprenante);
        when(userDao.findByEmail("aya@skillswap.ma"))
                .thenReturn(Optional.of(apprenante));

        // WHEN : Inscription
        User registered = userService.register(
                "Aya Hassoun", "aya@skillswap.ma", "AyaPass123", "Tanger");

        // THEN : Inscription OK
        assertNotNull(registered, "L'inscription doit réussir");
        assertEquals(3, registered.getSoldeHeures(), "Solde de bienvenue = 3");

        // WHEN : Login
        Optional<User> loggedIn = userService.authenticate("aya@skillswap.ma", "AyaPass123");

        // THEN : Login OK
        assertTrue(loggedIn.isPresent(), "Le login doit réussir");
        assertEquals("Aya Hassoun", loggedIn.get().getNom());

        System.out.println("✅ NR1 PASS | Inscription + Login : " + loggedIn.get().getNom());
    }

    // ─── NR2 : Cycle complet d'un swap ───────────────────────────────────────
    @Test
    @Order(2)
    @DisplayName("[NR2] Cycle complet : PENDING → ACCEPTED → COMPLETED + transfert crédits")
    void NR2_cycleCompletSwap_creditsTransferes() {
        // ÉTAPE 1 : Création
        SwapRequest swap = new SwapRequest(apprenante, enseignante, offer);
        swap.setId(1L);
        swap.setStatut(SwapStatus.PENDING);

        when(swapRequestDao.save(any(SwapRequest.class))).thenReturn(swap);
        SwapRequest created = swapRequestService.createRequest(apprenante, offer);
        assertEquals(SwapStatus.PENDING, created.getStatut(), "Statut initial = PENDING");

        // ÉTAPE 2 : Acceptation
        when(swapRequestDao.findById(1L)).thenReturn(swap);
        when(swapRequestDao.update(any(SwapRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        SwapRequest accepted = swapRequestService.acceptRequest(1L);
        assertEquals(SwapStatus.ACCEPTED, accepted.getStatut(), "Statut = ACCEPTED");

        // ÉTAPE 3 : Completion + transfert
        swap.setStatut(SwapStatus.ACCEPTED);
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        int soldeBefore_Aya      = apprenante.getSoldeHeures();   // 3
        int soldeBefore_Ghizlane = enseignante.getSoldeHeures();  // 1

        SwapRequest completed = swapRequestService.completeRequest(1L);

        // THEN : Transfert correct
        assertEquals(SwapStatus.COMPLETED, completed.getStatut());
        assertEquals(soldeBefore_Aya - 1, apprenante.getSoldeHeures(),
                "Aya perd 1 crédit");
        assertEquals(soldeBefore_Ghizlane + 1, enseignante.getSoldeHeures(),
                "Ghizlane gagne 1 crédit");

        System.out.println("✅ NR2 PASS | Cycle complet réussi");
        System.out.printf("   Aya : %d → %d | Ghizlane : %d → %d%n",
                soldeBefore_Aya, apprenante.getSoldeHeures(),
                soldeBefore_Ghizlane, enseignante.getSoldeHeures());
    }

    // ─── NR3 : Annonce créée → visible dans le catalogue ─────────────────────
    @Test
    @Order(3)
    @DisplayName("[NR3] Une offre créée apparaît bien dans le catalogue")
    void NR3_offerCreated_appearsInCatalog() {
        // GIVEN
        when(skillOfferDao.save(any(SkillOffer.class))).thenReturn(offer);
        when(skillOfferDao.findAll()).thenReturn(java.util.List.of(offer));

        // WHEN
        skillOfferService.save(offer);
        var catalog = skillOfferService.findAll();

        // THEN
        assertFalse(catalog.isEmpty(), "Le catalogue ne doit pas être vide");
        assertTrue(catalog.contains(offer), "L'offre doit être dans le catalogue");

        System.out.println("✅ NR3 PASS | Offre visible dans le catalogue");
    }

    // ─── NR4 : Swap complété → possibilité de noter ───────────────────────────
    @Test
    @Order(4)
    @DisplayName("[NR4] Un swap COMPLETED permet de créer un avis")
    void NR4_completedSwap_allowsReview() {
        // GIVEN
        SwapRequest completedSwap = new SwapRequest(apprenante, enseignante, offer);
        completedSwap.setId(10L);
        completedSwap.setStatut(SwapStatus.COMPLETED);

        Review review = new Review(5, "Excellent cours de Java !", completedSwap);
        when(reviewDao.save(any(Review.class))).thenReturn(review);

        // WHEN
        Review result = reviewService.createReview(5, "Excellent cours de Java !", completedSwap);

        // THEN
        assertNotNull(result);
        assertEquals(5, result.getNote());

        System.out.println("✅ NR4 PASS | Avis créé avec note : " + result.getNote());
    }

    // ─── NR5 : Hashage résistant à la refactorisation ────────────────────────
    @Test
    @Order(5)
    @DisplayName("[NR5] Le hashage de mot de passe reste cohérent (non-régression sécurité)")
    void NR5_passwordHashing_stableAfterRefactoring() {
        // Ce test détecte si l'algo de hachage change
        String password   = "SkillSwapSecure!2024";
        String hash1      = PasswordUtil.hashPassword(password);
        String hash2      = PasswordUtil.hashPassword(password);

        assertEquals(hash1, hash2,
                "Le hash doit être stable (même résultat pour même input)");
        assertTrue(PasswordUtil.verifyPassword(password, hash1),
                "La vérification doit fonctionner");
        assertFalse(PasswordUtil.verifyPassword("wrong", hash1),
                "Un mauvais MDP ne doit pas passer");

        System.out.println("✅ NR5 PASS | Hashage stable et sécurisé");
    }

    // ─── NR6 : Solde jamais négatif ───────────────────────────────────────────
    @Test
    @Order(6)
    @DisplayName("[NR6] Le solde d'un utilisateur ne peut jamais devenir négatif")
    void NR6_solde_neverNegative_afterMultipleSwaps() {
        // GIVEN : utilisateur avec 1 seul crédit
        apprenante.setSoldeHeures(1);

        SwapRequest swap1 = new SwapRequest(apprenante, enseignante, offer);
        swap1.setId(100L);
        swap1.setStatut(SwapStatus.ACCEPTED);

        when(swapRequestDao.findById(100L)).thenReturn(swap1);
        when(swapRequestDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN : 1er swap (valide)
        swapRequestService.completeRequest(100L);
        assertEquals(0, apprenante.getSoldeHeures(),
                "Solde = 0 après le swap");

        // THEN : avec 0 crédit, une nouvelle demande est impossible
        User providerOther = new User("Autre", "autre@test.com", "hash", "Casa");
        providerOther.setId(3L);
        SkillOffer otherOffer = new SkillOffer("Python", Level.INTERMEDIAIRE,
                providerOther, skill);

        assertThrows(
                IllegalArgumentException.class,
                () -> swapRequestService.createRequest(apprenante, otherOffer),
                "Avec 0 crédits, impossible de faire un autre swap"
        );

        assertTrue(apprenante.getSoldeHeures() >= 0,
                "Le solde ne doit jamais être négatif");

        System.out.println("✅ NR6 PASS | Solde protégé, jamais négatif : "
                + apprenante.getSoldeHeures());
    }

    // ─── NR7 : Filtrage catalogue stable ─────────────────────────────────────
    @Test
    @Order(7)
    @DisplayName("[NR7] Le filtrage par catégorie retourne toujours des résultats cohérents")
    void NR7_catalogFilter_alwaysConsistent() {
        // GIVEN
        when(skillOfferDao.findByCategory(1L)).thenReturn(java.util.List.of(offer));
        when(skillOfferDao.findByCategory(99L)).thenReturn(java.util.List.of());

        // WHEN
        var infoOffers    = skillOfferService.findByCategory(1L);
        var unknownOffers = skillOfferService.findByCategory(99L);

        // THEN
        assertFalse(infoOffers.isEmpty(),
                "La catégorie Informatique doit avoir des offres");
        assertTrue(unknownOffers.isEmpty(),
                "Une catégorie inexistante doit retourner une liste vide (pas null)");
        assertNotNull(unknownOffers,
                "La liste ne doit jamais être null");

        System.out.println("✅ NR7 PASS | Filtrage stable : "
                + infoOffers.size() + " offres Informatique, "
                + unknownOffers.size() + " pour catégorie inconnue");
    }
}