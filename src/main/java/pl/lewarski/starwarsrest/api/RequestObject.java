package pl.lewarski.starwarsrest.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class RequestObject {

    @JsonAlias("query_criteria_character_phrase")
    private String characterPhrase;
    @JsonAlias("query_criteria_planet_name")
    private String planetName;
}
