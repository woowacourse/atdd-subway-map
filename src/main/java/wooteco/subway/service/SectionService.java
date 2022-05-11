package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void save(final Long lineId, final SectionRequest sectionRequest) {
        validateStationInSection(sectionRequest);

        Section newSection = convertSection(sectionRequest);
        Sections sections = getAllSections(lineId);
        sections.add(newSection);

        update(lineId, sections);
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        Sections sections = getAllSections(lineId);
        sections.delete(stationId);

        update(lineId, sections);
    }

    private void validateStationInSection(final SectionRequest sectionRequest) {
        if (!stationDao.existStationById(sectionRequest.getUpStationId())) {
            throw new IllegalArgumentException("상행역이 존재하지 않습니다.");
        }
        if (!stationDao.existStationById(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("하행역이 존재하지 않습니다.");
        }
    }

    private Section convertSection(final SectionRequest sectionRequest) {
        return new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    private Sections getAllSections(final Long lineId) {
        return new Sections(sectionDao.findAllById(lineId));
    }

    private void update(final Long lineId, final Sections sections) {
        sectionDao.delete(lineId);
        sectionDao.saveAll(lineId, sections);
    }
}
