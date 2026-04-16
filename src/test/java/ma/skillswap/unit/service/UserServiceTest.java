package ma.skillswap.unit.service;

import ma.skillswap.dao.UserDao;
import ma.skillswap.model.entity.User;
import ma.skillswap.service.UserService;
import ma.skillswap.util.PasswordUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TESTS - UserService                                        ║
 * ║  Fonctionnalité A : Gestion du Compte et Authentification   ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * SCÉNARIOS DE TEST :
 * ┌─────┬────────────────────────────────────────┬──────────────────────────────┬──────────┐
 * │ ID  │ Description                            │ Résultat Attendu             │ Statut   │
 * ├─────┼────────────────────────────────────────┼──────────────────────────────┼──────────┤
 * │ US1 │ Inscription avec données valides       │ User créé, solde = 3         │ ✅ PASS  │
 * │ US2 │ Inscription avec email déjà existant   │ IllegalArgumentException     │ ✅ PASS  │
 * │ US3 │ Login avec identifiants corrects       │ Optional<User> présent       │ ✅ PASS  │
 * │ US4 │ Login avec mauvais mot de passe        │ Optional vide                │ ✅ PASS  │
 * │ US5 │ Login avec email inexistant            │ Optional vide                │ ✅ PASS  │
 * │ US6 │ Le mot de passe est bien hashé         │ Hash ≠ mot de passe clair    │ ✅ PASS  │
 * │ US7 │ Mise à jour du profil                  │ update() appelé une fois     │ ✅ PASS  │
 * └─────┴────────────────────────────────────────┴──────────────────────────────┴──────────┘
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests UserService - Authentification & Gestion de compte")
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User("Ghizlane Tougui", "ghizlane@example.com",
                PasswordUtil.hashPassword("Password123"), "Tanger");
        validUser.setSoldeHeures(3);
    }

    // ─── TC-US1 : Inscription valide ──────────────────────────────────────────
    @Test
    @DisplayName("[TC-US1] register() crée un utilisateur avec solde de bienvenue = 3")
    void TC_US1_register_validData_createsUser() {
        // GIVEN
        when(userDao.existsByEmail("ghizlane@example.com")).thenReturn(false);
        when(userDao.save(any(User.class))).thenReturn(validUser);

        // WHEN
        User result = userService.register(
                "Ghizlane Tougui", "ghizlane@example.com", "Password123", "Tanger");

        // THEN
        assertNotNull(result, "L'utilisateur créé ne doit pas être null");
        assertEquals(3, result.getSoldeHeures(),
                "Le solde de bienvenue doit être de 3 heures");
        verify(userDao, times(1)).save(any(User.class));

        System.out.println("✅ TC-US1 PASS | User créé avec solde = " + result.getSoldeHeures());
    }

    // ─── TC-US2 : Email déjà utilisé ─────────────────────────────────────────
    @Test
    @DisplayName("[TC-US2] register() lève une exception si l'email existe déjà")
    void TC_US2_register_duplicateEmail_throwsException() {
        // GIVEN
        when(userDao.existsByEmail("ghizlane@example.com")).thenReturn(true);

        // WHEN / THEN
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(
                        "Ghizlane", "ghizlane@example.com", "pass", "Fes"),
                "Une exception doit être levée pour un email dupliqué"
        );

        assertTrue(ex.getMessage().toLowerCase().contains("email") ||
                        ex.getMessage().toLowerCase().contains("existe"),
                "Le message doit mentionner l'email ou l'existence");

        verify(userDao, never()).save(any());

        System.out.println("✅ TC-US2 PASS | Exception levée : " + ex.getMessage());
    }

    // ─── TC-US3 : Login correct ───────────────────────────────────────────────
    @Test
    @DisplayName("[TC-US3] authenticate() retourne l'utilisateur si les identifiants sont corrects")
    void TC_US3_authenticate_correctCredentials_returnsUser() {
        // GIVEN
        when(userDao.findByEmail("ghizlane@example.com"))
                .thenReturn(Optional.of(validUser));

        // WHEN
        Optional<User> result = userService.authenticate(
                "ghizlane@example.com", "Password123");

        // THEN
        assertTrue(result.isPresent(), "L'utilisateur doit être trouvé");
        assertEquals("Ghizlane Tougui", result.get().getNom());

        System.out.println("✅ TC-US3 PASS | Login réussi pour : " + result.get().getNom());
    }

    // ─── TC-US4 : Mauvais mot de passe ────────────────────────────────────────
    @Test
    @DisplayName("[TC-US4] authenticate() retourne Optional vide si le mot de passe est incorrect")
    void TC_US4_authenticate_wrongPassword_returnsEmpty() {
        // GIVEN
        when(userDao.findByEmail("ghizlane@example.com"))
                .thenReturn(Optional.of(validUser));

        // WHEN
        Optional<User> result = userService.authenticate(
                "ghizlane@example.com", "MauvaisMotDePasse");

        // THEN
        assertFalse(result.isPresent(),
                "Le résultat doit être vide pour un mauvais mot de passe");

        System.out.println("✅ TC-US4 PASS | Login refusé (mauvais MDP)");
    }

    // ─── TC-US5 : Email inexistant ────────────────────────────────────────────
    @Test
    @DisplayName("[TC-US5] authenticate() retourne Optional vide si l'email n'existe pas")
    void TC_US5_authenticate_unknownEmail_returnsEmpty() {
        // GIVEN
        when(userDao.findByEmail("inconnu@example.com"))
                .thenReturn(Optional.empty());

        // WHEN
        Optional<User> result = userService.authenticate(
                "inconnu@example.com", "anyPassword");

        // THEN
        assertFalse(result.isPresent(),
                "Le résultat doit être vide pour un email inexistant");

        System.out.println("✅ TC-US5 PASS | Login refusé (email inconnu)");
    }

    // ─── TC-US6 : Mot de passe hashé ─────────────────────────────────────────
    @Test
    @DisplayName("[TC-US6] register() stocke le mot de passe hashé, jamais en clair")
    void TC_US6_register_passwordIsHashed() {
        // GIVEN
        String plainPassword = "MonMotDePasse";
        when(userDao.existsByEmail(anyString())).thenReturn(false);
        when(userDao.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        User created = userService.register("Test", "test@test.com", plainPassword, "Casa");

        // THEN
        assertNotEquals(plainPassword, created.getPassword(),
                "Le mot de passe stocké ne doit JAMAIS être en clair");
        assertTrue(PasswordUtil.verifyPassword(plainPassword, created.getPassword()),
                "Le hash doit correspondre au mot de passe original");

        System.out.println("✅ TC-US6 PASS | MDP hashé correctement");
    }

    // ─── TC-US7 : Mise à jour du profil ──────────────────────────────────────
    @Test
    @DisplayName("[TC-US7] updateProfile() appelle bien userDao.update()")
    void TC_US7_updateProfile_callsDao() {
        // GIVEN
        validUser.setVille("Casablanca");
        when(userDao.update(validUser)).thenReturn(validUser);

        // WHEN
        User updated = userService.updateProfile(validUser);

        // THEN
        assertEquals("Casablanca", updated.getVille());
        verify(userDao, times(1)).update(validUser);

        System.out.println("✅ TC-US7 PASS | Profil mis à jour");
    }
}