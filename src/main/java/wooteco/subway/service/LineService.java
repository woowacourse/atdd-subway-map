package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionEntity;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.LineNotFoundException;

@Service
public class LineService {

    private static final int NOT_FOUND = 0;

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Long lineId = lineDao.save(line);
        final Station upStation = stationDao.find(request.getUpStationId());
        final Station downStation = stationDao.find(request.getDownStationId());

        sectionDao.save(new Section(lineId, upStation, downStation, request.getDistance()));

        return LineResponse.of(line, List.of(upStation, downStation));
    }

    public void createSection(final Long lineId, final SectionRequest sectionRequest) {
        final Sections sections = new Sections(toSections(sectionDao.findAllByLineId(lineId)));
        final Section newSection = getNewSection(lineId, sectionRequest);
        final Long sectionId = sectionDao.save(newSection);
        sections.add(new Section(sectionId, newSection));
        sectionDao.updateAll(sections.getSections());
    }

    private List<Section> toSections(final List<SectionEntity> sectionEntities) {
        return sectionEntities.stream()
                .map(entity -> new Section(
                        entity.getId(),
                        entity.getLineId(),
                        stationDao.find(entity.getUpStationId()),
                        stationDao.find(entity.getDownStationId()),
                        entity.getDistance()
                ))
                .collect(Collectors.toList());
    }

    private Section getNewSection(Long lineId, SectionRequest sectionRequest) {
        final Station upStation = stationDao.find(sectionRequest.getUpStationId());
        final Station downStation = stationDao.find(sectionRequest.getDownStationId());
        final int distance = sectionRequest.getDistance();
        return new Section(lineId, upStation, downStation, distance);
    }

    public List<LineResponse> showLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, findStations(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(final long id) {
        final Line line = lineDao.find(id);
        return LineResponse.of(line, findStations(line.getId()));
    }

    private List<Station> findStations(final Long lineId) {
        final Sections sections = new Sections(toSections(sectionDao.findAllByLineId(lineId)));
        return sections.sortByStation();
    }

    public void updateLine(final long id, final LineRequest request) {
        if (lineDao.update(id, request.getName(), request.getColor()) == NOT_FOUND) {
            throw new LineNotFoundException();
        }
    }

    public void deleteLine(final long id) {
        if (lineDao.delete(id) == NOT_FOUND) {
            throw new LineNotFoundException();
        }
    }
}
