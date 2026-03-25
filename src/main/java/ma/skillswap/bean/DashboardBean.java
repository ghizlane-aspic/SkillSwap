package ma.skillswap.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.model.entity.SwapRequest;
import ma.skillswap.model.entity.enums.SwapStatus;
import ma.skillswap.service.SwapRequestService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject
    private SwapRequestService swapRequestService;

    @Inject
    private AuthBean authBean;

    private List<SwapRequest> sentRequests;
    private List<SwapRequest> receivedRequests;
    private Long completedCount;

    @PostConstruct
    public void init() {
        loadData();
    }

    public void loadData() {
        if (authBean.isLoggedIn()) {
            Long userId = authBean.getLoggedInUser().getId();
            sentRequests = swapRequestService.findByRequester(userId);
            receivedRequests = swapRequestService.findByProvider(userId);
            completedCount = swapRequestService.countCompletedByUser(userId);
        }
    }

    public void acceptRequest(Long requestId) {
        try {
            swapRequestService.acceptRequest(requestId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Demande acceptée !", null));
            loadData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void rejectRequest(Long requestId) {
        try {
            swapRequestService.rejectRequest(requestId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Demande refusée.", null));
            loadData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void completeRequest(Long requestId) {
        try {
            swapRequestService.completeRequest(requestId);
            authBean.refreshUser();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Échange terminé ! Les crédits ont été transférés.", null));
            loadData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public String getStatusStyleClass(SwapStatus status) {
        return switch (status) {
            case PENDING -> "badge-pending";
            case ACCEPTED -> "badge-accepted";
            case REJECTED -> "badge-rejected";
            case COMPLETED -> "badge-completed";
        };
    }

    // Getters
    public List<SwapRequest> getSentRequests() { return sentRequests; }
    public List<SwapRequest> getReceivedRequests() { return receivedRequests; }
    public Long getCompletedCount() { return completedCount; }
}
