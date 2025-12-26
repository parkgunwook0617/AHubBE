package ahubbe.ahubbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AnimeDto {
    private String title;
    private String keyVisual;
    private List<String> genreList;
}