package wooteco.subway.service.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.service.section.SectionService;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository,
        SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        if (lineRepository.existsByName(lineRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 지하철 노선 이름입니다.");
        }

        Line newLine = lineRepository.save(lineRequest.toLineDomain());
        sectionService.createSection(lineRequest, newLine.getId());
        return LineResponse.of(newLine);
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        Line line = lineRepository.findById(id);
        Sections sections = new Sections(sectionService.findByLineId(id));
        return LineResponse.of(line, sections);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        if (!lineRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }

        Line updateLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineRepository.update(updateLine);
    }

    public void deleteLine(Long id) {
        sectionService.deleteSectionsByLineId(id);
        lineRepository.deleteById(id);
    }
}
