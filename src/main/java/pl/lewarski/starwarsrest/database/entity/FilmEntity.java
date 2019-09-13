package pl.lewarski.starwarsrest.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "film")
public class FilmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "film_name")
    private String filmName;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReportEntity report;

    @ManyToMany
    @JoinTable(name = "character_film",
            joinColumns = { @JoinColumn(name = "character_id")},
            inverseJoinColumns = { @JoinColumn(name = "film_id") })
    private Set<CharacterEntity> characters = new HashSet<>();
}
