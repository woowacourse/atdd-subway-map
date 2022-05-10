package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {
    private static final String INVALID_STATION_ID_ERROR_MESSAGE = "구간 안에 존재하지 않는 아이디의 역이 있습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section save(Section section) {
        checkStationExist(section);
        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));
        sections.validateSave(section);
        if (sections.isMiddle(section)) {
            Section base = sections.findMiddleBase(section);
            sectionDao.save(base.calculateRemainSection(section));
            sectionDao.delete(base.getId());
        }
        return sectionDao.findById(sectionDao.save(section));
    }

    private void checkStationExist(Section section) {
        if (!stationDao.hasStation(section.getUpStationId()) || !stationDao.hasStation(section.getDownStationId())) {
            throw new IllegalArgumentException(INVALID_STATION_ID_ERROR_MESSAGE);
        }
    }

    public Section findById(Long id) {
        return sectionDao.findById(id);
    }

    public List<Station> findStationsOfLine(Long lineId) {
        return new Sections(sectionDao.findAllByLineId(lineId)).calculateStations();
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateDelete();
        Station station = stationDao.findById(stationId);
        if (sections.isSide(station)) {
            sectionDao.delete(sections.findSide(station).getId());
            return;
        }
        List<Section> linkedSections = sections.findByStation(station);
        sectionDao.save(new Sections(linkedSections).calculateCombinedSection(station));
        sectionDao.deleteAllBySections(linkedSections);
    }
}
