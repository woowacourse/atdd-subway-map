package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineWithStationRequest;
import wooteco.subway.dto.LineWithStationResponse;
import wooteco.subway.dto.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineWithStationResponse save(final LineWithStationRequest lineRequest) {
        final Line line = new Line(
                lineRequest.getName(),
                lineRequest.getColor()
        );
        final Line newLine = lineDao.save(line);

        sectionDao.save(
                newLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
        );

        List<StationResponse> stationResponses = getStationResponsesByLine(newLine);

        return new LineWithStationResponse(
                newLine.getId(),
                newLine.getName(),
                newLine.getColor(),
                stationResponses
        );
    }

    private List<StationResponse> getStationResponsesByLine(Line newLine) {
        Sections sections = new Sections(sectionDao.getSectionByLineId(newLine.getId()));
        Set<Long> stationIds = sections.getStations();
        return stationIds.stream()
                .map(stationDao::findById)
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public List<LineWithStationResponse> findAll() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(it -> new LineWithStationResponse(it.getId(), it.getName(), it.getColor(), getStationResponsesByLine(it)))
                .collect(Collectors.toList());
    }

    public LineWithStationResponse findById(final Long id) {
        final Line line = lineDao.findById(id);
        return new LineWithStationResponse(line.getId(), line.getName(), line.getColor(), getStationResponsesByLine(line));
    }

    public void update(final Long id, final LineWithStationRequest lineRequest) {
        lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
