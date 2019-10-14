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
import pl.lewarski.starwarsrest.api.dto.FilmDTO;
import pl.lewarski.starwarsrest.api.dto.PlanetDTO;
import pl.lewarski.starwarsrest.api.dto.ReportDTO;
import pl.lewarski.starwarsrest.database.entity.CharacterEntity;
import pl.lewarski.starwarsrest.database.entity.FilmEntity;
import pl.lewarski.starwarsrest.database.entity.PlanetEntity;
import pl.lewarski.starwarsrest.database.entity.ReportEntity;
import pl.lewarski.starwarsrest.database.repository.CharacterRepository;
import pl.lewarski.starwarsrest.database.repository.FilmRepository;
import pl.lewarski.starwarsrest.database.repository.PlanetRepository;
import pl.lewarski.starwarsrest.database.repository.ReportRepository;
import pl.lewarski.starwarsrest.util.FieldsNames;
import pl.lewarski.starwarsrest.util.Mappings;
import pl.lewarski.starwarsrest.util.UrlPaths;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping(Mappings.BASE_URL)
public class ApiController {

    private ReportRepository reportRepository;
    private PlanetRepository planetRepository;
    private FilmRepository filmRepository;
    private CharacterRepository characterRepository;

    @Autowired
    public ApiController(ReportRepository reportRepository,
                         PlanetRepository planetRepository,
                         FilmRepository filmRepository,
                         CharacterRepository characterRepository) {
        this.planetRepository = planetRepository;
        this.reportRepository = reportRepository;
        this.filmRepository = filmRepository;
        this.characterRepository = characterRepository;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> createReport(@PathVariable Long id,
                                                  @RequestBody RequestObject requestObject) {
        log.info("PUT /{}/{}, content: {}", Mappings.BASE_URL, id, requestObject.toString());

        PlanetDTO askedPlanet = getAskedPlanet(requestObject);
        Set<CharacterDTO> askedPeople = new HashSet<>();
        Set<FilmEntity> askedFilms = new HashSet<>();

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

        PlanetEntity planetEntity =
                PlanetEntity.builder()
                        .id(askedPlanet.getId())
                        .name(askedPlanet.getPlanetName())
                        .build();
        planetRepository.save(planetEntity);

        if (nonNull(askedPlanet.getResidentsURL())) {
            askedPeople = getAskedPeople(requestObject, askedPlanet);
        }

        for (CharacterDTO characterDTO : askedPeople) {
            Set<FilmEntity> characterFilms = getAskedFilms(characterDTO);
            askedFilms.addAll(characterFilms);

            CharacterEntity characterEntity = new CharacterEntity(characterDTO.getCharacterId(), characterDTO.getCharacterName(), planetEntity);
            characterFilms.forEach(characterEntity::addFilm);
            characterRepository.save(characterEntity);
        }


        ReportEntity report = ReportEntity.builder()
                .id(id)
                .queryCharacterPhrase(requestObject.getCharacterPhrase())
                .queryPlanetName(requestObject.getPlanetName())
                .planet(planetEntity)
                .build();

        reportRepository.save(report);


        return ResponseEntity.ok()
                .body(ReportDTO.builder()
                        .id(id)
                        .queryCharacterPhrase(requestObject.getCharacterPhrase())
                        .queryPlanetName(requestObject.getPlanetName())
                        .planet(PlanetDTO.fromEntity(planetEntity))
                        .characters(askedPeople)
                        .films(FilmDTO.fromEntitySet(askedFilms))
                        .build());
    }

    private Set<FilmEntity> getAskedFilms(CharacterDTO characterDTO) {
        Set<FilmEntity> filmEntities = new HashSet<>();
        characterDTO.getFilmsURL().forEach(e -> {
            JSONObject filmJSON = getJsonObject(e);
            long filmId = getId(e);
            String filmTitle = filmJSON.get(FieldsNames.TITLE_FIELD_NAME).toString();
            filmEntities.add(new FilmEntity(filmId, filmTitle));
        });
        return filmEntities;
    }


    private PlanetDTO getAskedPlanet(RequestObject requestObject) {
        JSONObject planetsJson = getJsonObject(UrlPaths.PLANETS_URL);

        while (!planetsJson.get(FieldsNames.NEXT_FIELD_NAME).toString().equals("null")) {
            JSONArray planets = getObjects(planetsJson);
            for (int i = 0; i < planets.length(); i++) {
                JSONObject planet = planets.getJSONObject(i);
                if (planet.get(FieldsNames.ASKED_NAME_FIELD_NAME).toString().equalsIgnoreCase(requestObject.getPlanetName())) {
                    String planetName = planet.get(FieldsNames.ASKED_NAME_FIELD_NAME).toString();
                    String planetURL = planet.get(FieldsNames.URL_FIELD_NAME).toString();

                    List<String> residentsURL = new ArrayList<>();
                    planet.getJSONArray(FieldsNames.RESIDENTS_FIELD_NAME).toList()
                            .forEach(e -> residentsURL.add(e.toString()));

                    long planetId = getId(planetURL);
                    return PlanetDTO.builder()
                            .id(planetId)
                            .planetName(planetName)
                            .residentsURL(residentsURL)
                            .build();
                }
            }
            planetsJson = getJsonObject(planetsJson.getString(FieldsNames.NEXT_FIELD_NAME));
        }
        return null;
    }

    private long getId(String url) {
        int lastIndex = url.lastIndexOf("/");
        return Integer.parseInt(url.substring(url.lastIndexOf("/", lastIndex - 1) + 1, lastIndex));
    }

    private JSONArray getObjects(JSONObject jsonObject) {
        return jsonObject.getJSONArray(FieldsNames.ARRAY_FIELD_NAME);
    }

    private JSONObject getJsonObject(String link) {
        if (!link.endsWith(UrlPaths.JSON_FORMAT_SUFIX)) {

            link += UrlPaths.JSON_FORMAT_SUFIX;
        }
        String content = Connect.getUrlContents(link);
        return new JSONObject(content);
    }

    private Set<CharacterDTO> getAskedPeople(RequestObject requestObject, PlanetDTO askedPlanet) {
        Set<CharacterDTO> characterList = new HashSet<>();
        for (String url : askedPlanet.getResidentsURL()) {
            JSONObject residentJSON = getJsonObject(url);
            String characterName = residentJSON.get(FieldsNames.ASKED_NAME_FIELD_NAME).toString();
            JSONArray residentFilmsURL = residentJSON.getJSONArray(FieldsNames.FILMS_FIELD_NAME);
            List<String> filmsURL = new ArrayList<>();
            residentFilmsURL.forEach(e -> filmsURL.add(e.toString()));

            if (characterName.toUpperCase().contains(requestObject.getCharacterPhrase().toUpperCase())) {
                characterList.add(CharacterDTO.builder()
                        .characterId(getId(url))
                        .characterName(characterName)
                        .homeworldId(askedPlanet.getId())
                        .filmsURL(filmsURL)
                        .build());
            }
        }
        return characterList;
    }
}
