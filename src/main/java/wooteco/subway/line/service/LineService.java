package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.line.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.repository.LineDao;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final SectionService sectionService;
    private final LineDao lineDao;

    public LineService(SectionService sectionService, LineDao lineDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        validateLineName(lineRequest);
        Line newLine = lineDao.save(lineRequest.toLine());
        sectionService.save(newLine, lineRequest);
        return new LineResponse(newLine);
    }

    private void validateLineName(LineRequest lineRequest) {
        if (checkNameDuplicate(lineRequest)) {
            throw new DuplicatedNameException("중복된 이름의 노선이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(LineRequest lineRequest) {
        return lineDao.validateDuplicateName(lineRequest.getName());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<Long> stationIds = sectionService.findAllSectionsId(id);
        List<StationResponse> stations = sectionService.findStationsByIds(stationIds);
        return new LineResponse(line, stations);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public void update(Long id, LineRequest lineRequest) {
        Line updatedLine = validatesRequest(id, lineRequest);
        lineDao.update(updatedLine);
    }

    private Line validatesRequest(Long id, LineRequest lineRequest) {
        Line currentLine = lineDao.findById(id);

        validateUsableName(lineRequest.getName(), currentLine.getName());
        return new Line(id, lineRequest.getName(), lineRequest.getColor());
    }

    private void validateUsableName(String newName, String oldName) {
        if (lineDao.validateUsableName(newName, oldName)) {
            throw new InvalidInsertException("변경할 수 없는 이름입니다.");
        }
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
