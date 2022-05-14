package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse create(final LineRequest lineRequest) {
        validateNameExist(lineRequest);
        final Line savedLine = lineDao.save(lineRequest.toEntity());

        final Section section = lineRequest.toSectionEntity();
        sectionDao.save(new Section(section.getUpStation(), section.getDownStation(), section.getDistance(), savedLine.getId()));

        return LineResponse.from(savedLine, getStationsByLine(savedLine.getId()));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> LineResponse.from(line, getStationsByLine(line.getId())))
                .collect(Collectors.toList());
    }

    private List<Station> getStationsByLine(Long lineId) {
        return stationDao.findAllByLineId(lineId);
    }

    @Transactional(readOnly = true)
    public LineResponse getById(final Long id) {
        final Line line = extractLine(lineDao.findById(id));
        return LineResponse.from(line, getStationsByLine(line.getId()));
    }

    @Transactional
    public void modify(final Long id, final LineRequest lineRequest) {
        validateNameExist(lineRequest);
        final Line line = lineRequest.toEntity();
        extractLine(lineDao.findById(id));
        lineDao.update(id, line);
    }

    private void validateNameExist(final LineRequest line) {
        if (lineDao.findByName(line.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 같은 이름의 노선이 존재합니다.");
        }
    }

    @Transactional
    public void remove(final Long id) {
        extractLine(lineDao.findById(id));
        lineDao.deleteById(id);
    }

    private Line extractLine(final Optional<Line> wrappedLine) {
        return wrappedLine.orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
    }
}
