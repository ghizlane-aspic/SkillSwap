package ma.skillswap.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findAll",
                query = "SELECT u FROM User u ORDER BY u.dateInscription DESC")
})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String ville;

    @Column(columnDefinition = "TEXT")
    private String biographie;

    @Column(name = "solde_heures", nullable = false)
    private Integer soldeHeures = 3;

    @Column(name = "photo_profil")
    private String photoProfil;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillOffer> skillOffers = new ArrayList<>();

    @OneToMany(mappedBy = "requester")
    private List<SwapRequest> sentRequests = new ArrayList<>();

    @OneToMany(mappedBy = "provider")
    private List<SwapRequest> receivedRequests = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (dateInscription == null) {
            dateInscription = LocalDate.now();
        }
        if (soldeHeures == null) {
            soldeHeures = 3;
        }
    }

    // Constructors
    public User() {}

    public User(String nom, String email, String password, String ville) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.ville = ville;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getBiographie() { return biographie; }
    public void setBiographie(String biographie) { this.biographie = biographie; }

    public Integer getSoldeHeures() { return soldeHeures; }
    public void setSoldeHeures(Integer soldeHeures) { this.soldeHeures = soldeHeures; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }

    public List<SkillOffer> getSkillOffers() { return skillOffers; }
    public void setSkillOffers(List<SkillOffer> skillOffers) { this.skillOffers = skillOffers; }

    public List<SwapRequest> getSentRequests() { return sentRequests; }
    public void setSentRequests(List<SwapRequest> sentRequests) { this.sentRequests = sentRequests; }

    public List<SwapRequest> getReceivedRequests() { return receivedRequests; }
    public void setReceivedRequests(List<SwapRequest> receivedRequests) { this.receivedRequests = receivedRequests; }

    public List<Message> getSentMessages() { return sentMessages; }
    public void setSentMessages(List<Message> sentMessages) { this.sentMessages = sentMessages; }
}