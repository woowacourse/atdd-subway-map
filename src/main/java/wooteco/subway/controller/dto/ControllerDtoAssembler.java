package wooteco.subway.controller.dto;

import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.controller.dto.section.SectionRequest;
import wooteco.subway.controller.dto.station.StationResponse;
import wooteco.subway.service.dto.line.LineRequestDto;
import wooteco.subway.service.dto.line.LineResponseDto;
import wooteco.subway.service.dto.section.SectionRequestDto;
import wooteco.subway.service.dto.station.StationResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class ControllerDtoAssembler {

    private ControllerDtoAssembler() {
    }

    public static StationResponse stationResponseByDto(StationResponseDto stationResponseDto){
        return new StationResponse(stationResponseDto.getId(), stationResponseDto.getName());
    }

    public static LineResponse lineResponseByDto(LineResponseDto lineResponseDto) {
        List<StationResponse> stations = lineResponseDto.getStations().stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return new LineResponse(lineResponseDto.getId(), lineResponseDto.getName(), lineResponseDto.getColor(), stations);
    }

    public static LineRequestDto lineRequestDto(LineRequest lineRequest) {
        return new LineRequestDto(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public static SectionRequestDto sectionRequestDto(Long lineId, SectionRequest sectionRequest) {
        return new SectionRequestDto(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }
}
