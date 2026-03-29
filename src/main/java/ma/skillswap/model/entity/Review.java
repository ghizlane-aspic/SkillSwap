package ma.skillswap.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reviews")
@NamedQueries({
        @NamedQuery(name = "Review.findByProvider",
                query = "SELECT r FROM Review r JOIN FETCH r.swapRequest JOIN FETCH r.swapRequest.requester JOIN FETCH r.swapRequest.skillOffer JOIN FETCH r.swapRequest.skillOffer.skill WHERE r.swapRequest.provider.id = :providerId ORDER BY r.id DESC"),
        @NamedQuery(name = "Review.averageByProvider",
                query = "SELECT AVG(r.note) FROM Review r WHERE r.swapRequest.provider.id = :providerId")
})
public class Review implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer note; // 1 to 5

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_request_id", nullable = false, unique = true)
    private SwapRequest swapRequest;

    // Constructors
    public Review() {}

    public Review(Integer note, String commentaire, SwapRequest swapRequest) {
        this.note = note;
        this.commentaire = commentaire;
        this.swapRequest = swapRequest;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public SwapRequest getSwapRequest() { return swapRequest; }
    public void setSwapRequest(SwapRequest swapRequest) { this.swapRequest = swapRequest; }
}