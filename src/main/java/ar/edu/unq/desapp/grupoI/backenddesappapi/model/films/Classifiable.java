package ar.edu.unq.desapp.grupoi.backenddesappapi.model.films;

import ar.edu.unq.desapp.grupoi.backenddesappapi.model.reviews.PremiumReview;
import ar.edu.unq.desapp.grupoi.backenddesappapi.model.reviews.PublicReview;
import ar.edu.unq.desapp.grupoi.backenddesappapi.model.reviews.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Classifiable")
public abstract class Classifiable {

    @Id
    private String tconst;

    @Column
    Double rating;


    public Classifiable(String tconst, List<PremiumReview> critics, List<PublicReview> reviews) {
        this.tconst = tconst;

    }

    public double getRating() {
        double totalRatingReviews = 1.1;
        double totalRatingCritics = 2.2;
        int total = 2;

        return (totalRatingCritics + totalRatingReviews) / total;
    }

    public void addRating(List<Review> reviews) {
        double totalRatingReviews = reviews.stream().mapToDouble(Review::getRatingId).sum();
        int total = reviews.size();
        double rating = totalRatingReviews / total;
        this.rating = new BigDecimal(rating).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
