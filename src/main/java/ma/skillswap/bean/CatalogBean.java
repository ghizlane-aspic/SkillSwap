package ma.skillswap.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.model.entity.Category;
import ma.skillswap.model.entity.SkillOffer;
import ma.skillswap.service.CategoryService;
import ma.skillswap.service.SkillOfferService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CatalogBean implements Serializable {

    @Inject
    private SkillOfferService skillOfferService;

    @Inject
    private CategoryService categoryService;

    private List<SkillOffer> offers;
    private List<Category> categories;
    private String searchKeyword;
    private Long selectedCategoryId;

    @PostConstruct
    public void init() {
        loadOffers();
        categories = categoryService.findAll();
    }

    public void loadOffers() {
        offers = skillOfferService.findAll();
    }

    public void search() {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            offers = skillOfferService.search(searchKeyword.trim());
        } else {
            loadOffers();
        }
    }

    public void filterByCategory() {
        if (selectedCategoryId != null && selectedCategoryId > 0) {
            offers = skillOfferService.findByCategory(selectedCategoryId);
        } else {
            loadOffers();
        }
    }

    public void resetFilters() {
        searchKeyword = null;
        selectedCategoryId = null;
        loadOffers();
    }

    // Getters & Setters
    public List<SkillOffer> getOffers() { return offers; }
    public void setOffers(List<SkillOffer> offers) { this.offers = offers; }

    public List<Category> getCategories() { return categories; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public Long getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(Long selectedCategoryId) { this.selectedCategoryId = selectedCategoryId; }
}
