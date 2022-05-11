package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.repository.dao.entity.StationEntity;

public interface StationDao {

    Long save(StationEntity stationEntity);

    List<StationEntity> findAll();

    Optional<StationEntity> findById(Long id);

    Boolean existsByName(String name);

    void remove(Long id);
}
