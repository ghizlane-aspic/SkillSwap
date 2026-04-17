package ma.skillswap.unit.service;

import ma.skillswap.dao.MessageDao;
import ma.skillswap.model.entity.*;
import ma.skillswap.model.entity.enums.SwapStatus;
import ma.skillswap.service.MessageService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TESTS - MessageService                                      ║
 * ║  Fonctionnalité : Messagerie entre participants              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * SCÉNARIOS DE TEST :
 * ┌─────┬─────────────────────────────────────────┬──────────────────────────┬──────────┐
 * │ ID  │ Description                             │ Résultat Attendu         │ Statut   │
 * ├─────┼─────────────────────────────────────────┼──────────────────────────┼──────────┤
 * │ MS1 │ Envoyer un message valide               │ Message sauvegardé       │ ✅ PASS  │
 * │ MS2 │ Envoyer un message vide                 │ IllegalArgumentException │ ✅ PASS  │
 * │ MS3 │ Envoyer par un non-participant          │ IllegalArgumentException │ ✅ PASS  │
 * │ MS4 │ Le provider peut répondre               │ Message sauvegardé       │ ✅ PASS  │
 * └─────┴─────────────────────────────────────────┴──────────────────────────┴──────────┘
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests MessageService - Messagerie")
class MessageServiceTest {

    @Mock private MessageDao messageDao;

    @InjectMocks
    private MessageService messageService;

    private User requester;
    private User provider;
    private User stranger;
    private SwapRequest swapRequest;

    @BeforeEach
    void setUp() {
        requester = new User("Aya", "aya@test.com", "hash", "Tanger");
        requester.setId(1L);

        provider = new User("Ghizlane", "ghizlane@test.com", "hash", "Fes");
        provider.setId(2L);

        stranger = new User("Inconnu", "stranger@test.com", "hash", "Rabat");
        stranger.setId(99L);

        swapRequest = new SwapRequest(requester, provider, new SkillOffer());
        swapRequest.setId(1L);
        swapRequest.setStatut(SwapStatus.ACCEPTED);
    }

    // ─── TC-MS1 : Message valide du requester ────────────────────────────────
    @Test
    @DisplayName("[TC-MS1] sendMessage() sauvegarde un message valide du requester")
    void TC_MS1_sendMessage_validMessage_saved() {
        // GIVEN
        Message expected = new Message("Bonjour, quand êtes-vous disponible ?",
                requester, swapRequest);
        when(messageDao.save(any(Message.class))).thenReturn(expected);

        // WHEN
        Message result = messageService.sendMessage(
                "Bonjour, quand êtes-vous disponible ?", requester, swapRequest);

        // THEN
        assertNotNull(result);
        assertEquals("Bonjour, quand êtes-vous disponible ?", result.getContenu());
        verify(messageDao, times(1)).save(any(Message.class));

        System.out.println("✅ TC-MS1 PASS | Message envoyé : " + result.getContenu());
    }

    // ─── TC-MS2 : Message vide ────────────────────────────────────────────────
    @Test
    @DisplayName("[TC-MS2] sendMessage() rejette un message vide ou blanc")
    void TC_MS2_sendMessage_emptyContent_throwsException() {
        // WHEN / THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> messageService.sendMessage("   ", requester, swapRequest),
                "Un message vide doit être rejeté"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> messageService.sendMessage("", requester, swapRequest),
                "Un message vide doit être rejeté"
        );

        verify(messageDao, never()).save(any());

        System.out.println("✅ TC-MS2 PASS | Messages vides rejetés");
    }

    // ─── TC-MS3 : Non-participant ─────────────────────────────────────────────
    @Test
    @DisplayName("[TC-MS3] sendMessage() rejette si l'expéditeur n'est pas participant")
    void TC_MS3_sendMessage_notParticipant_throwsException() {
        // WHEN / THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> messageService.sendMessage("Message de l'intrus", stranger, swapRequest),
                "Un non-participant ne peut pas envoyer de message"
        );

        System.out.println("✅ TC-MS3 PASS | Non-participant rejeté");
    }

    // ─── TC-MS4 : Le provider peut répondre ──────────────────────────────────
    @Test
    @DisplayName("[TC-MS4] sendMessage() accepte un message du provider")
    void TC_MS4_sendMessage_providerCanReply() {
        // GIVEN
        Message expected = new Message("Je suis disponible samedi !", provider, swapRequest);
        when(messageDao.save(any(Message.class))).thenReturn(expected);

        // WHEN
        Message result = messageService.sendMessage(
                "Je suis disponible samedi !", provider, swapRequest);

        // THEN
        assertNotNull(result);
        assertEquals(provider, result.getSender());

        System.out.println("✅ TC-MS4 PASS | Provider peut répondre");
    }
}