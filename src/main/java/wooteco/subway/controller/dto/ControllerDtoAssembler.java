package wooteco.subway.controller.dto;

import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.controller.dto.section.SectionRequest;
import wooteco.subway.controller.dto.station.StationResponse;
import wooteco.subway.service.dto.line.LineRequestDTO;
import wooteco.subway.service.dto.line.LineResponseDTO;
import wooteco.subway.service.dto.section.SectionRequestDto;
import wooteco.subway.service.dto.station.StationResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class ControllerDtoAssembler {

    private ControllerDtoAssembler() {
    }

    public static StationResponse stationResponseByDTO(StationResponseDto stationResponseDTO){
        return new StationResponse(stationResponseDTO.getId(), stationResponseDTO.getName());
    }

    public static LineResponse lineResponseByDTO(LineResponseDTO lineResponseDTO) {
        List<StationResponse> stations = lineResponseDTO.getStations().stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return new LineResponse(lineResponseDTO.getId(), lineResponseDTO.getName(), lineResponseDTO.getColor(), stations);
    }

    public static LineRequestDTO lineRequestDTO(LineRequest lineRequest) {
        return new LineRequestDTO(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public static SectionRequestDto sectionRequestDto(Long lineId, SectionRequest sectionRequest) {
        return new SectionRequestDto(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }
}
