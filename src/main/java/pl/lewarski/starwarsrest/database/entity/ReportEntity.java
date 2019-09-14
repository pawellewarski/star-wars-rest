package pl.lewarski.starwarsrest.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "report")
public class ReportEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "query_criteria_character_phrase")
    private String queryCharacterPhrase;

    @Column(name = "query_criteria_planet_name")
    private String queryPlanetName;

//    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
//    private Set<FilmEntity> films = new HashSet<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private Set<CharacterEntity> characters = new HashSet<>();

    @Column(name = "planet_id")
    private int planetId;

    @Column(name = "planet_name")
    private String planetName;
}

