package pl.lewarski.starwarsrest.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "character")
public class CharacterEntity {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "character_name")
    private String characterName;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReportEntity report;

    @ManyToMany(mappedBy = "characters")
    private Set<FilmEntity> films = new HashSet<>();
}
