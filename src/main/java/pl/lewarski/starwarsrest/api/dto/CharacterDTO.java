package pl.lewarski.starwarsrest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.lewarski.starwarsrest.database.entity.CharacterEntity;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
public class CharacterDTO {

    private int characterId;
    private String characterName;

    public static CharacterDTO fromEntity(CharacterEntity entity) {
        return CharacterDTO.builder()
                .characterId(entity.getId())
                .characterName(entity.getCharacterName())
                .build();
    }

    public static Set<CharacterDTO> fromEntitySet(Set<CharacterEntity> entities) {
        Set<CharacterDTO> characters = new HashSet<>();
        for (CharacterEntity characterEntity : entities) {
            characters.add(CharacterDTO.fromEntity(characterEntity));
        }
        return characters;
    }
}
