package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    List<Station> findAll();

    @Override
    List<Station> findAllById(Iterable<Long> ids);

    // cannot use in h2
    // @Query("SELECT * FROM station WHERE id IN (:ids) ORDER BY FIELD(id, :ids)")
    // List<Station> findAllByIdInOrder(@Param("ids") Iterable<Long> ids);

    @Modifying
    @Query("DELETE FROM Station WHERE name = :name")
    void deleteByName(@Param("name") String name);
}
