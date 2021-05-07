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
}
