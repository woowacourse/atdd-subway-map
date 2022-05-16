package wooteco.subway.repository.dao;

import java.util.List;
import wooteco.subway.repository.entity.StationEntity;

public interface StationDao {

    StationEntity save(final StationEntity stationEntity);

    List<StationEntity> findAll();

    StationEntity findById(final Long id);

    void deleteById(final Long id);
}
