package ma.skillswap.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.skillswap.dao.SwapRequestDao;
import ma.skillswap.dao.UserDao;
import ma.skillswap.model.entity.SkillOffer;
import ma.skillswap.model.entity.SwapRequest;
import ma.skillswap.model.entity.User;
import ma.skillswap.model.entity.enums.SwapStatus;

import java.util.List;

@ApplicationScoped
public class SwapRequestService {

    @Inject
    private SwapRequestDao swapRequestDao;

    @Inject
    private UserDao userDao;

    public SwapRequest createRequest(User requester, SkillOffer skillOffer) {
        // Vérifier que le requester ne demande pas sa propre offre
        if (requester.getId().equals(skillOffer.getUser().getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas demander votre propre compétence.");
        }

        // Vérifier que le requester a assez de crédits
        if (requester.getSoldeHeures() < 1) {
            throw new IllegalArgumentException("Solde de crédits insuffisant.");
        }

        SwapRequest request = new SwapRequest(requester, skillOffer.getUser(), skillOffer);
        return swapRequestDao.save(request);
    }

    public SwapRequest acceptRequest(Long requestId) {
        SwapRequest request = swapRequestDao.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }
        request.setStatut(SwapStatus.ACCEPTED);
        return swapRequestDao.update(request);
    }

    public SwapRequest rejectRequest(Long requestId) {
        SwapRequest request = swapRequestDao.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }
        request.setStatut(SwapStatus.REJECTED);
        return swapRequestDao.update(request);
    }

    public SwapRequest completeRequest(Long requestId) {
        SwapRequest request = swapRequestDao.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }
        if (request.getStatut() != SwapStatus.ACCEPTED) {
            throw new IllegalStateException("La demande doit être acceptée avant d'être complétée.");
        }

        // Transfert de crédits
        User requester = request.getRequester();
        User provider = request.getProvider();

        requester.setSoldeHeures(requester.getSoldeHeures() - 1);
        provider.setSoldeHeures(provider.getSoldeHeures() + 1);

        userDao.update(requester);
        userDao.update(provider);

        request.setStatut(SwapStatus.COMPLETED);
        return swapRequestDao.update(request);
    }

    public SwapRequest findById(Long id) {
        return swapRequestDao.findById(id);
    }

    public List<SwapRequest> findByRequester(Long userId) {
        return swapRequestDao.findByRequester(userId);
    }

    public List<SwapRequest> findByProvider(Long userId) {
        return swapRequestDao.findByProvider(userId);
    }

    public Long countCompletedByUser(Long userId) {
        return swapRequestDao.countByUserAndStatus(userId, SwapStatus.COMPLETED);
    }
}