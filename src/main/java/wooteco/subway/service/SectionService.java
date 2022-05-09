package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.section.SectionCreationRequest;
import wooteco.subway.dto.section.SectionDeletionRequest;
import wooteco.subway.exception.line.NoSuchLineException;

@Service
@Transactional
public class SectionService {

    private static final int VALID_STATION_COUNT = 1;

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
        final Stations stations = new Stations(stationDao.findAllByLineId(request.getLineId()));
        final int stationCount = stations.calculateMatchCount(request.getUpStationId(), request.getDownStationId());
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

        final Sections sections = sectionDao.findAllByLineId(request.getLineId());
        final Sections deletableSections = sections.findDeletableSections(request.getStationId());
        deleteAll(deletableSections);

        if (deletableSections.needMerge()) {
            final Section mergedSection = deletableSections.toMergedSection();
            sectionDao.insert(mergedSection);
        }
    }

    private void deleteAll(final Sections deletableSections) {
        final List<Long> sectionIds = deletableSections.findAllId();
        for (Long id : sectionIds) {
            sectionDao.deleteById(id);
        }
    }
}
