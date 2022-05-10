package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Long save(Section section);

    List<SectionEntity> findByLineId(Long lineId);

    boolean update(Long sectionId, Long downStationId, int distance);
}
