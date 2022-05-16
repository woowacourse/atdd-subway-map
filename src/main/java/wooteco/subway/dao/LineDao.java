package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.entity.LineEntity;

public interface LineDao extends UpdateDao<LineEntity> {

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

}
