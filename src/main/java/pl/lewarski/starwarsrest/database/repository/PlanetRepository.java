package pl.lewarski.starwarsrest.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lewarski.starwarsrest.database.entity.PlanetEntity;
import pl.lewarski.starwarsrest.database.entity.ReportEntity;

import java.util.List;

@Repository
public interface PlanetRepository extends JpaRepository<PlanetEntity, Long> {
    List<PlanetEntity> findAll();
}
