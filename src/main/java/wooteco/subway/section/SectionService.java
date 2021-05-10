package wooteco.subway.section;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionDao sectionDao;

    public Section createSection(Section section, Long lineId) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        Optional<Section> affectedSection = sections.affectedSection(section);
        sections.add(section);

        return sectionDao.saveAffectedSections(section, affectedSection, lineId);
    }

    public void removeSection(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findSectionContainsStationId(lineId, stationId);
        final Sections foundSections = Sections.from(sections);

        //TODO : 라인 아이디가 없을 시 예외처리
        sectionDao.removeSections(lineId, sections);

        // TODO : 연결하자.
        Optional<Section> affectedSection = foundSections.transformSection(stationId);

        affectedSection.ifPresent(section -> sectionDao.insertSection(section, lineId));
    }
}
