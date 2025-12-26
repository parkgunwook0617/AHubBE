package ahubbe.ahubbe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
public class AnimationInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifier;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private List<Integer> releaseYear;

    @Getter
    @Setter
    private List<Integer> releasequarter;

    @Getter
    @Setter
    private String keyVisual;

    @Getter
    @Setter
    private List<String> genreList;
}
