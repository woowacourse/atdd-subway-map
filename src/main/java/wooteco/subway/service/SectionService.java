package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionCreationRequest;
import wooteco.subway.dto.section.SectionDeletionRequest;
import wooteco.subway.exception.line.NoSuchLineException;
import wooteco.subway.exception.section.NoSuchSectionException;

@Service
@Transactional
public class SectionService {

    private static final int VALID_STATION_COUNT = 1;
    private static final int MIN_SECTION_SIZE = 1;
    private static final int MERGE_REQUIRED_SIZE = 2;

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public void save(final SectionCreationRequest request) {
        lineDao.findById(request.getLineId())
                .orElseThrow(NoSuchLineException::new);
        validateStationCount(request);

        sectionDao.findBy(request.getLineId(), request.getUpStationId(), request.getDownStationId())
                .ifPresentOrElse(existingSection -> insertBetween(request, existingSection),
                        () -> extendEndStation(request)
                );
    }

    private void validateStationCount(final SectionCreationRequest request) {
        final long stationCount = stationDao.findAllByLineId(request.getLineId())
                .stream()
                .map(Station::getId)
                .filter(it -> it.equals(request.getUpStationId()) || it.equals(request.getDownStationId()))
                .count();
        if (stationCount != VALID_STATION_COUNT) {
            throw new IllegalArgumentException("상행역과 하행역 중 하나의 역만 노선에 포함되어 있어야 합니다.");
        }
    }

    private void insertBetween(final SectionCreationRequest request, final Section existingSection) {
        sectionDao.deleteById(existingSection.getId());
        final Section newSection = request.toEntity();
        existingSection.assign(newSection)
                .forEach(sectionDao::insert);
    }

    private void extendEndStation(final SectionCreationRequest request) {
        sectionDao.findByLineIdAndUpStationId(request.getLineId(), request.getDownStationId())
                .ifPresent(section -> extendSection(request));

        sectionDao.findByLineIdAndDownStationId(request.getLineId(), request.getUpStationId())
                .ifPresent(section -> extendSection(request));
    }

    private void extendSection(final SectionCreationRequest request) {
        final Section newUpSection = request.toEntity();
        sectionDao.insert(newUpSection);
    }

    public void delete(final SectionDeletionRequest request) {
        lineDao.findById(request.getLineId())
                .orElseThrow(NoSuchLineException::new);

        final List<Section> sections = findDeletableSections(request);
        deleteAll(sections);

        if (sections.size() == MERGE_REQUIRED_SIZE) {
            merge(sections);
        }
    }

    private List<Section> findDeletableSections(final SectionDeletionRequest request) {
        final List<Section> sections = findAllByLineId(request.getLineId())
                .stream()
                .filter(it -> it.contains(request.getStationId()))
                .collect(Collectors.toList());

        if (sections.isEmpty()) {
            throw new NoSuchSectionException();
        }
        return sections;
    }

    private List<Section> findAllByLineId(final Long lineId) {
        final List<Section> allSections = sectionDao.findAllByLineId(lineId);
        if (allSections.size() == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("구간을 삭제할 수 없습니다.");
        }
        return allSections;
    }

    private void deleteAll(final List<Section> sections) {
        sections.stream()
                .map(Section::getId)
                .forEach(sectionDao::deleteById);
    }

    private void merge(final List<Section> sections) {
        final Section firstSection = sections.get(0);
        final Section secondSection = sections.get(1);
        final Section mergedSection = firstSection.merge(secondSection);
        sectionDao.insert(mergedSection);
    }
}
