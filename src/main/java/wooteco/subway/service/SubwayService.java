package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.LineWithStationsDto;
import wooteco.subway.service.dto.SimpleStationDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubwayService {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public SubwayService(LineService lineService, SectionService sectionService, StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public LineWithStationsDto findAllInfoByLineId(Long id) {
        final LineDto lineDto = lineService.findById(id);
        final Sections sections = new Sections(sectionService.findAlByLineId(id));
        final Set<SimpleStationDto> stations = sections.toSet();
        final List<StationResponse> stationResponses = makeStationResponse(stations);
        return new LineWithStationsDto(lineDto, sortByStationId(stationResponses));
    }

    public void insertSectionInLine(Long id, SectionInsertRequest sectionInsertRequest) {
        // 1. 해당 LineId가 존재하는지 확인한다.
        // 1-1. 존재하지 않으면 LineNotFoundException 던진다.
        // 2. 해당 LineId를 가진 모든 섹션을 가져온다.
        //
    }

    private List<StationResponse> sortByStationId(List<StationResponse> stationResponses) {
        return stationResponses.stream()
                .sorted(Comparator.comparing(StationResponse::getId))
                .collect(Collectors.toList());
    }

    private List<StationResponse> makeStationResponse(Set<SimpleStationDto> stations) {
        return stationService.makeStationResponses(stations);
    }

}
