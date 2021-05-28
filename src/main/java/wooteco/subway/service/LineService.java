package wooteco.subway.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.repository.LineDao;
import wooteco.subway.repository.SectionDao;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionService sectionService,
        SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        validateDuplication(line.getName());
        long id = lineDao.save(line);
        line.setId(id);

        SectionResponse sectionResponse = sectionService.createSection(
            id,
            lineRequest.getUpStationId(),
            lineRequest.getDownStationId(),
            lineRequest.getDistance()
        );

        List<StationResponse> stationResponses = Arrays.asList(
            sectionResponse.getUpStationResponse(),
            sectionResponse.getDownStationResponse()
        );

        return new LineResponse(line, stationResponses);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            line.setSections(sectionDao.findByLineId(line.getId()));
        }
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id)
            .orElseThrow(LineNotFoundException::new);
        Sections sections = sectionDao.findByLineId(line.getId());
        line.setSections(sections);
        return LineResponse.of(line);
    }

    public void editLine(Long id, LineRequest lineRequest) {
        Line line = lineRequest.toLine(id);
        validateDuplication(line.getName());
        lineDao.updateLine(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = lineDao.findAll()
            .stream()
            .anyMatch(line -> line.getName().equals(name));
        if (isDuplicated) {
            throw new LineDuplicationException();
        }
    }
}
