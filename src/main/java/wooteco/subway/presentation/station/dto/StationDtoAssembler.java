package wooteco.subway.presentation.station.dto;

import org.springframework.stereotype.Component;
import wooteco.subway.domain.station.Station;

@Component
public class StationDtoAssembler {

    public StationResponse station(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

}
