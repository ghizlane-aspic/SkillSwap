package ma.skillswap.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import ma.skillswap.model.entity.Review;

import java.util.List;

@ApplicationScoped
public class ReviewDao extends GenericDao<Review, Long> {

    public ReviewDao() {
        super(Review.class);
    }

    public List<Review> findByProvider(Long providerId) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Review.findByProvider", Review.class)
                    .setParameter("providerId", providerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Double getAverageRatingByProvider(Long providerId) {
        EntityManager em = getEntityManager();
        try {
            Double avg = em.createNamedQuery("Review.averageByProvider", Double.class)
                    .setParameter("providerId", providerId)
                    .getSingleResult();
            return avg != null ? avg : 0.0;
        } finally {
            em.close();
        }
    }
}
