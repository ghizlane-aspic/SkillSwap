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
import java.util.stream.Collectors;

@Named
@ViewScoped
public class CatalogBean implements Serializable {

    @Inject
    private SkillOfferService skillOfferService;

    @Inject
    private CategoryService categoryService;

    @Inject
    private AuthBean authBean;

    private List<SkillOffer> myOffers;
    private List<SkillOffer> otherOffers;
    private List<Category> categories;
    private String searchKeyword;
    private Long selectedCategoryId;

    @PostConstruct
    public void init() {
        loadOffers();
        categories = categoryService.findAll();
    }

    public void loadOffers() {
        List<SkillOffer> allOffers = skillOfferService.findAll();
        if (authBean.isLoggedIn()) {
            Long userId = authBean.getLoggedInUser().getId();
            myOffers = allOffers.stream()
                    .filter(offer -> offer.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
            otherOffers = allOffers.stream()
                    .filter(offer -> !offer.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            myOffers = null;
            otherOffers = allOffers;
        }
    }

    public void search() {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            List<SkillOffer> searched = skillOfferService.search(searchKeyword.trim());
            applyFiltersToLists(searched);
        } else {
            loadOffers();
        }
    }

    public void filterByCategory() {
        if (selectedCategoryId != null && selectedCategoryId > 0) {
            List<SkillOffer> filtered = skillOfferService.findByCategory(selectedCategoryId);
            applyFiltersToLists(filtered);
        } else {
            loadOffers();
        }
    }

    private void applyFiltersToLists(List<SkillOffer> allFiltered) {
        if (authBean.isLoggedIn()) {
            Long userId = authBean.getLoggedInUser().getId();
            myOffers = allFiltered.stream()
                    .filter(offer -> offer.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
            otherOffers = allFiltered.stream()
                    .filter(offer -> !offer.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            myOffers = null;
            otherOffers = allFiltered;
        }
    }

    public void resetFilters() {
        searchKeyword = null;
        selectedCategoryId = null;
        loadOffers();
    }

    // Getters & Setters
    public List<SkillOffer> getMyOffers() { return myOffers; }

    public List<SkillOffer> getOtherOffers() { return otherOffers; }

    public List<Category> getCategories() { return categories; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public Long getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(Long selectedCategoryId) { this.selectedCategoryId = selectedCategoryId; }
}
