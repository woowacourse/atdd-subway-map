package wooteco.subway.controller.dto;

import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.controller.dto.station.StationResponse;
import wooteco.subway.service.dto.line.LineRequestDto;
import wooteco.subway.service.dto.line.LineResponseDto;
import wooteco.subway.service.dto.station.StationResponseDto;

public class ControllerDtoAssembler {

    public ControllerDtoAssembler() {
    }

    public static StationResponse stationResponse(StationResponseDto stationResponseDto) {
        return new StationResponse(stationResponseDto.getId(), stationResponseDto.getName());
    }

    public static LineRequestDto lineRequestDto(LineRequest lineRequest) {
        return new LineRequestDto(lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
    }

    public static LineResponse lineResponse(LineResponseDto lineResponseDto) {
        return new LineResponse(lineResponseDto.getId(), lineResponseDto.getName(), lineResponseDto.getColor());
    }
}
