package wooteco.subway.repository;

import org.springframework.stereotype.Component;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.StationNotExistException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubwayRepository {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SubwayRepository(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public void insertSectionWithLineId(Section section) {
        sectionDao.insert(section);
    }

    public List<Station> findStationsByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getSortedStationIds()
                .stream()
                .map(id -> stationDao.findById(id)
                        .orElseThrow(() -> new StationNotExistException(id)))
                .collect(Collectors.toList());
    }
}
