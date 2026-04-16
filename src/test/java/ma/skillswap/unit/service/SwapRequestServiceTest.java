package ma.skillswap.unit.service;

import ma.skillswap.dao.SwapRequestDao;
import ma.skillswap.dao.UserDao;
import ma.skillswap.model.entity.*;
import ma.skillswap.model.entity.enums.SwapStatus;
import ma.skillswap.service.SwapRequestService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TESTS - SwapRequestService                                 ║
 * ║  Fonctionnalité C : Système de Swap & Cycle de Vie          ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * SCÉNARIOS DE TEST :
 * ┌─────┬───────────────────────────────────────────┬────────────────────────────┬──────────┐
 * │ ID  │ Description                               │ Résultat Attendu           │ Statut   │
 * ├─────┼───────────────────────────────────────────┼────────────────────────────┼──────────┤
 * │ SW1 │ Créer une demande valide                  │ Statut PENDING             │ ✅ PASS  │
 * │ SW2 │ Demander sa propre offre                  │ IllegalArgumentException   │ ✅ PASS  │
 * │ SW3 │ Demander sans crédits suffisants          │ IllegalArgumentException   │ ✅ PASS  │
 * │ SW4 │ Accepter une demande PENDING              │ Statut ACCEPTED            │ ✅ PASS  │
 * │ SW5 │ Refuser une demande PENDING               │ Statut REJECTED            │ ✅ PASS  │
 * │ SW6 │ Compléter un swap ACCEPTED → transfert    │ Crédits transférés         │ ✅ PASS  │
 * │ SW7 │ Compléter un swap non ACCEPTED            │ IllegalStateException      │ ✅ PASS  │
 * │ SW8 │ Transfert de crédits : débit et crédit    │ Soldes mis à jour          │ ✅ PASS  │
 * └─────┴───────────────────────────────────────────┴────────────────────────────┴──────────┘
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests SwapRequestService - Logique métier des échanges")
class SwapRequestServiceTest {

    @Mock private SwapRequestDao swapRequestDao;
    @Mock private UserDao userDao;

    @InjectMocks
    private SwapRequestService swapRequestService;

    private User requester;
    private User provider;
    private SkillOffer skillOffer;
    private SwapRequest swapRequest;

    @BeforeEach
    void setUp() {
        // Requester (apprenant) : a 2 crédits
        requester = new User("Aya", "aya@test.com", "hash", "Tanger");
        requester.setId(1L);
        requester.setSoldeHeures(2);

        // Provider (enseignant) : a 3 crédits
        provider = new User("Ghizlane", "ghizlane@test.com", "hash", "Fes");
        provider.setId(2L);
        provider.setSoldeHeures(3);

        // SkillOffer appartient au provider
        skillOffer = new SkillOffer();
        skillOffer.setId(1L);
        skillOffer.setUser(provider);

        // SwapRequest de base
        swapRequest = new SwapRequest(requester, provider, skillOffer);
        swapRequest.setId(1L);
        swapRequest.setStatut(SwapStatus.PENDING);
    }

    // ─── TC-SW1 : Création d'une demande valide ───────────────────────────────
    @Test
    @DisplayName("[TC-SW1] createRequest() crée une demande avec statut PENDING")
    void TC_SW1_createRequest_validData_statusPending() {
        // GIVEN
        when(swapRequestDao.save(any(SwapRequest.class))).thenReturn(swapRequest);

        // WHEN
        SwapRequest result = swapRequestService.createRequest(requester, skillOffer);

        // THEN
        assertNotNull(result);
        assertEquals(SwapStatus.PENDING, result.getStatut(),
                "Une nouvelle demande doit avoir le statut PENDING");
        verify(swapRequestDao, times(1)).save(any(SwapRequest.class));

        System.out.println("✅ TC-SW1 PASS | Demande créée avec statut : " + result.getStatut());
    }

    // ─── TC-SW2 : Demander sa propre offre ────────────────────────────────────
    @Test
    @DisplayName("[TC-SW2] createRequest() rejette si le requester est le provider")
    void TC_SW2_createRequest_ownOffer_throwsException() {
        // GIVEN : provider essaie de demander sa propre offre
        provider.setSoldeHeures(5);

        // WHEN / THEN
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> swapRequestService.createRequest(provider, skillOffer),
                "On ne peut pas demander sa propre compétence"
        );

        System.out.println("✅ TC-SW2 PASS | Auto-demande rejetée : " + ex.getMessage());
    }

    // ─── TC-SW3 : Crédits insuffisants ───────────────────────────────────────
    @Test
    @DisplayName("[TC-SW3] createRequest() rejette si le solde est insuffisant (< 1)")
    void TC_SW3_createRequest_insufficientCredits_throwsException() {
        // GIVEN : requester avec 0 crédits
        requester.setSoldeHeures(0);

        // WHEN / THEN
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> swapRequestService.createRequest(requester, skillOffer),
                "Un utilisateur sans crédits ne peut pas faire de demande"
        );

        assertTrue(ex.getMessage().toLowerCase().contains("crédit") ||
                        ex.getMessage().toLowerCase().contains("solde"),
                "Le message doit mentionner les crédits");

        System.out.println("✅ TC-SW3 PASS | Crédits insuffisants détectés : " + ex.getMessage());
    }

    // ─── TC-SW4 : Accepter une demande ───────────────────────────────────────
    @Test
    @DisplayName("[TC-SW4] acceptRequest() change le statut à ACCEPTED")
    void TC_SW4_acceptRequest_pendingRequest_statusAccepted() {
        // GIVEN
        when(swapRequestDao.findById(1L)).thenReturn(swapRequest);
        when(swapRequestDao.update(any(SwapRequest.class))).thenAnswer(inv -> {
            SwapRequest sr = inv.getArgument(0);
            return sr;
        });

        // WHEN
        SwapRequest result = swapRequestService.acceptRequest(1L);

        // THEN
        assertEquals(SwapStatus.ACCEPTED, result.getStatut(),
                "La demande doit passer en ACCEPTED");

        System.out.println("✅ TC-SW4 PASS | Statut : " + result.getStatut());
    }

    // ─── TC-SW5 : Refuser une demande ────────────────────────────────────────
    @Test
    @DisplayName("[TC-SW5] rejectRequest() change le statut à REJECTED")
    void TC_SW5_rejectRequest_pendingRequest_statusRejected() {
        // GIVEN
        when(swapRequestDao.findById(1L)).thenReturn(swapRequest);
        when(swapRequestDao.update(any(SwapRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        SwapRequest result = swapRequestService.rejectRequest(1L);

        // THEN
        assertEquals(SwapStatus.REJECTED, result.getStatut(),
                "La demande doit passer en REJECTED");

        System.out.println("✅ TC-SW5 PASS | Statut : " + result.getStatut());
    }

    // ─── TC-SW6 : Compléter un swap et transférer les crédits ─────────────────
    @Test
    @DisplayName("[TC-SW6] completeRequest() transfère les crédits correctement")
    void TC_SW6_completeRequest_accepted_transfersCredits() {
        // GIVEN
        swapRequest.setStatut(SwapStatus.ACCEPTED);
        int initialRequesterSolde = requester.getSoldeHeures(); // 2
        int initialProviderSolde  = provider.getSoldeHeures();  // 3

        when(swapRequestDao.findById(1L)).thenReturn(swapRequest);
        when(swapRequestDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        SwapRequest result = swapRequestService.completeRequest(1L);

        // THEN
        assertEquals(SwapStatus.COMPLETED, result.getStatut());
        assertEquals(initialRequesterSolde - 1, requester.getSoldeHeures(),
                "L'apprenant perd 1 crédit");
        assertEquals(initialProviderSolde + 1, provider.getSoldeHeures(),
                "L'enseignant gagne 1 crédit");

        System.out.println("✅ TC-SW6 PASS | Crédits transférés :");
        System.out.println("   Apprenant : " + initialRequesterSolde +
                " → " + requester.getSoldeHeures());
        System.out.println("   Enseignant : " + initialProviderSolde +
                " → " + provider.getSoldeHeures());
    }

    // ─── TC-SW7 : Compléter un swap non accepté ───────────────────────────────
    @Test
    @DisplayName("[TC-SW7] completeRequest() lève une exception si le statut n'est pas ACCEPTED")
    void TC_SW7_completeRequest_notAccepted_throwsException() {
        // GIVEN : swap encore PENDING
        swapRequest.setStatut(SwapStatus.PENDING);
        when(swapRequestDao.findById(1L)).thenReturn(swapRequest);

        // WHEN / THEN
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> swapRequestService.completeRequest(1L),
                "Ne peut pas compléter un swap non accepté"
        );

        System.out.println("✅ TC-SW7 PASS | Exception : " + ex.getMessage());
    }

    // ─── TC-SW8 : Vérification précise des soldes ─────────────────────────────
    @Test
    @DisplayName("[TC-SW8] completeRequest() : le solde ne peut jamais être négatif")
    void TC_SW8_completeRequest_soldeNeverNegative() {
        // GIVEN : apprenant avec exactement 1 crédit
        requester.setSoldeHeures(1);
        swapRequest.setStatut(SwapStatus.ACCEPTED);

        when(swapRequestDao.findById(1L)).thenReturn(swapRequest);
        when(swapRequestDao.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userDao.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        swapRequestService.completeRequest(1L);

        // THEN : solde = 0, pas négatif
        assertEquals(0, requester.getSoldeHeures(),
                "Le solde de l'apprenant doit être 0, pas négatif");
        assertTrue(requester.getSoldeHeures() >= 0,
                "Le solde ne doit jamais être négatif");

        System.out.println("✅ TC-SW8 PASS | Solde apprenant = " + requester.getSoldeHeures());
    }
}