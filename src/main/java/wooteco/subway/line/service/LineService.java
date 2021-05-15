package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.web.line.LineResponse;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineColor;
import wooteco.subway.line.domain.LineName;
import wooteco.subway.line.exception.InvalidLineNameException;
import wooteco.subway.line.exception.WrongLineIdException;
import wooteco.subway.section.domain.EmptySections;
import wooteco.subway.section.domain.OrderedSections;
import wooteco.subway.section.service.SectionService;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public LineResponse save(String lineName, String color, Long upStationId, Long downStationId, Long distance) {
        if (isDuplicatedName(new LineName(lineName))) {
            throw new InvalidLineNameException(String.format("노선 이름이 중복되었습니다. 중복된 노선 이름 : %s", lineName));
        }
        if (isDuplicatedColor(new LineColor(color))) {
            throw new InvalidLineNameException(String.format("노선 색상이 중복되었습니다. 중복된 노선 색상 : %s", color));
        }
        Line nonIdLine = new Line(lineName, color, new EmptySections());
        Line idLine = lineDao.save(nonIdLine);
        OrderedSections lineSections = sectionService.add(idLine.getId(), upStationId, downStationId, distance);

        Line entity = Line.createEntity(idLine, lineSections);
        return LineResponse.of(entity);
    }

    private boolean isDuplicatedName(LineName lineName) {
        return lineDao.checkExistName(lineName);
    }

    private boolean isDuplicatedColor(LineColor lineColor) {
        return lineDao.checkExistColor(lineColor);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        List<Line> withSections = createWithSections(lines);
        List<LineResponse> lineResponses = withSections.stream()
                .map(line -> new LineResponse(line.getId(), line.getName().text(), line.getColor().text()))
                .collect(toList());
        return lineResponses;
    }

    private List<Line> createWithSections(List<Line> lines) {
        Map<Long, OrderedSections> sectionsAllWithId = sectionService.findSectionsWithLineId();
        return lines.stream()
                .map(line -> Line.createEntity(line, sectionsAllWithId.get(line.getId())))
                .collect(toList());
    }

    public LineResponse findById(Long id) {
        Line byId = lineDao.findById(id);
        OrderedSections sections = sectionService.findSections(byId.getId());
        Line entity = Line.createEntity(byId, sections);
        return LineResponse.of(entity);
    }

    public void update(Long lineId, String lineName, String color) {
        if (!lineDao.checkExistId(lineId)) {
            throw new WrongLineIdException("노선이 존재하지 않습니다.");
        }
        lineDao.update(new Line(lineId, lineName, color, new EmptySections()));
    }

    public void delete(Long lineId) {
        if (!lineDao.checkExistId(lineId)) {
            throw new WrongLineIdException("노선이 존재하지 않습니다.");
        }
        lineDao.delete(lineId);
        sectionService.deleteSections(lineId);
    }
}
