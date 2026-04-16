package ma.skillswap.unit.service;

import ma.skillswap.dao.SkillOfferDao;
import ma.skillswap.model.entity.*;
import ma.skillswap.model.entity.enums.Level;
import ma.skillswap.service.SkillOfferService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TESTS - SkillOfferService                                  ║
 * ║  Fonctionnalité B : Catalogue et Gestion des Compétences    ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * SCÉNARIOS DE TEST :
 * ┌─────┬────────────────────────────────────┬──────────────────────────┬──────────┐
 * │ ID  │ Description                        │ Résultat Attendu         │ Statut   │
 * ├─────┼────────────────────────────────────┼──────────────────────────┼──────────┤
 * │ SO1 │ Créer une offre                    │ Offre sauvegardée        │ ✅ PASS  │
 * │ SO2 │ Lister toutes les offres           │ Liste complète           │ ✅ PASS  │
 * │ SO3 │ Recherche par mot-clé              │ Offres filtrées          │ ✅ PASS  │
 * │ SO4 │ Filtrer par catégorie              │ Offres de la catégorie   │ ✅ PASS  │
 * │ SO5 │ Mettre à jour une offre            │ update() appelé          │ ✅ PASS  │
 * │ SO6 │ Supprimer une offre               │ deleteById() appelé      │ ✅ PASS  │
 * └─────┴────────────────────────────────────┴──────────────────────────┴──────────┘
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests SkillOfferService - Gestion des offres de compétences")
class SkillOfferServiceTest {

    @Mock private SkillOfferDao skillOfferDao;

    @InjectMocks
    private SkillOfferService skillOfferService;

    private SkillOffer offer1;
    private SkillOffer offer2;

    @BeforeEach
    void setUp() {
        User user = new User("Aya", "aya@test.com", "hash", "Tanger");
        user.setId(1L);

        Category category = new Category("Informatique");
        category.setId(1L);

        Skill skill = new Skill("Java", category);
        skill.setId(1L);

        offer1 = new SkillOffer("Cours de Java pour débutants", Level.DEBUTANT, user, skill);
        offer1.setId(1L);

        offer2 = new SkillOffer("Java avancé et design patterns", Level.EXPERT, user, skill);
        offer2.setId(2L);
    }

    // ─── TC-SO1 : Créer une offre ─────────────────────────────────────────────
    @Test
    @DisplayName("[TC-SO1] save() persiste l'offre de compétence")
    void TC_SO1_save_validOffer_persisted() {
        // GIVEN
        when(skillOfferDao.save(offer1)).thenReturn(offer1);

        // WHEN
        SkillOffer result = skillOfferService.save(offer1);

        // THEN
        assertNotNull(result);
        assertEquals("Cours de Java pour débutants", result.getDescriptionOffre());
        verify(skillOfferDao, times(1)).save(offer1);

        System.out.println("✅ TC-SO1 PASS | Offre créée : " + result.getDescriptionOffre());
    }

    // ─── TC-SO2 : Lister toutes les offres ───────────────────────────────────
    @Test
    @DisplayName("[TC-SO2] findAll() retourne toutes les offres disponibles")
    void TC_SO2_findAll_returnsAllOffers() {
        // GIVEN
        when(skillOfferDao.findAll()).thenReturn(Arrays.asList(offer1, offer2));

        // WHEN
        List<SkillOffer> result = skillOfferService.findAll();

        // THEN
        assertEquals(2, result.size(), "Doit retourner 2 offres");
        verify(skillOfferDao, times(1)).findAll();

        System.out.println("✅ TC-SO2 PASS | " + result.size() + " offres trouvées");
    }

    // ─── TC-SO3 : Recherche par mot-clé ──────────────────────────────────────
    @Test
    @DisplayName("[TC-SO3] search() retourne les offres correspondant au mot-clé")
    void TC_SO3_search_keyword_returnsMatchingOffers() {
        // GIVEN
        when(skillOfferDao.search("Java")).thenReturn(Arrays.asList(offer1, offer2));

        // WHEN
        List<SkillOffer> result = skillOfferService.search("Java");

        // THEN
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(o -> o.getDescriptionOffre().contains("Java")));

        System.out.println("✅ TC-SO3 PASS | " + result.size() + " offres pour 'Java'");
    }

    // ─── TC-SO4 : Filtrer par catégorie ──────────────────────────────────────
    @Test
    @DisplayName("[TC-SO4] findByCategory() retourne les offres d'une catégorie")
    void TC_SO4_findByCategory_returnsFilteredOffers() {
        // GIVEN
        when(skillOfferDao.findByCategory(1L)).thenReturn(Arrays.asList(offer1, offer2));

        // WHEN
        List<SkillOffer> result = skillOfferService.findByCategory(1L);

        // THEN
        assertFalse(result.isEmpty());
        verify(skillOfferDao, times(1)).findByCategory(1L);

        System.out.println("✅ TC-SO4 PASS | " + result.size() + " offres pour catégorie ID=1");
    }

    // ─── TC-SO5 : Mettre à jour une offre ────────────────────────────────────
    @Test
    @DisplayName("[TC-SO5] update() met à jour l'offre correctement")
    void TC_SO5_update_offer_callsDao() {
        // GIVEN
        offer1.setDescriptionOffre("Description modifiée");
        when(skillOfferDao.update(offer1)).thenReturn(offer1);

        // WHEN
        SkillOffer result = skillOfferService.update(offer1);

        // THEN
        assertEquals("Description modifiée", result.getDescriptionOffre());
        verify(skillOfferDao, times(1)).update(offer1);

        System.out.println("✅ TC-SO5 PASS | Offre mise à jour");
    }

    // ─── TC-SO6 : Supprimer une offre ────────────────────────────────────────
    @Test
    @DisplayName("[TC-SO6] deleteById() supprime l'offre par son ID")
    void TC_SO6_deleteById_callsDao() {
        // GIVEN
        doNothing().when(skillOfferDao).deleteById(1L);

        // WHEN
        skillOfferService.deleteById(1L);

        // THEN
        verify(skillOfferDao, times(1)).deleteById(1L);

        System.out.println("✅ TC-SO6 PASS | Offre supprimée (ID=1)");
    }
}