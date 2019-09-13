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
import pl.lewarski.starwarsrest.database.entity.ReportEntity;
import pl.lewarski.starwarsrest.database.repository.CharacterRepository;
import pl.lewarski.starwarsrest.database.repository.FilmRepository;
import pl.lewarski.starwarsrest.database.repository.ReportRepository;
import pl.lewarski.starwarsrest.util.FieldsNames;
import pl.lewarski.starwarsrest.util.Mappings;
import pl.lewarski.starwarsrest.util.UrlPaths;

import java.util.List;

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
    public ResponseEntity<ReportEntity> createReport(@PathVariable Long id,
                                                     @RequestBody RequestObject requestObject) {
        log.info("PUT /{}/{}, content: {}", Mappings.BASE_URL, id, requestObject.toString());
        PlanetDTO askedPlanet = getAskedPlanet(requestObject);
//        List<CharacterDTO> askedPeople = getAskedPeople(requestObject);
//        List<Character> filtered = getFiltered(askedPeople, askedPlanet);
//        Set<CharacterEntity> characters = createCharactersList(filtered);
//        Set<FilmEntity> films = createFilmSet(filtered);
//        ReportEntity report = new ReportEntity(requestObject.getCharacterPhrase(),
//                requestObject.getPlanetName(),
//                films, characters,
//                askedPlanet.getPlanetId(), askedPlanet.getPlanetName());
//        report.setId(id);
//
//        reportRepository.save(report);
        return ResponseEntity.ok().build();
    }


    private PlanetDTO getAskedPlanet(RequestObject requestObject) {
        JSONObject planetsJson = getJsonObject(UrlPaths.PLANETS_URL);

        while (nonNull(planetsJson.get(FieldsNames.NEXT_FIELD_NAME))) {
            JSONArray planets = getObjects(planetsJson);
            for (int i = 0; i < planets.length(); i++) {
                JSONObject planet = planets.getJSONObject(i);
                if (planet.get(FieldsNames.ASKED_NAME_FIELD_NAME).toString().equalsIgnoreCase(requestObject.getPlanetName())) {
                    String planetName = planet.get(FieldsNames.ASKED_NAME_FIELD_NAME).toString();
                    String planetURL = planet.get(FieldsNames.PLANET_URL_FIELD_NAME).toString();
                    int planetId = getPlanetId(planetURL);
                    PlanetDTO askedPlanet = new PlanetDTO(planetId, planetName);
                    return askedPlanet;
                }
            }
            planetsJson = getJsonObject(planetsJson.getString(FieldsNames.NEXT_FIELD_NAME));
        }
        return null;
    }

    private int getPlanetId(String planetURL) {
        int lastIndex = planetURL.lastIndexOf("/");
        return Integer.parseInt(planetURL.substring(planetURL.lastIndexOf("/", lastIndex - 1) + 1, lastIndex));
    }

    private JSONArray getObjects(JSONObject jsonObject) {
        return jsonObject.getJSONArray(FieldsNames.ARRAY_FIELD_NAME);
    }

    private JSONObject getJsonObject(String link) {
        String content = Connect.getUrlContents(link + UrlPaths.JSON_FORMAT_SUFIX);
        return new JSONObject(content);
    }

//    private List<CharacterDTO> getAskedPeople(RequestObject requestObject) {
//        List<CharacterDTO> characterList = new ArrayList<>();
//        JSONObject peopleJson = getJsonObject(UrlPaths.PEOPLE_URL);
//
//        while (peopleJson.get("next").toString() != "null") {
//            JSONArray people = getObjects(peopleJson);
//            for (int i = 0; i < people.length(); i++) {
//                JSONObject person = people.getJSONObject(i);
//                if (person.get("name").toString().toLowerCase().contains(requestObject.getCharacterPhrase().toLowerCase())) {
//                    String characterName = person.get("name").toString();
//                    String characterURL = person.get("url").toString();
//                    int characterId = Integer.parseInt(characterURL.substring(28, characterURL.length() - 1));
//                    String homeworld = person.get("homeworld").toString();
//                    Set<FilmEntity> filmList = new HashSet<>();
//                    JSONArray results = person.getJSONArray("films");
//                    for (int j = 0; j < results.length(); j++) {
//                        FilmEntity film = getAskedFilm(results.get(j).toString());
//                        filmList.add(film);
//                    }
//                    CharacterDTO characterDTO = new CharacterDTO(characterId, characterName);
//                    characterList.add(characterDTO);
//                }
//            }
//            peopleJson = getJsonObject(peopleJson.get("next").toString());
//        }
//        return characterList;
//    }
}