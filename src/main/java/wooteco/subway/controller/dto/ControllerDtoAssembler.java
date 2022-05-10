package wooteco.subway.controller.dto;

import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.controller.dto.section.SectionRequest;
import wooteco.subway.controller.dto.station.StationResponse;
import wooteco.subway.service.dto.line.LineRequestDTO;
import wooteco.subway.service.dto.line.LineResponseDTO;
import wooteco.subway.service.dto.section.SectionRequestDto;
import wooteco.subway.service.dto.station.StationResponseDTO;

public class ControllerDtoAssembler {

    private ControllerDtoAssembler() {
    }

    public static StationResponse stationResponseByDTO(StationResponseDTO stationResponseDTO){
        return new StationResponse(stationResponseDTO.getId(), stationResponseDTO.getName());
    }

    public static LineResponse lineResponseByDTO(LineResponseDTO lineResponseDTO) {
        return new LineResponse(lineResponseDTO.getId(), lineResponseDTO.getName(), lineResponseDTO.getColor());
    }

    public static LineRequestDTO lineRequestDTO(LineRequest lineRequest) {
        return new LineRequestDTO(lineRequest.getName(), lineRequest.getColor());
    }

    public static SectionRequestDto sectionRequestDto(Long lineId, SectionRequest sectionRequest) {
        return new SectionRequestDto(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }
}
