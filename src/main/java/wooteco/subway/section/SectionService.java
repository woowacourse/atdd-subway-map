package wooteco.subway.section;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.UniqueSectionDeleteException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.SectionRequest;
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

    public void add(Long lindId, SectionRequest sectionRequest) {
        Section section = new Section(lindId, sectionRequest);
        Sections sections = new Sections(sectionDao.findSectionsByLineId(lindId));
        sections.validateSectionStations(section);
        if (section.isEndPointOf(sections)) {
            sectionDao.save(section);
            return;
        }
        sections.validateSectionDistance(section);
        Section dividedSection = sections.divideSection(lindId, section);
        sectionDao.deleteBySectionId(sections.sectionToBeDivided(section).getId());
        sectionDao.save(dividedSection);
        sectionDao.save(section);
    }

    public void initialize(Long id, LineRequest request) {
        Section section = new Section(id, request.getUpStationId(),
            request.getDownStationId(), request.getDistance());
        sectionDao.save(section);
    }

    public List<StationResponse> findAllByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(lineId));
        return stationService.findAllByIds(sections.sortedStationIds());
    }

    public void deleteById(Long id, Long stationId) {
        if (sectionDao.findSectionsByLineId(id).size() == 1) {
            throw new UniqueSectionDeleteException();
        }

        Sections sections = new Sections(sectionDao.findById(id, stationId));
        sectionDao.deleteById(id, stationId);
        if (sections.isNotEndPoint()) {
            Long upStationId = sections.findUpStationId(stationId);
            Long downStationId = sections.findDownStationId(stationId);
            sectionDao.save(new Section(id, upStationId, downStationId, sections.sumDistance()));
        }
    }

    public void deleteAllByLineId(Long lineId) {
        sectionDao.deleteAllById(lineId);
    }
}
