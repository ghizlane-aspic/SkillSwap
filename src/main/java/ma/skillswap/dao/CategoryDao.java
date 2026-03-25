package ma.skillswap.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import ma.skillswap.model.entity.Category;

import java.util.Optional;

@ApplicationScoped
public class CategoryDao extends GenericDao<Category, Long> {

    public CategoryDao() {
        super(Category.class);
    }

    public Optional<Category> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            Category cat = em.createNamedQuery("Category.findByName", Category.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(cat);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}