package pl.lewarski.starwarsrest.api.processor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import pl.lewarski.starwarsrest.Connect;
import pl.lewarski.starwarsrest.api.RequestObject;
import pl.lewarski.starwarsrest.api.dto.CharacterDTO;
import pl.lewarski.starwarsrest.api.dto.PlanetDTO;
import pl.lewarski.starwarsrest.database.entity.FilmEntity;
import pl.lewarski.starwarsrest.util.FieldsNames;
import pl.lewarski.starwarsrest.util.UrlPaths;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class Processor {

    public Set<FilmEntity> getAskedFilms(CharacterDTO characterDTO) {
        Set<FilmEntity> filmEntities = new HashSet<>();
        characterDTO.getFilmsURL().forEach(e -> {
            JSONObject filmJSON = getJsonObject(e);
            long filmId = getId(e);
            String filmTitle = filmJSON.get(FieldsNames.TITLE_FIELD_NAME).toString();
            filmEntities.add(new FilmEntity(filmId, filmTitle));
        });
        return filmEntities;
    }

    public PlanetDTO getAskedPlanet(RequestObject requestObject) {

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
        if (!link.contains("json")) {

            link += UrlPaths.JSON_FORMAT_SUFIX;
        }
        String content = Connect.getUrlContents(link);
        return new JSONObject(content);
    }

    public Set<CharacterDTO> getAskedPeople(RequestObject requestObject, PlanetDTO askedPlanet) {
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
