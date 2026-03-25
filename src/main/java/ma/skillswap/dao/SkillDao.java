package ma.skillswap.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import ma.skillswap.model.entity.Skill;

import java.util.List;

@ApplicationScoped
public class SkillDao extends GenericDao<Skill, Long> {

    public SkillDao() {
        super(Skill.class);
    }

    public List<Skill> findByCategory(Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Skill.findByCategory", Skill.class)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Skill> searchByTitle(String keyword) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Skill.findByTitle", Skill.class)
                    .setParameter("title", "%" + keyword + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
