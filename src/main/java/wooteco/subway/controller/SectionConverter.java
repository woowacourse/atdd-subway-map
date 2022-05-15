package wooteco.subway.controller;

import wooteco.subway.dto.info.RequestToCreateSection;
import wooteco.subway.dto.info.RequestToDeleteSection;
import wooteco.subway.dto.request.SectionRequest;

public class SectionConverter {

    static RequestToCreateSection toInfo(Long lineId, SectionRequest sectionRequest) {
        return new RequestToCreateSection(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
    }

    static RequestToDeleteSection toInfo(Long lineId, Long stationId) {
        return new RequestToDeleteSection(lineId, stationId);
    }
}
