package wooteco.subway.section;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.UniqueSectionDeleteException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.StationService;
import wooteco.subway.station.dto.StationResponse;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao,
        StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public LineResponse create(Long lindId, SectionRequest sectionRequest) {
        Section section = new Section(lindId, sectionRequest);
        Sections sections = new Sections(sectionDao.findSectionsByLineId(lindId));
        sections.validateSectionStations(section);
        return null;
    }

    public void initialize(Long id, LineRequest request){
        Section section = new Section(id, request.getUpStationId(),
            request.getDownStationId(), request.getDistance());
        sectionDao.save(section);
    }

    public List<StationResponse> findAllByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(lineId));
        return sections.sortedStationIds()
            .stream()
            .map(stationService::findById)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id, Long stationId) {
        if(sectionDao.findSectionsByLineId(id).size() == 1){
            throw new UniqueSectionDeleteException();
        }

        Sections sections = new Sections(sectionDao.findById(id, stationId));
        sectionDao.deleteById(id, stationId);
        Long upStationId = sections.findUpStationId(stationId);
        Long downStationId = sections.findDownStationId(stationId);
        if(sections.isNotEndPoint()){
            sectionDao.save(new Section(id, upStationId, downStationId, sections.sumDistance()));
        }
    }

    public void deleteAllByLineId(Long lineId) {
        sectionDao.deleteAllById(lineId);
    }
}
