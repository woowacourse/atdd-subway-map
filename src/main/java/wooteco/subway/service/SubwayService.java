package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.SimpleStation;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.LineWithStationsDto;

import java.util.List;

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
        final Sections sections = new Sections(sectionService.findAllByLineId(id));
        final List<StationResponse> stationResponses = makeStationResponse(sections.sortSectionsByOrder());
        return new LineWithStationsDto(lineDto, stationResponses);
    }

    public void insertSectionInLine(Long id, SectionInsertRequest sectionInsertRequest) {
        lineService.checkIfExistsById(id);
        sectionService.validateEndStationsAreIncluded(id, sectionInsertRequest);
        sectionService.insertSections(id, sectionInsertRequest);
    }

    public void deleteLine(Long id) {
        sectionService.deleteAllSectionByLineId(id);
        lineService.deleteById(id);
    }

    public void deleteSectionInLine(Long lineId, Long stationId) {
        lineService.checkIfExistsById(lineId);
        sectionService.validateSectionCount(lineId);
        sectionService.deleteSection(lineId, stationId);
    }

    private List<StationResponse> makeStationResponse(List<SimpleStation> stationIds) {
        return stationService.makeStationResponses(stationIds);
    }
}
