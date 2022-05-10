package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.RowDuplicatedException;
import wooteco.subway.exception.RowNotFoundException;

@Service
public class LineService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        validateDistinct(lineRequest.getName());
        Line savedLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        final Station upStation = stationDao.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new RowNotFoundException(String.format("%d의 id를 가진 역이 존재하지 않습니다.", lineRequest.getUpStationId())));
        final Station downStation = stationDao.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new RowNotFoundException(String.format("%d의 id를 가진 역이 존재하지 않습니다.", lineRequest.getDownStationId())));
        final Distance distance = new Distance(lineRequest.getDistance());
        Section savedSection = sectionDao.save(new Section(upStation, downStation, distance), savedLine.getId());
        return LineResponse.of(savedLine, savedSection);
    }

    private void validateDistinct(String name) {
        Optional<Line> line = lineDao.findByName(name);
        if (line.isPresent()) {
            throw new RowDuplicatedException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
            .stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public LineResponse findOne(Long id) {
        final Optional<Line> foundLine = lineDao.findById(id);
        return LineResponse.from(foundLine.orElseThrow(
            () -> new RowNotFoundException("조회하고자 하는 노선이 존재하지 않습니다.")
        ));
    }

    public void update(Long id, LineRequest lineRequest) {
        final boolean isUpdated = lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
        if (!isUpdated) {
            throw new RowNotFoundException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    public void delete(Long id) {
        final boolean isDeleted = lineDao.delete(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
