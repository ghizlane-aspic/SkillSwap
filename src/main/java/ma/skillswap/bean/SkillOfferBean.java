package ma.skillswap.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.model.entity.Review;
import ma.skillswap.model.entity.SkillOffer;
import ma.skillswap.model.entity.User;
import ma.skillswap.service.ReviewService;
import ma.skillswap.service.SkillOfferService;
import ma.skillswap.service.SwapRequestService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class SkillOfferBean implements Serializable {

    @Inject
    private SkillOfferService skillOfferService;

    @Inject
    private SwapRequestService swapRequestService;

    @Inject
    private ReviewService reviewService;

    @Inject
    private AuthBean authBean;

    private Long offerId;
    private SkillOffer offer;
    private List<Review> providerReviews;
    private Double providerAverageRating;

    @PostConstruct
    public void init() {
        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (id != null) {
            offerId = Long.parseLong(id);
            loadOffer();
        }
    }

    public void loadOffer() {
        if (offerId != null) {
            offer = skillOfferService.findById(offerId);
            if (offer != null) {
                providerReviews = reviewService.findByProvider(offer.getUser().getId());
                providerAverageRating = reviewService.getAverageRating(offer.getUser().getId());
            }
        }
    }

    public String requestSwap() {
        if (!authBean.isLoggedIn()) {
            return "/login.xhtml?faces-redirect=true";
        }

        try {
            User requester = authBean.getLoggedInUser();
            swapRequestService.createRequest(requester, offer);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Demande de swap envoyée avec succès !", null));
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
        return null;
    }

    public boolean isOwnOffer() {
        if (authBean.isLoggedIn() && offer != null) {
            return authBean.getLoggedInUser().getId().equals(offer.getUser().getId());
        }
        return false;
    }

    // Getters & Setters
    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }

    public SkillOffer getOffer() { return offer; }

    public List<Review> getProviderReviews() { return providerReviews; }

    public Double getProviderAverageRating() { return providerAverageRating; }
    public String getStars(Integer note) {
        if (note == null) return "";
        return "★".repeat(note) + "☆".repeat(5 - note);
    }
}
