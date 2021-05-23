package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final SectionService sectionService;
    private final LineDao lineDao;

    public LineService(SectionService sectionService, LineDao lineDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
    }

    @Transactional
    public Long save(LineRequest lineRequest) {
        Line line = lineRequest.toLineEntity();
        validateLine(line);
        Long lineId = lineDao.save(line);
        sectionService.save(lineId, lineRequest);
        return lineId;
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));

        lineDao.findByName(lineRequest.getName())
                .filter(foundLine -> !foundLine.sameAs(id))
                .ifPresent(foundLine -> {
                    throw new IllegalArgumentException("중복된 노선입니다.");
                });
        Line updatedLine = new Line(id, line.getName(), line.getColor());

        lineDao.update(updatedLine);
    }

    private void validateLine(Line line) {
        lineDao.findByName(line.getName())
                .ifPresent(matchedLine -> {
                    throw new IllegalArgumentException("중복된 노선입니다.");
                });
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        lineDao.delete(id);
        sectionService.deleteSectionByLineId(id);
    }

    public LineResponse findById(Long id) {
        return lineDao.findById(id)
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철 역이 없습니다."));
    }

    public LineResponse findSectionsById(Long lineId) {
        LineResponse line = findById(lineId);
        List<StationResponse> sections = sectionService.findSectionByLineId(lineId);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sections);
    }

    @Transactional
    public LineResponse saveSection(Long lineId, LineRequest lineRequest) {
        sectionService.saveSectionOfExistLine(lineId, lineRequest);
        LineResponse lineResponse = findById(lineId);
        List<StationResponse> section = sectionService.findSectionByLineId(lineId);
        return new LineResponse(lineId, lineResponse.getName(), lineResponse.getColor(), section);
    }
}
