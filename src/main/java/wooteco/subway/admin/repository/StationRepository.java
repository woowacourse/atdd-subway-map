package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.CrudRepository;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long>{
    @Override
    List<Station> findAll();
}