package pl.lewarski.starwarsrest.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "planetId")
    private PlanetEntity planet;
}

