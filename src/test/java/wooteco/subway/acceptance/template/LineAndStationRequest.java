package wooteco.subway.acceptance.template;

import wooteco.subway.controller.dto.request.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.SectionRequestDto;
import wooteco.subway.controller.dto.request.StationRequestDto;

import java.util.HashMap;
import java.util.Map;

public class LineAndStationRequest {
    public static Map<String, Long> createLineWithStationsAndSectionsRequest() {
        // 역 3개 추가
        Long stationId1 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("강남역"));
        Long stationId2 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("길동역"));
        Long stationId3 = StationRequest.createStationRequestAndReturnId(new StationRequestDto("주안역"));

        // 라인 1개 추가 + 구간 1개 추가
        Long lineId = LineRequest.createLineRequestAndReturnId(new LineCreateRequestDto(
                "1호선",
                "yellow",
                stationId1,
                stationId2,
                10
        ));

        // 구간 1개 추가
        LineRequest.createSectionRequestAndReturnResponse(
                new SectionRequestDto(stationId2, stationId3, 10),
                lineId
        );

        Map<String, Long> ids = new HashMap<>();
        ids.put("station1", stationId1);
        ids.put("station2", stationId2);
        ids.put("station3", stationId3);
        ids.put("line", lineId);
        return ids;
    }
}
