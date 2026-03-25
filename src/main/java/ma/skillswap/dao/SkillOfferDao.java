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
