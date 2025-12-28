package ahubbe.ahubbe.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnimeDto {
    private String title;
    private String keyVisual;
    private List<String> genreList;
}
