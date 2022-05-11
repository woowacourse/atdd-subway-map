package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.Optional;

public interface SectionDao {

    long save(Section section);

    Optional<Section> findById(long id);
}
