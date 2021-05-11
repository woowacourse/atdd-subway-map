package wooteco.subway.section.servcie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.common.exception.bad_request.WrongSectionInfoException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public Section addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);
        validateSameStation(upStation, downStation);

        Section section = new Section(lineId, upStation, downStation, new Distance(distance));
        Sections sections = sectionDao.findByLineId(lineId);
        if (sections.isEmpty() || sections.isUpLastSection(section) || sections.isDownLastSection(section)) {
            return sectionDao.save(section);
        }
        Section modifiedSection = sections.getModifiedSectionIfCanAdd(section);
        sectionDao.update(modifiedSection);
        return sectionDao.save(section);
    }

    private void validateSameStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new WrongSectionInfoException(String.format("같은 역으로 구간을 추가할 수 없습니다. 상행역: %s, 하행역: %s",
                    upStation.getName().text(), downStation.getName().text()));
        }
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        validateCanDelete(lineId);
        List<Section> sameStationSections = sectionDao.findByLineIdAndStationId(lineId, stationId);
        if (sameStationSections.size() == 1) {
            sectionDao.delete(sameStationSections.get(0));
            return;
        }
        Section firstSection = sameStationSections.get(0);
        Section secondSection = sameStationSections.get(1);
        Section newSection = firstSection.merge(secondSection, stationId);
        sectionDao.save(newSection);
        sectionDao.delete(firstSection);
        sectionDao.delete(secondSection);
    }

    private void validateCanDelete(Long lineId) {
        if (sectionDao.canDelete(lineId)) {
            throw new WrongSectionInfoException(String.format("구간을 삭제할 수 없습니다. 구간 ID: %d", lineId));
        }
    }

    public Sections findByLineId(Long id) {
        return sectionDao.findByLineId(id);
    }
}
