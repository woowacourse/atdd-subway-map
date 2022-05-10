package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionRequest;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        stationDao.findById(sectionRequest.getUpStationId());
        stationDao.findById(sectionRequest.getDownStationId());

        Section section = Section.of(lineId, sectionRequest);
        sectionDao.save(section);
    }

    public List<Station> findStationsByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        List<Long> stationIds = sections.getSortedStationIds();

        List<Station> stations = new LinkedList<>();
        for (Long id : stationIds) {
            stations.add(stationDao.findById(id));
        }

        return stations;
    }
}
