package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final JdbcSectionDao jdbcSectionDao;
    private final StationService stationService;

    public SectionService(JdbcSectionDao jdbcSectionDao, StationService stationService) {
        this.jdbcSectionDao = jdbcSectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public Section save(Long lineId, SectionRequest sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();
        Section inputSection = new Section(lineId, stationService.getStation(upStationId).getId(),
                stationService.getStation(downStationId).getId(), distance);

        Sections sections = new Sections(getSectionsByLineId(lineId));
        Section connectedPoint = sections.connectSection(inputSection);

        Long id = jdbcSectionDao.save(inputSection);
        update(lineId, connectedPoint);

        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public List<Section> getSectionsByLineId(long lineId) {
        return jdbcSectionDao.findSectionsByLineId(lineId);
    }

    private void update(Long lineId, Section section) {
        jdbcSectionDao.update(lineId, section);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(getSectionsByLineId(lineId));
        Sections deletedSections = sections.deleteSection(stationId);
        if (deletedSections.isExistSection()) {
            jdbcSectionDao.save(deletedSections.getSections().get(deletedSections.size() - 1));
        }
        jdbcSectionDao.delete(stationId, lineId);
    }
}
