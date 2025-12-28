package ahubbe.ahubbe.service.Admin;

import ahubbe.ahubbe.dto.AnimeDto;
import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AdminRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Document getHtml(String URL) {
        Document doc;
        try {
            doc = Jsoup.connect(URL).get();
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Elements getAnimeElements(String URL, String targetTag) {
        Document doc = getHtml(URL);

        Elements elements = doc.select("[" + targetTag + "]");

        elements.removeIf(el -> el.hasAttr("class"));
        elements.removeIf(el -> el.hasAttr("rel"));
        elements.removeIf(el -> el.attr("title").matches("^애니메이션/\\d{4}년\\s\\d{1,2}월.*"));
        elements.removeIf(el -> el.attr("title").matches("^분류:.*"));

        return elements;
    }

    public List<AnimeDto> getAnimeDetailElements(String Year, String quarter, String targetTag) {
        String URL =
                "https://namu.wiki/w/"
                        + URLEncoder.encode(
                                        "분류:" + Year + "년 " + quarter + "분기 일본 애니메이션",
                                        StandardCharsets.UTF_8)
                                .replace("+", "%20");
        Elements animeAddress = getAnimeElements(URL, targetTag);
        List<AnimeDto> resultList = new ArrayList<>();

        for (Element el : animeAddress) {
            try {
                Document DetailAnimePage = getHtml("https://namu.wiki" + el.attr("href"));
                String title = el.attr("title");
                String keyVisual =
                        "https:"
                                + DetailAnimePage.select(
                                                "table > tbody > tr > td > div > span > span > img")
                                        .get(1)
                                        .attr("data-src");
                Elements genre =
                        DetailAnimePage.select("tr:has(td:contains(장르)) > td:nth-child(2) a");
                if (genre.isEmpty()) {
                    genre = DetailAnimePage.select("tr:has(td:contains(장르)) > td:nth-child(2) div");
                }
                List<String> genreList =
                        genre.eachText().stream()
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();
                resultList.add(new AnimeDto(title, keyVisual, genreList));
            } catch (Exception ignored) {
            }
        }

        return resultList;
    }

    @Transactional
    public List<AnimationInformation> saveAnimeData(String year, String quarter, String targetTag) {
        List<AnimeDto> AnimeDetailElements = getAnimeDetailElements(year, quarter, targetTag);
        List<AnimationInformation> resultList = new ArrayList<>();

        for (AnimeDto dto : AnimeDetailElements) {

            AnimationInformation animation = new AnimationInformation();

            animation.setTitle(dto.getTitle());
            animation.setKeyVisual(dto.getKeyVisual());
            animation.setGenreList(dto.getGenreList());

            if (adminRepository.findByTitle(dto.getTitle()).isPresent()) {
                Set<Integer> releaseYear = new HashSet<>();
                Set<Integer> releaseQuarter = new HashSet<>();

                releaseYear.addAll(
                        adminRepository.findByTitle(dto.getTitle()).get().getReleaseYear());
                releaseQuarter.addAll(
                        adminRepository.findByTitle(dto.getTitle()).get().getReleasequarter());

                releaseYear.add(Integer.parseInt(year));
                releaseQuarter.add(Integer.parseInt(quarter));

                animation.setReleaseYear(new ArrayList<>(releaseYear));
                animation.setReleasequarter(new ArrayList<>(releaseQuarter));

                adminRepository.deleteByTitle(dto.getTitle());
            } else {
                List<Integer> releaseYear = new ArrayList<>();
                List<Integer> releaseQuarter = new ArrayList<>();

                releaseYear.add(Integer.parseInt(year));
                releaseQuarter.add(Integer.parseInt(quarter));

                animation.setReleaseYear(new ArrayList<>(releaseYear));
                animation.setReleasequarter(new ArrayList<>(releaseQuarter));
            }

            resultList.add(animation);
        }

        return adminRepository.saveAll(resultList);
    }

    @Transactional
    public AnimationInformation saveSingleAnimeData(
            AnimeDto animeDto, String year, String quarter) {
        AnimationInformation animation = new AnimationInformation();

        if (adminRepository.findByTitle(animeDto.getTitle()).isPresent()) {
            Set<Integer> releaseYear = new HashSet<>();
            Set<Integer> releaseQuarter = new HashSet<>();

            releaseYear.addAll(
                    adminRepository.findByTitle(animeDto.getTitle()).get().getReleaseYear());
            releaseQuarter.addAll(
                    adminRepository.findByTitle(animeDto.getTitle()).get().getReleasequarter());

            releaseYear.add(Integer.parseInt(year));
            releaseQuarter.add(Integer.parseInt(quarter));

            animation.setReleaseYear(new ArrayList<>(releaseYear));
            animation.setReleasequarter(new ArrayList<>(releaseQuarter));

            adminRepository.deleteByTitle(animeDto.getTitle());
        } else {
            animation.setReleaseYear(List.of(Integer.parseInt(year)));
            animation.setReleasequarter(List.of(Integer.parseInt(quarter)));
        }

        animation.setTitle(animeDto.getTitle());
        animation.setKeyVisual(animeDto.getKeyVisual());
        animation.setGenreList(animeDto.getGenreList());

        adminRepository.save(animation);
        return animation;
    }
}
