package wooteco.subway.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.CommonSectionDao;
import wooteco.subway.dao.CommonStationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.SectionDto;

@Repository
public class SectionRepository {

    private final CommonStationDao stationDao;
    private final CommonSectionDao sectionDao;
    private final LineRepository lineRepository;

    public SectionRepository(final CommonStationDao stationDao, final CommonSectionDao sectionDao,
                             final LineRepository lineRepository) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public Line findLineById(final Long lineId) {
        return lineRepository.findById(lineId);
    }

    @Transactional(readOnly = true)
    public Station findStationById(final Long stationId) {
        return stationDao.findById(stationId);
    }

    @Transactional
    public void addSections(final Long lineId, final List<Section> sections) {
        for (Section section : sections) {
            sectionDao.save(lineId, SectionDto.from(section));
        }
    }

    @Transactional
    public void deleteSections(final Long lineId, final List<Section> sections) {
        for (Section section : sections) {
            sectionDao.save(lineId, SectionDto.from(section));
        }
    }
}
