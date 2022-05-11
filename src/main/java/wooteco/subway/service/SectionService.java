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
        validateSectionRequest(sectionRequest);

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

    public void add(long lineId, SectionRequest sectionRequest) {
        validateSectionRequest(sectionRequest);

        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section target = Section.of(lineId, sectionRequest);

        if (sections.isTerminus(target)) {
            sectionDao.save(target);
            return;
        }
    }

    private void validateSectionRequest(SectionRequest request) {
        stationDao.findById(request.getUpStationId());
        stationDao.findById(request.getDownStationId());
    }
}
