package pl.lewarski.starwarsrest.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanetDTO {
    private int planetId;
    private String planetName;
    @JsonIgnore
    private List<String> residentsURL;
}
