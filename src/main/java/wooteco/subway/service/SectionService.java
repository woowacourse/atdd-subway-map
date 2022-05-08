package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionCreationRequest;
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
                .ifPresent(existingSection -> insertBetween(request, existingSection));

        sectionDao.findByLineIdAndUpStationId(request.getLineId(), request.getDownStationId())
                .ifPresent(section -> extendSection(request));

        sectionDao.findByLineIdAndDownStationId(request.getLineId(), request.getUpStationId())
                .ifPresent(section -> extendSection(request));
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

    private void extendSection(final SectionCreationRequest request) {
        final Section newUpSection = request.toEntity();
        sectionDao.insert(newUpSection);
    }
}
