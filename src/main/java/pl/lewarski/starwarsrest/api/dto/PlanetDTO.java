package pl.lewarski.starwarsrest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanetDTO {
    private int planetId;
    private String planetName;
}
