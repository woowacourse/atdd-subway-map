package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
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

    public void addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        final List<Section> sections = sectionDao.findByLineId(lineId);
        final Line line = lineDao.findById(lineId);

        final Line savedLine = new Line(line.getName(), line.getColor(), sections);
        final Station upStation = stationDao.findById(upStationId);
        final Station downStation = stationDao.findById(downStationId);
        savedLine.addSection(new Section(upStation, downStation, distance));

        sectionDao.deleteByLineId(lineId);
        sectionDao.save(savedLine.getSections(), lineId);
    }

    public void deleteSection(Long lineId, Long stationId) {
        final List<Section> sections = sectionDao.findByLineId(lineId);
        final Line savedLine = lineDao.findById(lineId);

        final Line line = new Line(savedLine.getName(), savedLine.getColor(), sections);
        final Station station = stationDao.findById(stationId);

        line.deleteSection(station);

        sectionDao.deleteByLineId(lineId);
        sectionDao.save(line.getSections(), lineId);
    }
}
