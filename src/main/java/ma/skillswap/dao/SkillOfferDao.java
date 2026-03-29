package ma.skillswap.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import ma.skillswap.model.entity.SkillOffer;

import java.util.List;

@ApplicationScoped
public class SkillOfferDao extends GenericDao<SkillOffer, Long> {

    public SkillOfferDao() {
        super(SkillOffer.class);
    }

    // OVERRIDE findById pour charger les associations
    @Override
    public SkillOffer findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            List<SkillOffer> result = em.createQuery(
                            "SELECT so FROM SkillOffer so JOIN FETCH so.skill s JOIN FETCH s.category JOIN FETCH so.user WHERE so.id = :id",
                            SkillOffer.class)
                    .setParameter("id", id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    public List<SkillOffer> findByUser(Long userId) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("SkillOffer.findByUser", SkillOffer.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<SkillOffer> findByCategory(Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("SkillOffer.findByCategory", SkillOffer.class)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<SkillOffer> search(String keyword) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("SkillOffer.search", SkillOffer.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
