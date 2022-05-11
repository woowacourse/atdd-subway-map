package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.StationResponse;

@Service
public class SectionService {

    private final StationService stationService;
    private final JdbcSectionDao jdbcSectionDao;

    public SectionService(StationService stationService, JdbcSectionDao jdbcSectionDao) {
        this.stationService = stationService;
        this.jdbcSectionDao = jdbcSectionDao;
    }

    public Long createSection(Section section) {
        return jdbcSectionDao.save(section);
    }

    public List<StationResponse> getStationsByLineId(Long lineId) {
        return jdbcSectionDao.findByLineId(lineId)
                .getStationIds()
                .stream()
                .map(stationService::getStation)
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = jdbcSectionDao.findByLineIdAndStationId(lineId, stationId);
        sections.validateLengthToDeletion();
        Section upStationSection = sections.getSectionStationIdEqualsUpStationId(stationId);
        Section downStationSection = sections.getSectionStationIdEqualsDownStationId(stationId);
        jdbcSectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
        jdbcSectionDao.updateDownStationIdByLineIdAndUpStationId(lineId, downStationSection.getUpStationId(),
                upStationSection.getDownStationId());

        return true;
    }
}
