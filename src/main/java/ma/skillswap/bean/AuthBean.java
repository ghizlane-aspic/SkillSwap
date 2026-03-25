package ma.skillswap.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.model.entity.User;
import ma.skillswap.service.UserService;

import java.io.Serializable;
import java.util.Optional;

@Named
@SessionScoped
public class AuthBean implements Serializable {

    @Inject
    private UserService userService;

    private String email;
    private String password;
    private User loggedInUser;

    public String login() {
        Optional<User> userOpt = userService.authenticate(email, password);
        if (userOpt.isPresent()) {
            loggedInUser = userOpt.get();
            // Stocker dans la session HTTP
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("loggedInUser", loggedInUser);
            return "/my-swaps.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email ou mot de passe incorrect.", null));
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        loggedInUser = null;
        return "/index.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public void refreshUser() {
        if (loggedInUser != null) {
            loggedInUser = userService.findById(loggedInUser.getId());
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("loggedInUser", loggedInUser);
        }
    }

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User getLoggedInUser() { return loggedInUser; }
    public void setLoggedInUser(User loggedInUser) { this.loggedInUser = loggedInUser; }
}
