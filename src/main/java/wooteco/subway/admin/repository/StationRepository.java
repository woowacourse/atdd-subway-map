package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

import java.util.Set;

public interface StationRepository extends CrudRepository<Station, Long> {

    @Query("select id from station where name = :name")
    Long findIdByName(@Param("name") String name);
}