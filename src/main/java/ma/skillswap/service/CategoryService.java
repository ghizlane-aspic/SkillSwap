package ma.skillswap.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.skillswap.dao.CategoryDao;
import ma.skillswap.model.entity.Category;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryService {

    @Inject
    private CategoryDao categoryDao;

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    public Category findById(Long id) {
        return categoryDao.findById(id);
    }

    public Category save(Category category) {
        return categoryDao.save(category);
    }

    public Optional<Category> findByName(String name) {
        return categoryDao.findByName(name);
    }
}