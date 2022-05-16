package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.StationNotFoundException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public List<Station> findSectionStationsByLineId(final Long lineId) {
        return new Sections(sectionDao.findSectionStationsByLineId(lineId))
            .getSortedSections()
            .stream()
            .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
            .distinct()
            .map(id -> stationDao.findById(id).get())
            .sorted(Comparator.comparing(Station::getId))
            .collect(Collectors.toList());
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final List<Section> sectionsBeforeAddSection = sectionDao.findSectionStationsByLineId(lineId);
        Section newSection = sectionRequest.toEntity(lineId);
        newSection = sectionDao.save(newSection);
        new Sections(sectionsBeforeAddSection).addSection(newSection)
            .ifPresent(sectionDao::update);
    }

    @Transactional
    public void deleteSection(final Long id, final Long stationId) {
        lineDao.findById(id)
            .orElseThrow(() -> new LineNotFoundException("[ERROR] 해당 노선이 없습니다."));
        stationDao.findById(id)
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));

        final Sections sections = new Sections(sectionDao.findSectionStationsByLineId(id));
        final boolean isMiddleDelete = sections.isMiddleDelete(stationId);
        sectionDao.deleteById(sections.deleteSectionByStationId(stationId));
        if (isMiddleDelete) {
            final Section updatedSection = sections.getUpdatedSection(
                new Sections(sectionDao.findSectionStationsByLineId(id)));
            sectionDao.update(updatedSection);
        }
    }
}
