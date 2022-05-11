package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Section;
import wooteco.subway.entity.SectionEntity;

public interface SectionDao {

    SectionEntity save(SectionEntity section, Long lineId);

    List<SectionEntity> readSectionsByLineId(Long lineId);
}
