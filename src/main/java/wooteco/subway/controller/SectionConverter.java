package wooteco.subway.controller;

import wooteco.subway.dto.info.RequestCreateSectionInfo;
import wooteco.subway.dto.info.RequestDeleteSectionInfo;
import wooteco.subway.dto.request.SectionRequest;

public class SectionConverter {

    static RequestCreateSectionInfo toInfo(Long lineId, SectionRequest sectionRequest) {
        return new RequestCreateSectionInfo(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
    }

    static RequestDeleteSectionInfo toInfo(Long lineId, Long stationId) {
        return new RequestDeleteSectionInfo(lineId, stationId);
    }
}
