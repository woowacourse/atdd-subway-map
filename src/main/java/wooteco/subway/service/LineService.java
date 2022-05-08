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
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        checkExistLineByName(lineRequest);
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Long id = lineDao.save(line);
        final Section section = saveSectionByCreateLine(lineRequest, id);

        final Station upStation = findByStationId(section.getUpStationId());
        final Station downStation = findByStationId(section.getDownStationId());
        final StationResponse upStationResponse = new StationResponse(upStation.getId(), upStation.getName());
        final StationResponse downStationResponse = new StationResponse(downStation.getId(), downStation.getName());

        return new LineResponse(id, line.getName(), line.getColor(), List.of(upStationResponse, downStationResponse));
    }

    private void checkExistLineByName(LineRequest lineRequest) {
        if (lineDao.hasLine(lineRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
    }

    private Section saveSectionByCreateLine(LineRequest lineRequest, Long newLineId) {
        final Section section = new Section(newLineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
        sectionDao.save(section);
        return section;
    }

    private Station findByStationId(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("구간 상행역 생성에 오류가 발생했습니다."));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        final Line line = checkExistLineById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void updateLine(Long id, String name, String color) {
        checkExistLineById(id);
        lineDao.updateById(id, name, color);
    }

    public void deleteLine(Long id) {
        checkExistLineById(id);
        lineDao.deleteById(id);
    }

    private Line checkExistLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 노선이 존재하지 않습니다."));
    }
}
