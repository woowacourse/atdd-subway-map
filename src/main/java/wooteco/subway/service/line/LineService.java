package wooteco.subway.service.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.service.section.SectionService;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao,
        SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        if (lineDao.existsByName(lineRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 지하철 노선 이름입니다.");
        }

        Line newLine = lineDao.save(lineRequest.toLineDomain());
        sectionService.createSection(lineRequest, newLine.getId());
        return LineResponse.of(newLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        List<Line> lines = lineDao.findAll();
        return lines
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(Long id) {
        Line line = lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다."));
        Sections sections = new Sections(sectionService.findByLineId(id));
        return LineResponse.of(line, sections);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        if (!lineDao.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철 노선입니다.");
        }

        Line updateLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(updateLine);
    }

    @Transactional
    public void deleteLine(Long id) {
        sectionService.deleteSectionsByLineId(id);
        lineDao.deleteById(id);
    }
}
