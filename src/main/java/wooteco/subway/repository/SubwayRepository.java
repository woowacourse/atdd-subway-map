package wooteco.subway.repository;

import org.springframework.stereotype.Component;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.StationNotExistException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;

import java.util.*;
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
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        Deque<Long> sortedStationIds = new ArrayDeque<>();
        Map<Long, Long> upStationIds = new LinkedHashMap<>();
        Map<Long, Long> downStationIds = new LinkedHashMap<>();

        for (Section section : sections) {
            upStationIds.put(section.getDownStationId(), section.getUpStationId());
            downStationIds.put(section.getUpStationId(), section.getDownStationId());
        }
        Section now = sections.get(0);
        sortedStationIds.addFirst(now.getUpStationId());

        while (upStationIds.containsKey(sortedStationIds.peekFirst())) {
            Long currentId = sortedStationIds.peekFirst();
            sortedStationIds.addFirst(upStationIds.get(currentId));
        }

        while (downStationIds.containsKey(sortedStationIds.peekLast())) {
            Long currentId = sortedStationIds.peekLast();
            sortedStationIds.addLast(downStationIds.get(currentId));
        }

        return sortedStationIds.stream()
                .map(id -> stationDao.findById(id)
                        .orElseThrow(() -> new StationNotExistException(id)))
                .collect(Collectors.toList());
    }
}
