package ma.skillswap.util;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import ma.skillswap.dao.CategoryDao;
import ma.skillswap.model.entity.Category;
import ma.skillswap.model.entity.Skill;
import ma.skillswap.dao.SkillDao;

import java.util.List;

@Singleton
@Startup
public class DataInitializer {

    @Inject
    private CategoryDao categoryDao;

    @Inject
    private SkillDao skillDao;

    @PostConstruct
    public void init() {
        // Check if data already exists
        List<Category> existing = categoryDao.findAll();
        if (!existing.isEmpty()) {
            return; // Data already seeded
        }

        // Create categories
        Category informatique = new Category("Informatique");
        Category langues = new Category("Langues");
        Category arts = new Category("Arts");
        Category musique = new Category("Musique");
        Category soutienScolaire = new Category("Soutien Scolaire");
        Category cuisine = new Category("Cuisine");
        Category sport = new Category("Sport & Bien-être");

        categoryDao.save(informatique);
        categoryDao.save(langues);
        categoryDao.save(arts);
        categoryDao.save(musique);
        categoryDao.save(soutienScolaire);
        categoryDao.save(cuisine);
        categoryDao.save(sport);

        // Create skills
        // Informatique
        skillDao.save(new Skill("Java", informatique));
        skillDao.save(new Skill("Python", informatique));
        skillDao.save(new Skill("JavaScript", informatique));
        skillDao.save(new Skill("HTML/CSS", informatique));
        skillDao.save(new Skill("SQL", informatique));
        skillDao.save(new Skill("React", informatique));

        // Langues
        skillDao.save(new Skill("Anglais", langues));
        skillDao.save(new Skill("Français", langues));
        skillDao.save(new Skill("Espagnol", langues));
        skillDao.save(new Skill("Arabe", langues));
        skillDao.save(new Skill("Allemand", langues));

        // Arts
        skillDao.save(new Skill("Dessin", arts));
        skillDao.save(new Skill("Peinture", arts));
        skillDao.save(new Skill("Photographie", arts));

        // Musique
        skillDao.save(new Skill("Guitare", musique));
        skillDao.save(new Skill("Piano", musique));
        skillDao.save(new Skill("Chant", musique));

        // Soutien Scolaire
        skillDao.save(new Skill("Mathématiques", soutienScolaire));
        skillDao.save(new Skill("Physique", soutienScolaire));
        skillDao.save(new Skill("Chimie", soutienScolaire));

        // Cuisine
        skillDao.save(new Skill("Pâtisserie", cuisine));
        skillDao.save(new Skill("Cuisine marocaine", cuisine));

        // Sport
        skillDao.save(new Skill("Yoga", sport));
        skillDao.save(new Skill("Fitness", sport));

        System.out.println("✅ SkillSwap - Données initiales chargées avec succès !");
    }
}
