package ma.skillswap.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.skillswap.dao.MessageDao;
import ma.skillswap.model.entity.Message;
import ma.skillswap.model.entity.SwapRequest;
import ma.skillswap.model.entity.User;

import java.util.List;

@ApplicationScoped
public class MessageService {

    @Inject
    private MessageDao messageDao;

    public Message sendMessage(String contenu, User sender, SwapRequest swapRequest) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Le message ne peut pas être vide.");
        }

        // Vérifier que l'expéditeur fait partie de la conversation
        boolean isParticipant = sender.getId().equals(swapRequest.getRequester().getId())
                || sender.getId().equals(swapRequest.getProvider().getId());
        if (!isParticipant) {
            throw new IllegalArgumentException("Vous ne faites pas partie de cet échange.");
        }

        Message message = new Message(contenu, sender, swapRequest);
        return messageDao.save(message);
    }

    public List<Message> findBySwapRequest(Long swapRequestId) {
        return messageDao.findBySwapRequest(swapRequestId);
    }
}