package wooteco.subway.section;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.exception.section.NotEnoughSectionException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Transactional
    public Section createSection(Section section, Long lineId) {
        //모든 섹션들을 받아옴
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        // 모든 섹션리스트에 새로 추가할 섹션을 넣어줌
        // 영향가는 섹션을 찾아서 업데이트 해줌
        Optional<Section> affectedSection = sections.affectedSection(section);

        // 새로운 내용돌을 추가해줌

        sections.add(section);

        return sectionDao.saveAffectedSections(section, affectedSection, lineId);
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {
        lineDao.findLineById(lineId).orElseThrow(LineNotFoundException::new);
        stationDao.findStationById(stationId).orElseThrow(StationNotFoundException::new);
        if (sectionDao.findSectionsByLineId(lineId).hasSize(1)) {
            throw new NotEnoughSectionException();
        }

        List<Section> sections = sectionDao.findSectionContainsStationId(lineId, stationId);
        final Sections foundSections = Sections.from(sections);

        sectionDao.removeSections(lineId, sections);

        Optional<Section> affectedSection = foundSections.transformSection(stationId);

        affectedSection.ifPresent(section -> sectionDao.insertSection(section, lineId));
    }
}
