package wooteco.subway.service;

import wooteco.subway.service.dto.SectionServiceRequest;

public interface SectionService {

    void save(Long lineId, SectionServiceRequest sectionServiceRequest);

    void deleteByLineIdAndStationId(Long lineId, Long sectionId);
}
