package wooteco.subway.section.servcie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.NoSuchElementException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);
        validateSameStation(upStation, downStation);

        Section section = new Section(lineId, upStation, downStation, new Distance(distance));
        Sections sections = sectionDao.findByLineId(lineId);
        if (sections.isUpLastSection(section) || sections.isDownLastSection(section)) {
            sectionDao.save(section);
            return;
        }
        Section modifiedSection = sections.getModifiedSectionIfCanAdd(section);
        sectionDao.save(section);
        sectionDao.update(modifiedSection);
    }

    private void validateSameStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("같은 역으로 구간을 추가할 수 없습니다.");
        }
    }
}
