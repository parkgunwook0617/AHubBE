package ahubbe.ahubbe.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnimeDto {
    private String title;
    private String keyVisual;
    private List<String> genreList;
}
