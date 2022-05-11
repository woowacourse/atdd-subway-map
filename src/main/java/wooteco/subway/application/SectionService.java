package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        final List<Section> sections = sectionDao.findByLineId(lineId);
        final Line line = lineDao.findById(lineId);

        final Line savedLine = Line.createWithoutId(line.getName(), line.getColor(), sections);
        final Station upStation = stationDao.findById(upStationId);
        final Station downStation = stationDao.findById(downStationId);
        savedLine.addSection(Section.createWithoutId(upStation, downStation, distance));

        updateSection(lineId, savedLine);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        final List<Section> sections = sectionDao.findByLineId(lineId);
        final Line savedLine = lineDao.findById(lineId);

        final Line line = Line.createWithoutId(savedLine.getName(), savedLine.getColor(), sections);
        final Station station = stationDao.findById(stationId);

        line.deleteSection(station);

        updateSection(lineId, line);
    }

    private void updateSection(Long lineId, Line line) {
        sectionDao.deleteByLineId(lineId);
        sectionDao.save(line.getSections(), lineId);
    }
}
