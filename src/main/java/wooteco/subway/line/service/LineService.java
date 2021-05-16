package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        validateLineName(lineRequest);
        Line newLine = lineDao.save(lineRequest.toLine());
        sectionService.save(newLine, lineRequest);
        return new LineResponse(newLine);
    }

    private void validateLineName(LineRequest lineReq) {
        if (checkNameDuplicate(lineReq)) {
            throw new DuplicatedNameException("중복된 이름의 노선이 존재합니다.");
        }
        sectionService.validateStations(lineReq.getUpStationId(), lineReq.getDownStationId());
    }

    private boolean checkNameDuplicate(LineRequest lineRequest) {
        return lineDao.isExistingName(lineRequest.getName());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<Long> stationIds = sectionService.findAllSectionsId(id);
        List<StationResponse> stations = sectionService.findStationsByIds(stationIds);
        return new LineResponse(line, stations);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        Line currentLine = lineDao.findById(id);
        validatesChangeName(lineRequest.getName(), currentLine.getName());
        currentLine.update(lineRequest);
        lineDao.update(currentLine);
    }

    private void validatesChangeName(String newName, String currentName) {
        if (lineDao.existNewNameExceptCurrentName(newName, currentName)) {
            throw new InvalidInsertException("변경할 수 없는 이름입니다.");
        }
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
