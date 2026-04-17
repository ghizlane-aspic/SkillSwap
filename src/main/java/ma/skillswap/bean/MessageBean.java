package ma.skillswap.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.model.entity.Message;
import ma.skillswap.model.entity.SwapRequest;
import ma.skillswap.service.MessageService;
import ma.skillswap.service.SwapRequestService;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class MessageBean implements Serializable {

    @Inject
    private MessageService messageService;

    @Inject
    private SwapRequestService swapRequestService;

    @Inject
    private AuthBean authBean;

    private Long swapRequestId;
    private SwapRequest swapRequest;
    private List<Message> messages;
    private String messageContent;

    @PostConstruct
    public void init() {
        // Get the swapRequestId from request parameters
        String requestParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("swapRequestId");

        if (requestParam != null && !requestParam.isEmpty()) {
            try {
                swapRequestId = Long.parseLong(requestParam);
                loadSwapRequest();
            } catch (NumberFormatException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "ID d'échange invalide", null));
            }
        }
    }

    private void loadSwapRequest() {
        if (swapRequestId != null) {
            swapRequest = swapRequestService.findById(swapRequestId);
            if (swapRequest != null) {
                // Verify the user is a participant in this swap
                Long currentUserId = authBean.getLoggedInUser().getId();
                if (!isUserParticipant(currentUserId)) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Vous n'avez pas accès à cette conversation", null));
                    swapRequest = null;
                    return;
                }
                loadMessages();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Échange non trouvé", null));
            }
        }
    }

    private boolean isUserParticipant(Long userId) {
        if (swapRequest == null) return false;
        return userId.equals(swapRequest.getRequester().getId()) ||
               userId.equals(swapRequest.getProvider().getId());
    }

    private void loadMessages() {
        messages = messageService.findBySwapRequest(swapRequestId);
        // Initialiser les objets lazy pour éviter les erreurs de proxy
        if (messages != null) {
            for (Message msg : messages) {
                // Accéder aux propriétés de l'expéditeur pour les initialiser
                if (msg.getSender() != null) {
                    msg.getSender().getId();
                    msg.getSender().getNom();
                }
            }
        }
    }

    public void sendMessage() {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Le message ne peut pas être vide", null));
            return;
        }

        if (swapRequest == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Échange non valide", null));
            return;
        }

        try {
            messageService.sendMessage(messageContent.trim(), authBean.getLoggedInUser(), swapRequest);
            messageContent = "";
            loadMessages();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Message envoyé", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage(), null));
        }
    }

    // Getters and Setters
    public Long getSwapRequestId() { return swapRequestId; }
    public void setSwapRequestId(Long swapRequestId) { this.swapRequestId = swapRequestId; }

    public SwapRequest getSwapRequest() { return swapRequest; }

    public List<Message> getMessages() { return messages; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public boolean isSwapRequestValid() {
        return swapRequest != null;
    }

    public String getSenderName(Message message) {
        if (message.getSender().getId().equals(authBean.getLoggedInUser().getId())) {
            return "Vous";
        }
        return message.getSender().getNom();
    }

    public String getSenderStyle(Message message) {
        if (message.getSender().getId().equals(authBean.getLoggedInUser().getId())) {
            return "message-sent";
        }
        return "message-received";
    }
}


