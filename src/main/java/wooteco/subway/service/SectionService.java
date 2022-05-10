package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.StationResponse;

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
}
