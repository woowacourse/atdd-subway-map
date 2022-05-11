package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.service.dto.SectionDto;

public interface CommonSectionDao {

    long save(final Long lineId, final SectionDto section);

    List<SectionDto> findAllByLineId(final Long lineId);

    int deleteById(final Long lineId, final Long stationId);
}
