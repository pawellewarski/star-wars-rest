package pl.lewarski.starwarsrest.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lewarski.starwarsrest.database.entity.FilmEntity;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<FilmEntity, Long> {
    List<FilmEntity> findAll();
}
