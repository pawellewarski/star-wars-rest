package pl.lewarski.starwarsrest.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "character")
public class CharacterEntity {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "character_name")
    private String characterName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "planetId")
    private PlanetEntity planet;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "character_film",
            joinColumns = {@JoinColumn(name = "character_id")},
            inverseJoinColumns = {@JoinColumn(name = "film_id")})
    private Set<FilmEntity> films = new HashSet<>();

    public void addFilm(FilmEntity filmEntity) {
        films.add(filmEntity);
        filmEntity.getCharacters().add(this);
    }

    public CharacterEntity(long id, String characterName, PlanetEntity planet) {
        this.id = id;
        this.characterName = characterName;
        this.planet = planet;
    }
}
