package ahubbe.ahubbe.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
public class AnimationInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifier;

    @Getter @Setter private String title;

    @ElementCollection @Getter @Setter private List<Integer> releaseYear;

    @ElementCollection @Getter @Setter private List<Integer> releaseQuarter;

    @Getter @Setter private String keyVisual;

    @ElementCollection @Getter @Setter private List<String> genreList;
}
