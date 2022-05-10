package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long save(Section section) {
        List<Section> sections = sectionDao.findSectionsByLineId(section.getLineId());
        if (sections.size() != 0) {
            validateSection(section);
        }
        return sectionDao.save(section);
    }

    private void validateSection(Section section) {
        boolean existUpStation =
                sectionDao.existSectionByLineIdAndStationId(section.getLineId(), section.getUpStationId());
        boolean existDownStation =
                sectionDao.existSectionByLineIdAndStationId(section.getLineId(), section.getDownStationId());

        validateBothStationsIncludeInLine(existUpStation, existDownStation);
        validateBothStationsExcludeInLine(existUpStation, existDownStation);
    }

    private void validateBothStationsExcludeInLine(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 노선에 포함되어 있습니다.");
        }
    }

    private void validateBothStationsIncludeInLine(boolean existUpStation, boolean existDownStation) {
        if (!(existUpStation || existDownStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 포함되어있지 않습니다.");
        }
    }

    public List<Section> findAll() {
        return sectionDao.findAll();
    }
}
