package wooteco.subway.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.request.LineAndSectionCreateRequest;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.SimpleStation;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.LineWithStationsDto;

import java.util.List;

@Component
public class SubwayFacade {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public SubwayFacade(LineService lineService, SectionService sectionService, StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @Transactional
    public LineDto createLine(LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        lineService.validate(lineAndSectionCreateRequest);
        final Line line = lineService.create(lineAndSectionCreateRequest);
        sectionService.insert(line.getId(), lineAndSectionCreateRequest.toSimpleSection());
        return new LineDto(line);
    }

    public LineWithStationsDto findAllInfoByLineId(Long id) {
        final LineDto lineDto = lineService.findById(id);
        final Sections sections = new Sections(sectionService.findAllByLineId(id));
        final List<StationResponse> stationResponses = makeStationResponse(sections.sortSectionsByOrder());
        return new LineWithStationsDto(lineDto, stationResponses);
    }

    public void insertSectionInLine(Long id, SectionInsertRequest sectionInsertRequest) {
        lineService.checkIfExistsById(id);
        sectionService.validateEndStationsAreIncluded(id, sectionInsertRequest.toSimpleSection());
        sectionService.insertSections(id, sectionInsertRequest.toSimpleSection());
    }

    public void deleteLine(Long id) {
        sectionService.deleteAllSectionByLineId(id);
        lineService.deleteById(id);
    }

    public void deleteSectionInLine(Long lineId, Long stationId) {
        lineService.checkIfExistsById(lineId);
        sectionService.deleteSection(lineId, stationId);
    }

    private List<StationResponse> makeStationResponse(List<SimpleStation> stationIds) {
        return stationService.makeStationResponses(stationIds);
    }

    public List<LineDto> findAllLines() {
        return lineService.findAll();
    }

    public void updateLineById(Long id, LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        lineService.updateById(id, lineAndSectionCreateRequest.toSimpleLine());
    }
}
