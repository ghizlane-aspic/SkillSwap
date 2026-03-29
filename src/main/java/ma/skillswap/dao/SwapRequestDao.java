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
    @Override
    public SwapRequest findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            List<SwapRequest> result = em.createQuery(
                            "SELECT sr FROM SwapRequest sr " +
                                    "JOIN FETCH sr.skillOffer so JOIN FETCH so.skill s JOIN FETCH s.category " +
                                    "JOIN FETCH sr.requester JOIN FETCH sr.provider " +
                                    "WHERE sr.id = :id",
                            SwapRequest.class)
                    .setParameter("id", id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
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
