package pl.lewarski.starwarsrest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CharacterDTO {

    private int characterId;
    private String characterName;
}
