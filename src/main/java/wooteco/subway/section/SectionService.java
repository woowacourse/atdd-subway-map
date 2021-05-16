package wooteco.subway.section;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.UniqueSectionDeleteException;
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

    public void createSectionOfNewLine(Long lineId, Long upStationId, Long downStationId,
        int distance) {
        Section section = new Section(lineId, upStationId,
            downStationId, distance);
        sectionDao.save(section);
    }

    public void create(Long lineId, Long upStationId, Long downStationId, int distance) {
        Section section = new Section(lineId, upStationId,
            downStationId, distance);
        List<Section> sectionList = sectionDao.findSectionsByLineId(lineId);
        Sections sections = new Sections(sectionList, section);
        if (section.isEndPointOf(sections)) {
            sectionDao.save(section);
            return;
        }
        createSectionBetweenSections(lineId, section, sections);
    }

    private void createSectionBetweenSections(Long lindId, Section section, Sections sections) {
        Section dividedSection = sections.divideSection(lindId, section);
        sectionDao.deleteBySectionId(dividedSection.getId());
        sectionDao.save(dividedSection);
        sectionDao.save(section);
    }

    public List<StationResponse> findAllByLineId(Long lineId) {
        List<Section> sectionList = sectionDao.findSectionsByLineId(lineId);
        Sections sections = new Sections(sectionList);
        return stationService.findAllByIds(sections.sortedStationIds());
    }

    public void deleteById(Long lineId, Long stationId) {
        if (isUniqueSectionOfLine(lineId)) {
            throw new UniqueSectionDeleteException();
        }
        List<Section> sectionList = sectionDao.findById(lineId, stationId);
        Sections sections = new Sections(sectionList);
        sectionDao.deleteById(lineId, stationId);
        if (sections.isNotEndPoint()) {
            Long upStationId = sections.findUpStationId(stationId);
            Long downStationId = sections.findDownStationId(stationId);
            sectionDao
                .save(new Section(lineId, upStationId, downStationId, sections.sumDistance()));
        }
    }

    private boolean isUniqueSectionOfLine(Long lineId) {
        return sectionDao.findSectionsByLineId(lineId).size() == 1;
    }

    public void deleteAllByLineId(Long lineId) {
        sectionDao.deleteAllById(lineId);
    }
}
