package ma.skillswap.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.model.entity.Review;
import ma.skillswap.model.entity.SkillOffer;
import ma.skillswap.model.entity.SwapRequest;
import ma.skillswap.model.entity.User;
import ma.skillswap.service.ReviewService;
import ma.skillswap.service.SkillOfferService;
import ma.skillswap.service.SwapRequestService;
import ma.skillswap.service.UserService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProfileBean implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private SkillOfferService skillOfferService;

    @Inject
    private ReviewService reviewService;

    @Inject
    private SwapRequestService swapRequestService;

    @Inject
    private AuthBean authBean;

    private User user;
    private List<SkillOffer> userOffers;
    private List<Review> userReviews;
    private Double averageRating;
    private Long completedSwaps;

    // Edit mode
    private boolean editMode = false;

    // Review form
    private Long reviewSwapId;
    private Integer reviewNote;
    private String reviewComment;

    @PostConstruct
    public void init() {
        if (authBean.isLoggedIn()) {
            user = userService.findById(authBean.getLoggedInUser().getId());
            loadProfileData();

            // Check if coming from review link
            String swapId = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("reviewSwapId");
            if (swapId != null) {
                reviewSwapId = Long.parseLong(swapId);
            }
        }
    }

    private void loadProfileData() {
        userOffers = skillOfferService.findByUser(user.getId());
        userReviews = reviewService.findByProvider(user.getId());
        averageRating = reviewService.getAverageRating(user.getId());
        completedSwaps = swapRequestService.countCompletedByUser(user.getId());
    }

    public String updateProfile() {
        try {
            userService.updateProfile(user);
            authBean.refreshUser();
            editMode = false;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Profil mis à jour avec succès !", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage(), null));
        }
        return null;
    }

    public String submitReview() {
        try {
            SwapRequest swapRequest = swapRequestService.findById(reviewSwapId);
            reviewService.createReview(reviewNote, reviewComment, swapRequest);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Avis publié avec succès !", null));
            reviewSwapId = null;
            reviewNote = null;
            reviewComment = null;
            loadProfileData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
        return null;
    }

    public void toggleEditMode() {
        editMode = !editMode;
    }

    // Getters & Setters
    public User getUser() { return user; }

    public List<SkillOffer> getUserOffers() { return userOffers; }

    public List<Review> getUserReviews() { return userReviews; }

    public Double getAverageRating() { return averageRating; }

    public Long getCompletedSwaps() { return completedSwaps; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }

    public Long getReviewSwapId() { return reviewSwapId; }
    public void setReviewSwapId(Long reviewSwapId) { this.reviewSwapId = reviewSwapId; }

    public Integer getReviewNote() { return reviewNote; }
    public void setReviewNote(Integer reviewNote) { this.reviewNote = reviewNote; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public String getStars(Integer note) {
        if (note == null) return "";
        return "★".repeat(note) + "☆".repeat(5 - note);
    }
}
