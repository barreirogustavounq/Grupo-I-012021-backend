package model.films;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.reviews.PremiumReview;
import model.reviews.PublicReview;
import model.reviews.Review;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Title extends Classifiable {
    private String tconst;
    private String titleType;
    private String primaryTitle;
    private String originalTitle;
    private Boolean isAdult;
    private Integer startYear;
    private Integer endYear;
    private Integer runtimeMinutes;
    private List<String> genres;


    public Title(String tconst, String titleType, String primaryTitle,
                 String originalTitle, Boolean isAdult,
                 Integer startYear, Integer endYear, Integer runtimeMinutes,
                 List<String> genres, List<PremiumReview> critics,
                 List<PublicReview> reviews) {
        super(critics, reviews);
        this.tconst = tconst;
        this.titleType = titleType;
        this.primaryTitle = primaryTitle;
        this.originalTitle = originalTitle;
        this.isAdult = isAdult;
        this.startYear = startYear;
        this.endYear = endYear;
        this.runtimeMinutes = runtimeMinutes;
        this.genres = genres;
    }
}
