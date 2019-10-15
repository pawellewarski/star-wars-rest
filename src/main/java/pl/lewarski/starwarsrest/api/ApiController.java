package pl.lewarski.starwarsrest.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lewarski.starwarsrest.api.dto.CharacterDTO;
import pl.lewarski.starwarsrest.api.dto.FilmDTO;
import pl.lewarski.starwarsrest.api.dto.PlanetDTO;
import pl.lewarski.starwarsrest.api.dto.ReportDTO;
import pl.lewarski.starwarsrest.api.processor.Processor;
import pl.lewarski.starwarsrest.database.entity.CharacterEntity;
import pl.lewarski.starwarsrest.database.entity.FilmEntity;
import pl.lewarski.starwarsrest.database.entity.PlanetEntity;
import pl.lewarski.starwarsrest.database.entity.ReportEntity;
import pl.lewarski.starwarsrest.database.repository.CharacterRepository;
import pl.lewarski.starwarsrest.database.repository.PlanetRepository;
import pl.lewarski.starwarsrest.database.repository.ReportRepository;
import pl.lewarski.starwarsrest.util.Mappings;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping(Mappings.BASE_URL)
public class ApiController {

    private ReportRepository reportRepository;
    private PlanetRepository planetRepository;
    private CharacterRepository characterRepository;
    private Processor processor;

    @Autowired
    public ApiController(ReportRepository reportRepository,
                         PlanetRepository planetRepository,
                         CharacterRepository characterRepository,
                         Processor processor) {
        this.planetRepository = planetRepository;
        this.reportRepository = reportRepository;
        this.characterRepository = characterRepository;
        this.processor = processor;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> createReport(@PathVariable Long id,
                                                  @RequestBody RequestObject requestObject) {
        log.info("PUT /{}/{}, content: {}", Mappings.BASE_URL, id, requestObject.toString());

        PlanetDTO askedPlanet = processor.getAskedPlanet(requestObject);
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

        PlanetEntity planetEntity = PlanetEntity.builder()
                .id(askedPlanet.getId())
                .name(askedPlanet.getPlanetName())
                .build();
        planetRepository.save(planetEntity);

        if (nonNull(askedPlanet.getResidentsURL())) {
            askedPeople = processor.getAskedPeople(requestObject, askedPlanet);
        }

        for (CharacterDTO characterDTO : askedPeople) {
            Set<FilmEntity> characterFilms = processor.getAskedFilms(characterDTO);
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


}
