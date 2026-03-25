package ma.skillswap.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.skillswap.service.UserService;

@Named
@RequestScoped
public class RegisterBean {

    @Inject
    private UserService userService;

    private String nom;
    private String email;
    private String password;
    private String confirmPassword;
    private String ville;

    public String register() {
        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Les mots de passe ne correspondent pas.", null));
            return null;
        }

        try {
            userService.register(nom, email, password, ville);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Inscription réussie ! Vous pouvez maintenant vous connecter.", null));
            return "/login.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        }
    }

    // Getters & Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
}
