package wooteco.subway.line.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SectionRepository {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionRepository(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public List<Section> findAllByLineId(Long id) {
        List<Section> findSections = sectionDao.findAllByLineId(id);
        List<Section> sections = new ArrayList<>();
        for (Section section : findSections) {
            Station upStation = findStationByStationId(section.upStation().id());
            Station downStation = findStationByStationId(section.downStation().id());
            sections.add(new Section(section.id(), id, upStation, downStation, section.distance()));
        }
        return sections;
    }

    private Station findStationByStationId(Long id) {
        return findStationById(id);
    }

    public Section save(Long lineId, Long upStationId, Long downStationId, int distance) {
        Station upStation = findStationById(upStationId);
        Station downStation = findStationById(downStationId);
        return sectionDao.save(new Section(lineId, upStation, downStation, distance));
    }

    private Station findStationById(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalStateException("[ERROR] 존재하지 않는 역입니다."));
    }

    public void update(Long lineId, Section section) {
        sectionDao.update(new Section(section.id(), lineId, section.upStation(), section.downStation(), section.distance()));
    }
}
