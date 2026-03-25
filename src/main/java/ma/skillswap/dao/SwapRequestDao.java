package ma.skillswap.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import ma.skillswap.model.entity.SwapRequest;
import ma.skillswap.model.entity.enums.SwapStatus;

import java.util.List;

@ApplicationScoped
public class SwapRequestDao extends GenericDao<SwapRequest, Long> {

    public SwapRequestDao() {
        super(SwapRequest.class);
    }

    public List<SwapRequest> findByRequester(Long userId) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("SwapRequest.findByRequester", SwapRequest.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<SwapRequest> findByProvider(Long userId) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("SwapRequest.findByProvider", SwapRequest.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Long countByUserAndStatus(Long userId, SwapStatus status) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("SwapRequest.countByUser", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("status", status)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
