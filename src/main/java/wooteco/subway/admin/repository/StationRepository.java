package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Station;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    List<Station> findAll();

    @Modifying
    @Query("DELETE FROM Station WHERE name = :name")
    void deleteByName(@Param("name") String name);

    @Query("SELECT * FROM Station WHERE id IN (:ids)")
    List<Station> findByIds(List<Long> ids);
}