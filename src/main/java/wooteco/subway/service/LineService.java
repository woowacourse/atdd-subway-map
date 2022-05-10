package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
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

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(final LineRequest lineRequest) {
        validateLineName(lineRequest.getName());
        final Line line = lineDao.save(lineRequest.toEntity());

        final Station upStation = stationDao.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new NoSuchElementException("해당하는 지하철역을 찾을 수 없습니다."));
        final Station downStation = stationDao.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new NoSuchElementException("해당하는 지하철역을 찾을 수 없습니다."));
        sectionDao.save(new Section(line.getId(), upStation, downStation, lineRequest.getDistance()));

        return LineResponse.from(line, List.of(upStation, downStation));
    }

    private void validateLineName(final String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalStateException("이미 존재하는 노선입니다.");
        }
    }

    public List<LineResponse> showLines() {
        return lineDao.findAll()
            .stream()
            .map(line -> LineResponse.from(line, findStations(line.getId())))
            .collect(Collectors.toList());
    }

    public LineResponse showLine(final Long id) {
        return lineDao.findById(id)
            .map(line -> LineResponse.from(line, findStations(line.getId())))
            .orElseThrow(() -> new NoSuchElementException("해당 노선 ID가 존재하지 않습니다."));
    }

    private List<Station> findStations(final Long id) {
        Sections sections = new Sections(sectionDao.findByLineId(id));
        return sections.sortSections();
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        Line line = lineDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 노선 ID가 존재하지 않습니다."));
        line.updateName(lineRequest.getName());
        line.updateColor(lineRequest.getColor());
        lineDao.update(id, line);
    }

    public void deleteLine(final Long id) {
        lineDao.deleteById(id);
    }
}
