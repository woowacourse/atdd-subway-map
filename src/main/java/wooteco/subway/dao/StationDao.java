package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.entity.StationEntity;

public interface StationDao extends UpdateDao<StationEntity> {

    List<StationEntity> findAll();

    Optional<StationEntity> findById(Long id);
}
