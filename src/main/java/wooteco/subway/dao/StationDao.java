package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

public interface StationDao {
    Station save(Station station) throws IllegalArgumentException;

    List<Station> findAll();

    void deleteById(Long id);

    Optional<Station> findById(Long id);

    boolean existByName(String name);
}
