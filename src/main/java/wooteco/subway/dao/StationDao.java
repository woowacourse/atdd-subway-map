package wooteco.subway.dao;

import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.util.List;

@Repository
public interface StationDao {

    Station save(Station station);

    Station findByName(String name);

    List<Station> findAll();

    void deleteById(Long id);

    boolean existByName(String name);

    boolean existById(Long id);
}
