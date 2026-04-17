package ma.skillswap.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.model.entity.Category;
import ma.skillswap.model.entity.Skill;
import ma.skillswap.model.entity.SkillOffer;
import ma.skillswap.model.entity.enums.Level;
import ma.skillswap.service.CategoryService;
import ma.skillswap.service.SkillOfferService;
import ma.skillswap.service.SkillService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ManageSkillsBean implements Serializable {

    private String toastScript;

    public String getToastScript() { return toastScript; }

    @Inject
    private SkillOfferService skillOfferService;

    @Inject
    private SkillService skillService;

    @Inject
    private CategoryService categoryService;

    @Inject
    private AuthBean authBean;

    private List<SkillOffer> myOffers;
    private List<Category> categories;
    private List<Skill> skills;

    // Form fields
    private Long selectedSkillId;
    private Long selectedCategoryId;
    private String descriptionOffre;
    private Level niveau;
    private Long editingOfferId;

    // New skill fields
    private String newSkillTitle;
    private Long newSkillCategoryId;

    @PostConstruct
    public void init() {
        loadMyOffers();
        categories = categoryService.findAll();
        skills = skillService.findAll();
    }

    public void loadMyOffers() {
        if (authBean.isLoggedIn()) {
            myOffers = skillOfferService.findByUser(authBean.getLoggedInUser().getId());
        }
    }

    public void loadSkillsByCategory() {
        if (selectedCategoryId != null && selectedCategoryId > 0) {
            skills = skillService.findByCategory(selectedCategoryId);
        } else {
            skills = skillService.findAll();
        }
    }

    public String saveOffer() {
        try {
            if (editingOfferId != null) {
                SkillOffer offer = skillOfferService.findById(editingOfferId);
                offer.setDescriptionOffre(descriptionOffre);
                offer.setNiveau(niveau);
                if (selectedSkillId != null) {
                    offer.setSkill(skillService.findById(selectedSkillId));
                }
                skillOfferService.update(offer);
                toastScript = "ssToast('success','Offre mise à jour !','Votre offre a été modifiée avec succès.');";
            } else {
                Skill skill = skillService.findById(selectedSkillId);
                SkillOffer offer = new SkillOffer(descriptionOffre, niveau,
                        authBean.getLoggedInUser(), skill);
                skillOfferService.save(offer);
                toastScript = "ssToast('success','Offre publiée !','Votre compétence est maintenant visible dans le catalogue.');";
            }
            resetForm();
            loadMyOffers();
        } catch (Exception e) {
            toastScript = "ssToast('error','Erreur','" + e.getMessage() + "');";
        }
        return null;
    }
    public String createNewSkill() {
        try {
            Category category = categoryService.findById(newSkillCategoryId);
            Skill skill = new Skill(newSkillTitle, category);
            skillService.save(skill);
            loadSkillsByCategory();
            toastScript = "ssToast('success','Compétence créée !','" + newSkillTitle + " a été ajoutée avec succès.');";
            newSkillTitle = null;
            newSkillCategoryId = null;
        } catch (Exception e) {
            toastScript = "ssToast('error','Erreur','" + e.getMessage() + "');";
        }
        return null;
    }
    public void editOffer(SkillOffer offer) {
        editingOfferId = offer.getId();
        descriptionOffre = offer.getDescriptionOffre();
        niveau = offer.getNiveau();
        selectedSkillId = offer.getSkill().getId();
        selectedCategoryId = offer.getSkill().getCategory().getId();
    }

    public void deleteOffer(Long offerId) {
        try {
            skillOfferService.deleteById(offerId);
            toastScript = "ssToast('success','Offre supprimée !','Votre offre a été supprimée avec succès.');";
            loadMyOffers();
        } catch (Exception e) {
            toastScript = "ssToast('error','Erreur','" + e.getMessage() + "');";
        }
    }

    public void resetForm() {
        editingOfferId = null;
        descriptionOffre = null;
        niveau = null;
        selectedSkillId = null;
        selectedCategoryId = null;
    }

    public Level[] getLevels() {
        return Level.values();
    }

    // Getters & Setters
    public List<SkillOffer> getMyOffers() { return myOffers; }

    public List<Category> getCategories() { return categories; }

    public List<Skill> getSkills() { return skills; }

    public Long getSelectedSkillId() { return selectedSkillId; }
    public void setSelectedSkillId(Long selectedSkillId) { this.selectedSkillId = selectedSkillId; }

    public Long getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(Long selectedCategoryId) { this.selectedCategoryId = selectedCategoryId; }

    public String getDescriptionOffre() { return descriptionOffre; }
    public void setDescriptionOffre(String descriptionOffre) { this.descriptionOffre = descriptionOffre; }

    public Level getNiveau() { return niveau; }
    public void setNiveau(Level niveau) { this.niveau = niveau; }

    public Long getEditingOfferId() { return editingOfferId; }

    public String getNewSkillTitle() { return newSkillTitle; }
    public void setNewSkillTitle(String newSkillTitle) { this.newSkillTitle = newSkillTitle; }

    public Long getNewSkillCategoryId() { return newSkillCategoryId; }
    public void setNewSkillCategoryId(Long newSkillCategoryId) { this.newSkillCategoryId = newSkillCategoryId; }
}
