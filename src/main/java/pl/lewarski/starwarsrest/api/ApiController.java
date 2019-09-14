package pl.lewarski.starwarsrest.api;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lewarski.starwarsrest.Connect;
import pl.lewarski.starwarsrest.api.dto.CharacterDTO;
import pl.lewarski.starwarsrest.api.dto.PlanetDTO;
import pl.lewarski.starwarsrest.api.dto.ReportDTO;
import pl.lewarski.starwarsrest.database.entity.FilmEntity;
import pl.lewarski.starwarsrest.database.entity.ReportEntity;
import pl.lewarski.starwarsrest.database.repository.CharacterRepository;
import pl.lewarski.starwarsrest.database.repository.FilmRepository;
import pl.lewarski.starwarsrest.database.repository.ReportRepository;
import pl.lewarski.starwarsrest.util.FieldsNames;
import pl.lewarski.starwarsrest.util.Mappings;
import pl.lewarski.starwarsrest.util.UrlPaths;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping(Mappings.BASE_URL)
public class ApiController {

    private ReportRepository reportRepository;
    private FilmRepository filmRepository;
    private CharacterRepository characterRepository;

    @Autowired
    public ApiController(ReportRepository reportRepository,
                         FilmRepository filmRepository,
                         CharacterRepository characterRepository) {
        this.reportRepository = reportRepository;
        this.filmRepository = filmRepository;
        this.characterRepository = characterRepository;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> createReport(@PathVariable Long id,
                                                  @RequestBody RequestObject requestObject) {
        log.info("PUT /{}/{}, content: {}", Mappings.BASE_URL, id, requestObject.toString());

        PlanetDTO askedPlanet = getAskedPlanet(requestObject);
        List<CharacterDTO> askedPeople = new ArrayList<>();
        List<FilmEntity> askedFilms = new ArrayList<>();

        if (isNull(askedPlanet)) {
            ReportEntity reportEntity = ReportEntity
                    .builder()
                    .id(id)
                    .queryCharacterPhrase(requestObject.getCharacterPhrase())
                    .queryPlanetName(requestObject.getPlanetName())
                    .build();
            reportRepository.save(reportEntity);

            return ResponseEntity.ok()
                    .body(ReportDTO.fromEntity(reportEntity));
        }

        if (nonNull(askedPlanet.getResidentsURL())) {
            askedPeople = getAskedPeople(requestObject, askedPlanet.getResidentsURL());
        }

//        Set<CharacterEntity> characters = createCharactersList(filtered);
//        Set<FilmEntity> films = createFilmSet(filtered);
//        ReportEntity report = new ReportEntity(requestObject.getCharacterPhrase(),
//                requestObject.getPlanetName(),
//                films, characters,
//                askedPlanet.getId(), askedPlanet.getPlanetName());
//        report.setId(id);
//
//        reportRepository.save(report);

        return ResponseEntity.ok().build();
    }


    private PlanetDTO getAskedPlanet(RequestObject requestObject) {
        JSONObject planetsJson = getJsonObject(UrlPaths.PLANETS_URL);

        while (!planetsJson.get(FieldsNames.NEXT_FIELD_NAME).toString().equals("null")) {
            JSONArray planets = getObjects(planetsJson);
            for (int i = 0; i < planets.length(); i++) {
                JSONObject planet = planets.getJSONObject(i);
                if (planet.get(FieldsNames.ASKED_NAME_FIELD_NAME).toString().equalsIgnoreCase(requestObject.getPlanetName())) {
                    String planetName = planet.get(FieldsNames.ASKED_NAME_FIELD_NAME).toString();
                    String planetURL = planet.get(FieldsNames.PLANET_URL_FIELD_NAME).toString();

                    List<String> residentsURL = new ArrayList<>();
                    planet.getJSONArray(FieldsNames.RESIDENTS_FIELD_NAME).toList()
                            .forEach(e -> residentsURL.add(e.toString()));

                    int planetId = getId(planetURL);
                    return PlanetDTO.builder()
                            .planetId(planetId)
                            .planetName(planetName)
                            .residentsURL(residentsURL)
                            .build();
                }
            }
            planetsJson = getJsonObject(planetsJson.getString(FieldsNames.NEXT_FIELD_NAME));
        }
        return null;
    }

    private int getId(String url) {
        int lastIndex = url.lastIndexOf("/");
        return Integer.parseInt(url.substring(url.lastIndexOf("/", lastIndex - 1) + 1, lastIndex));
    }

    private JSONArray getObjects(JSONObject jsonObject) {
        return jsonObject.getJSONArray(FieldsNames.ARRAY_FIELD_NAME);
    }

    private JSONObject getJsonObject(String link) {
        String content = Connect.getUrlContents(link + UrlPaths.JSON_FORMAT_SUFIX);
        return new JSONObject(content);
    }

    private List<CharacterDTO> getAskedPeople(RequestObject requestObject, List<String> residentsURL) {
        List<CharacterDTO> characterList = new ArrayList<>();
        for (String url : residentsURL) {
            JSONObject residentJSON = getJsonObject(url);
            String characterName = residentJSON.get(FieldsNames.NEXT_FIELD_NAME).toString();
            if (characterName.toUpperCase().contains(requestObject.getCharacterPhrase().toUpperCase())) {
                characterList.add(CharacterDTO.builder()
                        .characterId(getId(url))
                        .characterName(characterName)
                        .build());
            }
        }
        return characterList;
    }
}