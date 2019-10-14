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
@Table(name = "planet")
public class PlanetEntity {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "planet", cascade = CascadeType.ALL)
    private Set<CharacterEntity> characters = new HashSet<>();

    @OneToMany(mappedBy = "planet", cascade = CascadeType.ALL)
    private Set<ReportEntity> reports = new HashSet<>();
}

