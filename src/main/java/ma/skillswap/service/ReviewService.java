package ma.skillswap.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.skillswap.dao.ReviewDao;
import ma.skillswap.model.entity.Review;
import ma.skillswap.model.entity.SwapRequest;
import ma.skillswap.model.entity.enums.SwapStatus;

import java.util.List;

@ApplicationScoped
public class ReviewService {

    @Inject
    private ReviewDao reviewDao;

    public Review createReview(Integer note, String commentaire, SwapRequest swapRequest) {
        if (swapRequest.getStatut() != SwapStatus.COMPLETED) {
            throw new IllegalStateException("L'échange doit être terminé pour laisser un avis.");
        }
        if (swapRequest.getReview() != null) {
            throw new IllegalStateException("Un avis a déjà été laissé pour cet échange.");
        }
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5.");
        }

        Review review = new Review(note, commentaire, swapRequest);
        return reviewDao.save(review);
    }

    public List<Review> findByProvider(Long providerId) {
        return reviewDao.findByProvider(providerId);
    }

    public Double getAverageRating(Long providerId) {
        return reviewDao.getAverageRatingByProvider(providerId);
    }
}