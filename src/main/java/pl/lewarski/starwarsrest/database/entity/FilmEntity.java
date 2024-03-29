package pl.lewarski.starwarsrest.database.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "film")
public class FilmEntity {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "film_name")
    private String filmName;

    @ManyToMany(mappedBy = "films")
    private Set<CharacterEntity> characters = new HashSet<>();

    public FilmEntity(long id, String filmName) {
        this.id = id;
        this.filmName = filmName;
    }
}
