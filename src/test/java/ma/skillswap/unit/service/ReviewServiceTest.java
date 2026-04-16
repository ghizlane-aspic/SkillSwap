package ma.skillswap.unit.service;

import ma.skillswap.dao.ReviewDao;
import ma.skillswap.model.entity.*;
import ma.skillswap.model.entity.enums.SwapStatus;
import ma.skillswap.service.ReviewService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TESTS - ReviewService                                      ║
 * ║  Fonctionnalité D : Système de Confiance (Évaluations)      ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * SCÉNARIOS DE TEST :
 * ┌─────┬───────────────────────────────────────────┬────────────────────────────┬──────────┐
 * │ ID  │ Description                               │ Résultat Attendu           │ Statut   │
 * ├─────┼───────────────────────────────────────────┼────────────────────────────┼──────────┤
 * │ RV1 │ Créer un avis valide (note 1-5)           │ Review créée               │ ✅ PASS  │
 * │ RV2 │ Avis sur un swap non COMPLETED            │ IllegalStateException      │ ✅ PASS  │
 * │ RV3 │ Avis sur un swap déjà noté                │ IllegalStateException      │ ✅ PASS  │
 * │ RV4 │ Note invalide (< 1 ou > 5)               │ IllegalArgumentException   │ ✅ PASS  │
 * │ RV5 │ Notes aux limites (1 et 5) valides        │ Review créée               │ ✅ PASS  │
 * └─────┴───────────────────────────────────────────┴────────────────────────────┴──────────┘
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests ReviewService - Système d'évaluation")
class ReviewServiceTest {

    @Mock private ReviewDao reviewDao;

    @InjectMocks
    private ReviewService reviewService;

    private SwapRequest completedSwap;
    private SwapRequest pendingSwap;

    @BeforeEach
    void setUp() {
        // Swap terminé sans avis
        completedSwap = new SwapRequest();
        completedSwap.setId(1L);
        completedSwap.setStatut(SwapStatus.COMPLETED);
        completedSwap.setReview(null);

        // Swap en attente
        pendingSwap = new SwapRequest();
        pendingSwap.setId(2L);
        pendingSwap.setStatut(SwapStatus.PENDING);
    }

    // ─── TC-RV1 : Créer un avis valide ───────────────────────────────────────
    @Test
    @DisplayName("[TC-RV1] createReview() crée un avis pour un swap terminé")
    void TC_RV1_createReview_completedSwap_createsReview() {
        // GIVEN
        Review expected = new Review(4, "Très bon cours !", completedSwap);
        when(reviewDao.save(any(Review.class))).thenReturn(expected);

        // WHEN
        Review result = reviewService.createReview(4, "Très bon cours !", completedSwap);

        // THEN
        assertNotNull(result);
        assertEquals(4, result.getNote());
        assertEquals("Très bon cours !", result.getCommentaire());
        verify(reviewDao, times(1)).save(any(Review.class));

        System.out.println("✅ TC-RV1 PASS | Avis créé avec note : " + result.getNote());
    }

    // ─── TC-RV2 : Swap non terminé ───────────────────────────────────────────
    @Test
    @DisplayName("[TC-RV2] createReview() rejette si le swap n'est pas COMPLETED")
    void TC_RV2_createReview_notCompletedSwap_throwsException() {
        // WHEN / THEN
        assertThrows(
                IllegalStateException.class,
                () -> reviewService.createReview(5, "Top !", pendingSwap),
                "Impossible de noter un swap non terminé"
        );
        verify(reviewDao, never()).save(any());

        System.out.println("✅ TC-RV2 PASS | Notation refusée (swap non terminé)");
    }

    // ─── TC-RV3 : Swap déjà noté ─────────────────────────────────────────────
    @Test
    @DisplayName("[TC-RV3] createReview() rejette si un avis existe déjà")
    void TC_RV3_createReview_alreadyReviewed_throwsException() {
        // GIVEN : le swap a déjà un avis
        completedSwap.setReview(new Review(3, "Bien", completedSwap));

        // WHEN / THEN
        assertThrows(
                IllegalStateException.class,
                () -> reviewService.createReview(5, "Excellent !", completedSwap),
                "Impossible de noter deux fois le même swap"
        );

        System.out.println("✅ TC-RV3 PASS | Double notation refusée");
    }

    // ─── TC-RV4 : Note invalide ───────────────────────────────────────────────
    @ParameterizedTest(name = "[TC-RV4] Note invalide : {0}")
    @ValueSource(ints = {0, -1, 6, 10, -99})
    @DisplayName("[TC-RV4] createReview() rejette les notes hors de [1-5]")
    void TC_RV4_createReview_invalidNote_throwsException(int invalidNote) {
        // WHEN / THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> reviewService.createReview(invalidNote, "Commentaire", completedSwap),
                "La note " + invalidNote + " doit être rejetée"
        );

        System.out.println("✅ TC-RV4 PASS | Note invalide rejetée : " + invalidNote);
    }

    // ─── TC-RV5 : Notes aux limites ──────────────────────────────────────────
    @ParameterizedTest(name = "[TC-RV5] Note limite valide : {0}")
    @ValueSource(ints = {1, 5})
    @DisplayName("[TC-RV5] createReview() accepte les notes 1 et 5 (valeurs limites)")
    void TC_RV5_createReview_boundaryNotes_accepted(int note) {
        // GIVEN
        when(reviewDao.save(any(Review.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // WHEN / THEN
        assertDoesNotThrow(
                () -> reviewService.createReview(note, "Commentaire", completedSwap),
                "La note " + note + " doit être acceptée"
        );

        System.out.println("✅ TC-RV5 PASS | Note limite acceptée : " + note);
    }
}