package ma.skillswap.util;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║  TESTS - PasswordUtil                                   ║
 * ║  Fonctionnalité : Hashage et vérification des mots      ║
 * ║                   de passe                              ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * CAS DE TEST :
 * ┌────┬──────────────────────────────┬────────────────────────────┬──────────┐
 * │ ID │ Description                  │ Résultat Attendu           │ Statut   │
 * ├────┼──────────────────────────────┼────────────────────────────┼──────────┤
 * │ P1 │ Hash non null                │ String non vide            │ ✅ PASS  │
 * │ P2 │ Hash ≠ mot de passe original │ Chaînes différentes        │ ✅ PASS  │
 * │ P3 │ Même MDP → même hash         │ Déterministe               │ ✅ PASS  │
 * │ P4 │ Vérification correcte        │ true                       │ ✅ PASS  │
 * │ P5 │ Vérification incorrecte      │ false                      │ ✅ PASS  │
 * │ P6 │ Mots de passe variés         │ Tous hashés sans exception │ ✅ PASS  │
 * └────┴──────────────────────────────┴────────────────────────────┴──────────┘
 */
@DisplayName("Tests PasswordUtil - Sécurité des mots de passe")
class PasswordUtilTest {

    // ─── TC-P1 : Le hash ne doit pas être null ───────────────────────────────
    @Test
    @DisplayName("[TC-P1] hashPassword() ne retourne pas null")
    void TC_P1_hashPassword_notNull() {
        // GIVEN
        String password = "MonMotDePasse123";

        // WHEN
        String hash = PasswordUtil.hashPassword(password);

        // THEN
        assertNotNull(hash, "Le hash ne doit pas être null");
        assertFalse(hash.isBlank(), "Le hash ne doit pas être vide");

        System.out.println("✅ TC-P1 PASS | Hash généré : " + hash.substring(0, 10) + "...");
    }

    // ─── TC-P2 : Le hash doit être différent du mot de passe original ─────────
    @Test
    @DisplayName("[TC-P2] hashPassword() ne retourne pas le mot de passe en clair")
    void TC_P2_hashPassword_notEqualToOriginal() {
        // GIVEN
        String password = "SkillSwap2024!";

        // WHEN
        String hash = PasswordUtil.hashPassword(password);

        // THEN
        assertNotEquals(password, hash,
                "Le hash ne doit JAMAIS être identique au mot de passe en clair");

        System.out.println("✅ TC-P2 PASS | MDP original ≠ hash");
    }

    // ─── TC-P3 : Le hash doit être déterministe ───────────────────────────────
    @Test
    @DisplayName("[TC-P3] hashPassword() est déterministe - même input = même output")
    void TC_P3_hashPassword_isDeterministic() {
        // GIVEN
        String password = "password123";

        // WHEN
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        // THEN
        assertEquals(hash1, hash2,
                "Le même mot de passe doit toujours produire le même hash");

        System.out.println("✅ TC-P3 PASS | Déterminisme confirmé");
    }

    // ─── TC-P4 : Vérification d'un mot de passe correct ──────────────────────
    @Test
    @DisplayName("[TC-P4] verifyPassword() retourne true pour un mot de passe correct")
    void TC_P4_verifyPassword_correctPassword_returnsTrue() {
        // GIVEN
        String plainPassword = "AyaHassoun2024";
        String hashed = PasswordUtil.hashPassword(plainPassword);

        // WHEN
        boolean result = PasswordUtil.verifyPassword(plainPassword, hashed);

        // THEN
        assertTrue(result, "La vérification doit retourner true pour un mot de passe correct");

        System.out.println("✅ TC-P4 PASS | Vérification correcte");
    }

    // ─── TC-P5 : Vérification d'un mot de passe incorrect ────────────────────
    @Test
    @DisplayName("[TC-P5] verifyPassword() retourne false pour un mauvais mot de passe")
    void TC_P5_verifyPassword_wrongPassword_returnsFalse() {
        // GIVEN
        String correctPassword = "CorrectPassword";
        String wrongPassword   = "WrongPassword";
        String hashed = PasswordUtil.hashPassword(correctPassword);

        // WHEN
        boolean result = PasswordUtil.verifyPassword(wrongPassword, hashed);

        // THEN
        assertFalse(result, "La vérification doit retourner false pour un mot de passe incorrect");

        System.out.println("✅ TC-P5 PASS | Rejet du mauvais mot de passe");
    }

    // ─── TC-P6 : Test paramétré sur plusieurs mots de passe ──────────────────
    @ParameterizedTest(name = "[TC-P6] Hash de ''{0}''")
    @ValueSource(strings = {
            "simple",
            "Complexe@123!",
            "averylongpasswordthatismorethan30characters",
            "12345678",
            "àéèùîô",
            "P@$$w0rd"
    })
    @DisplayName("[TC-P6] hashPassword() fonctionne pour des mots de passe variés")
    void TC_P6_hashPassword_variousPasswords_noException(String password) {
        // WHEN / THEN
        assertDoesNotThrow(() -> {
            String hash = PasswordUtil.hashPassword(password);
            assertNotNull(hash);
            assertTrue(PasswordUtil.verifyPassword(password, hash));
        }, "Aucune exception ne doit être levée pour le mot de passe : " + password);

        System.out.println("✅ TC-P6 PASS | Password testé : " + password);
    }
}