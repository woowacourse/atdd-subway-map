package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotFoundException;
import java.util.List;

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

    @Transactional
    public Section addSection(final long lineId, final Section requestSection) {
        final Station upStation = stationDao.findById(requestSection.getUpStation().getId())
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 지하철역 ID입니다."));
        final Station downStation = stationDao.findById(requestSection.getDownStation().getId())
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 지하철역 ID입니다."));
        lineDao.findById(lineId)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 노선 ID입니다."));

        Section section = new Section(upStation, downStation, requestSection.getDistance(), lineId);
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.add(section);

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.getSections());

        return section;
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.remove(stationId);
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.getSections());
    }

    @Transactional(readOnly = true)
    public List<Station> getStationsByLine(final long lineId) {
        final List<Section> lineSections = sectionDao.findAllByLineId(lineId);
        final Sections sections = new Sections(lineSections);
        return sections.extractStations();
    }
}
