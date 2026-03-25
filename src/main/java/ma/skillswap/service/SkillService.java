package ma.skillswap.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.skillswap.dao.SkillDao;
import ma.skillswap.model.entity.Skill;

import java.util.List;

@ApplicationScoped
public class SkillService {

    @Inject
    private SkillDao skillDao;

    public List<Skill> findAll() {
        return skillDao.findAll();
    }

    public Skill findById(Long id) {
        return skillDao.findById(id);
    }

    public List<Skill> findByCategory(Long categoryId) {
        return skillDao.findByCategory(categoryId);
    }

    public Skill save(Skill skill) {
        return skillDao.save(skill);
    }

    public List<Skill> searchByTitle(String keyword) {
        return skillDao.searchByTitle(keyword);
    }
}