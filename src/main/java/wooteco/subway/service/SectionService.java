package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.SectionServiceRequest;

public interface SectionService {

    List<Sections> findAll();

    Sections findByLineId(Long lineId);

    void save(Long lineId, SectionServiceRequest sectionServiceRequest);

    void deleteByLineIdAndStationId(Long lineId, Long sectionId);
}
