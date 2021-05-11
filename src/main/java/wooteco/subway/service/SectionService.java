package wooteco.subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.notFound.LineNotFoundException;
import wooteco.subway.exception.notFound.StationNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Transactional
    public Section insertSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        final Station upStation =
            stationDao.findStationById(upStationId).orElseThrow(StationNotFoundException::new);
        final Station downStation =
            stationDao.findStationById(downStationId).orElseThrow(StationNotFoundException::new);

        final Sections sections = Sections.create(sectionDao.findAllByLineId(lineId));

        if(sections.isEmpty()) {
            throw new LineNotFoundException();
        }

        final Section createdSection = Section.create(upStation, downStation, distance);
        sections.affectedSection(createdSection).ifPresent(sectionDao::update);
        sectionDao.save(createdSection, lineId);

        return createdSection;
    }

}
