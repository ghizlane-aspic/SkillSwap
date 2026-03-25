package ma.skillswap.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.skillswap.dao.SkillOfferDao;
import ma.skillswap.model.entity.SkillOffer;

import java.util.List;

@ApplicationScoped
public class SkillOfferService {

    @Inject
    private SkillOfferDao skillOfferDao;

    public List<SkillOffer> findAll() {
        return skillOfferDao.findAll();
    }

    public SkillOffer findById(Long id) {
        return skillOfferDao.findById(id);
    }

    public List<SkillOffer> findByUser(Long userId) {
        return skillOfferDao.findByUser(userId);
    }

    public List<SkillOffer> findByCategory(Long categoryId) {
        return skillOfferDao.findByCategory(categoryId);
    }

    public List<SkillOffer> search(String keyword) {
        return skillOfferDao.search(keyword);
    }

    public SkillOffer save(SkillOffer offer) {
        return skillOfferDao.save(offer);
    }

    public SkillOffer update(SkillOffer offer) {
        return skillOfferDao.update(offer);
    }

    public void delete(SkillOffer offer) {
        skillOfferDao.delete(offer);
    }

    public void deleteById(Long id) {
        skillOfferDao.deleteById(id);
    }
}