package wooteco.subway.dao;

import java.util.Optional;

public interface SectionDao {

    Optional<Object> findById(Long id);
}
