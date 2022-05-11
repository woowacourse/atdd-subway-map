package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;

@Repository
public class CheckRepository {
    private static final String NO_STATION_ID_ERROR_MESSAGE = "해당 아이디의 역을 찾을 수 없습니다.";
    private static final String NO_LINE_ID_ERROR_MESSAGE = "해당 아이디의 노선을 찾을 수 없습니다.";
    private static final String NO_SECTION_ID_ERROR_MESSAGE = "해당 아이디의 구간을 찾을 수 없습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public CheckRepository(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public void checkSectionExist(Long id) {
        if (!sectionDao.hasSection(id)) {
            throw new IllegalArgumentException(NO_SECTION_ID_ERROR_MESSAGE);
        }
    }

    public void checkLineExist(Long lineId) {
        if (!lineDao.hasLine(lineId)) {
            throw new IllegalArgumentException(NO_LINE_ID_ERROR_MESSAGE);
        }
    }

    public void checkStationExist(Long stationId) {
        if (!stationDao.hasStation(stationId)) {
            throw new IllegalArgumentException(NO_STATION_ID_ERROR_MESSAGE);
        }
    }

    public void checkStationsExist(Section section) {
        if (!stationDao.hasValidStations(section)) {
            throw new IllegalArgumentException(NO_STATION_ID_ERROR_MESSAGE);
        }
    }
}
