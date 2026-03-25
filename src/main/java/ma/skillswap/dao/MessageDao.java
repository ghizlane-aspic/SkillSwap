package ma.skillswap.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import ma.skillswap.model.entity.Message;

import java.util.List;

@ApplicationScoped
public class MessageDao extends GenericDao<Message, Long> {

    public MessageDao() {
        super(Message.class);
    }

    public List<Message> findBySwapRequest(Long swapRequestId) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Message.findBySwapRequest", Message.class)
                    .setParameter("swapRequestId", swapRequestId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}