package repository;

import exceptions.ReviewHasBeenAddedException;
import exceptions.UserHasBeenAddedException;
import model.platform.Platform;
import model.reviews.PremiumReview;
import model.reviews.PublicReview;
import model.user.CommonUser;
import model.user.Critic;
import model.user.Type_User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.review.ReviewRepository;
import repository.review.ReviewRepositoryImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReviewRepositoryTest {

    private ReviewRepository reviewRepository;
    private CommonUser jose;
    private Critic carlos;
    private PublicReview review1;
    private PremiumReview critic1;


    @BeforeEach
    public void setUp(){
        this.reviewRepository = new ReviewRepositoryImpl();
        jose = new CommonUser(4,
                Platform.DISNEY_PLUS,
                Type_User.COMMON,
                "Jose1990",
                "Argentina");
        carlos = new Critic(5,
                Platform.DISNEY_PLUS,
                Type_User.CRITIC,
                6 );
        review1 = new PublicReview(
                "review1",
                "muy buena pelicula de Ciencia ficcion",
                "Me parecio una muy buena pelicula",
                3,
                LocalDate.of(2021,4,25),
                "Spanish",
                jose,
                true);

        critic1 = new PremiumReview(2,
                "critic2",
                "critic resume",
                "critic extended text",
                4, LocalDate.now(),"English",
                carlos,
                carlos.getCritic_id());
    }

    @Test void repositoryAddReview(){
        this.reviewRepository.addReview(review1);
        assertEquals(1, reviewRepository.getReviews().size());
    }
    @Test void repositoryCantAddReview(){
        this.reviewRepository.addReview(review1);
        assertThrows(ReviewHasBeenAddedException.class, ()-> this.reviewRepository.addReview(review1));
    }
    @Test void repositoryGetAllReviews(){
        this.reviewRepository.addReview(review1);
        this.reviewRepository.addReview(critic1);
        assertEquals(2, reviewRepository.getReviews().size());
    }

    @Test void repositoryGetReview(){
        this.reviewRepository.addReview(review1);
        this.reviewRepository.addReview(critic1);
        assertEquals(carlos, reviewRepository.getReviews("critic2").get(0).getUser());
    }

}
