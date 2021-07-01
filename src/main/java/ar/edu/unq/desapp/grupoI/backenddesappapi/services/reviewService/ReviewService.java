package ar.edu.unq.desapp.grupoi.backenddesappapi.services.reviewService;

//import lombok.Getter;

import ar.edu.unq.desapp.grupoi.backenddesappapi.dto.ReviewOrderDTO;
import ar.edu.unq.desapp.grupoi.backenddesappapi.dto.SubscribeDTO;
import ar.edu.unq.desapp.grupoi.backenddesappapi.exceptions.NoSuchReviewsWithTitle;
import ar.edu.unq.desapp.grupoi.backenddesappapi.exceptions.ReviewsNotFoundException;
import ar.edu.unq.desapp.grupoi.backenddesappapi.model.filter.*;
import ar.edu.unq.desapp.grupoi.backenddesappapi.model.reviews.Review;
import ar.edu.unq.desapp.grupoi.backenddesappapi.model.reviews.ReviewSubscriber;
import ar.edu.unq.desapp.grupoi.backenddesappapi.model.user.UserAbs;
import ar.edu.unq.desapp.grupoi.backenddesappapi.repositories.review.ReviewRepository;
import ar.edu.unq.desapp.grupoi.backenddesappapi.repositories.review.SubscribeRepository;
import ar.edu.unq.desapp.grupoi.backenddesappapi.services.userService.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

//import ar.edu.unq.desapp.grupoXXX.backenddesappapi.repositories.review.ReviewRepositoryImpl;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    EntityManager em;

    public List<Review> findAll() {
        return iterableToList(this.reviewRepository.findAll());
    }

    private List<Review> iterableToList(Iterable<Review> iterable) {
        List<Review> reviews = new ArrayList<>();
        for (Review review : iterable) {
            reviews.add(review);
        }
        return reviews;
    }

    public Review findById(Long id) {
        return this.reviewRepository.findById(id).orElseThrow(() -> new ReviewsNotFoundException(HttpStatus.BAD_REQUEST));
    }

    public List<Review> findReviewsForTitle(String tconst) {
        List<Review> reviews = this.reviewRepository.findReviewsForTitle(tconst);
        if (reviews.isEmpty()) throw new NoSuchReviewsWithTitle(HttpStatus.BAD_REQUEST);
        return reviews;
    }

    //@Override
    public List<Review> getReviews(String tittle_tconst) {
        List<Review> all = iterableToList(reviewRepository.findAll());
        List<Review> result = all.stream()
                .filter(r -> r.getTittle_tconst()
                        .equalsIgnoreCase(tittle_tconst))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new ReviewsNotFoundException(HttpStatus.BAD_REQUEST);
        } else {
            return result;
        }
    }

    public List<Review> getReviewsWithFilter(LinkedHashMap<String, String> filters) {
        List<Filter> filterList = this.convertMapToListFilter(filters);
        List<UserAbs> userAbsList = userService.findAll();
        List<Review> result = iterableToList(reviewRepository.findAll());
        for (Filter filter : filterList) {
            result = filter.doFilter(result, userAbsList);
        }
        return result;
    }

    public List<Review> findReviewsWithFilterCriteria(LinkedHashMap<String, String> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Review> cq = cb.createQuery(Review.class);

        Root<Review> reviewRoot = cq.from(Review.class);

        Join<Review, User> userJoin = reviewRoot.join("userAbs");

        List<Predicate> predicates = new ArrayList<>();
        List<Filter> filterList = this.convertMapToListFilter(filters);
        for (Filter filter : filterList) {
            switch (filter.getType()) {
                case "spoiler_alert":
                    predicates.add(cb.equal(reviewRoot.get(filter.getType()), filter.getValue().equalsIgnoreCase("true")));
                    break;
                case "language":
                    predicates.add(cb.equal(reviewRoot.get(filter.getType()), filter.getValue()));
                    break;
                default:
                    predicates.add(cb.equal(userJoin.get(filter.getType()), filter.getValue()));
                    break;
            }

        }
        if (filters.get("orderBy") != null) {
            switch (filters.get("orderBy")) {
                case "ratingAsc":
                    cq.orderBy(cb.asc(reviewRoot.get("rating"))).where(predicates.toArray(new Predicate[0]));
                    break;
                case "ratingDesc":
                    cq.orderBy(cb.desc(reviewRoot.get("rating"))).where(predicates.toArray(new Predicate[0]));
                    break;
                case "dateAsc":
                    cq.orderBy(cb.asc(reviewRoot.get("date"))).where(predicates.toArray(new Predicate[0]));
                    break;
                case "dateDesc":
                    cq.orderBy(cb.desc(reviewRoot.get("date"))).where(predicates.toArray(new Predicate[0]));
                    break;
            }
        } else {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        return em.createQuery(cq).getResultList();
    }

    @Transactional
    public Review save(Review review) {
        return this.reviewRepository.save(review);
    }


    private List<Filter> convertMapToListFilter(LinkedHashMap<String, String> filters) {
        List<Filter> listFilter = new ArrayList<>();
        System.out.println(filters);
        for (String filter : filters.keySet()) {
            System.out.println(filter);
            switch (filter) {
                case "language":
                    listFilter.add(new LanguageFilter(filters.get("language")));
                    break;
                case "location":
                    listFilter.add(new LocationFilter(filters.get("location")));
                    break;
                case "platform":
                    listFilter.add(new PlatformFilter(filters.get("platform")));
                    break;
                case "spoiler_alert":
                    listFilter.add(new SpoilerAlertFilter(filters.get("spoiler_alert").equalsIgnoreCase("true")));
                    break;
                case "type_user":
                    listFilter.add(new TypeUserFilter(filters.get("type_user")));
                    break;
            }
        }
        return listFilter;
    }

    public List<Review> getReviewsWithOrder(ReviewOrderDTO reviewOrder) {
        List<Review> allReviews = findAll();
        if (reviewOrder.isDateAsc())
            return allReviews.stream().sorted(Comparator.comparing(Review::getDate)).collect(Collectors.toList());
        if (reviewOrder.isDateDesc())
            return allReviews.stream().sorted(Comparator.comparing(Review::getDate).reversed()).collect(Collectors.toList());
        if (reviewOrder.isRatingDesc())
            return allReviews.stream().sorted(Comparator.comparing(Review::getRating)).collect(Collectors.toList());
        if (reviewOrder.isRatingAsc())
            return allReviews.stream().sorted(Comparator.comparing(Review::getRating).reversed()).collect(Collectors.toList());
        return allReviews;
    }

    @Transactional
    public void subscribe(SubscribeDTO subscribeDTO) {
        ReviewSubscriber subscribers = new ReviewSubscriber(new ArrayList<>(), subscribeDTO.getReviewId());
        if(subscribeRepository.existsById(subscribeDTO.getReviewId())) {
            subscribers = subscribeRepository.getOne(subscribeDTO.getReviewId());
        }
        subscribers.getUsers().add(subscribeDTO.getNick());
        subscribeRepository.save(subscribers);
    }

    @Transactional
    public void unsubscribe(SubscribeDTO subscribeDTO) {
        ReviewSubscriber subscribers = subscribeRepository.getOne(subscribeDTO.getReviewId());
        subscribers.getUsers().remove(subscribeDTO.getNick());
        subscribeRepository.save(subscribers);
    }

    public List<String> getSubscribers(Integer reviewId) {
        return subscribeRepository.getOne(reviewId).getUsers();
    }
}
