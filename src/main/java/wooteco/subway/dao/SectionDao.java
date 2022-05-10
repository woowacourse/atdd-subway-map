package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionEntity;

public interface SectionDao {
    void save(Long lineId, Section section);

    List<SectionEntity> findByLine(Long lineId);

    void update(Long lineId, Section section);
}
