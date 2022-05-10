package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.ui.dto.SectionRequest;

@Service
public class SectionService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SectionService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void create(Long lineId, SectionRequest sectionRequest) {
        validRequest(sectionRequest);
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section newSection = sectionRequest.toEntity();
        List<Section> needUpdateSections = sections.add(newSection);

        for (Section section : needUpdateSections) {
            saveOrUpdate(newSection, section);
        }
    }

    private void saveOrUpdate(Section newSection, Section section) {
        if (newSection.equals(section)) {
            sectionDao.save(section);
            return;
        }
        sectionDao.update(section);
    }

    private void validRequest(SectionRequest sectionRequest) {
        if (!stationDao.existsById(sectionRequest.getUpStationId())
                || !stationDao.existsById(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException("존재하지 않는 역을 등록할 수 없습니다.");
        }
        if (!lineDao.existsById(sectionRequest.getLineId())) {
            throw new IllegalArgumentException("존재하지 않는 노선에 등록할 수 없습니다.");
        }
    }

    @Transactional
    public void deleteById(Long lineId, Long sectionId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        sectionDao.deleteById(sectionId);

        Optional<Section> deletedSection = sections.delete(sectionId);
        deletedSection.ifPresent(section -> sectionDao.deleteById(section.getId()));
    }
}
