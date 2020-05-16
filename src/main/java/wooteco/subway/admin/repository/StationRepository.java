package wooteco.subway.admin.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {

    @Query("SELECT * FROM STATION WHERE name = :name")
    Station findIdByName(@Param("name") String name);
}
