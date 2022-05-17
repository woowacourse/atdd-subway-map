package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.utils.StringFormat;

@Service
@Transactional
public class LineService {

    private static final String LINE_DUPLICATION_EXCEPTION_MESSAGE = "이름이 중복되는 지하철 노선이 존재합니다.";
    private static final String NO_SUCH_LINE_EXCEPTION_MESSAGE = "해당 ID의 지하철 노선이 존재하지 않습니다.";

    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public LineResponse save(LineRequest lineRequest) {
        if (isDuplicateName(lineRequest)) {
            throw new IllegalArgumentException(
                    StringFormat.errorMessage(lineRequest.getName(), LINE_DUPLICATION_EXCEPTION_MESSAGE));
        }

        Line newLine = lineDao.save(lineRequest.toEntity());
        Section savedSection = sectionService.save(new SectionRequest(newLine.getId(), lineRequest));
        return LineResponse.of(newLine, List.of(savedSection.getUpStation(), savedSection.getDownStation()));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<LineResponse> lineResponses = new ArrayList<>();
        List<Line> lines = lineDao.findAll();

        for (Line line : lines) {
            Sections sections = sectionService.findAllByLineId(line.getId());
            lineResponses.add(LineResponse.of(line, sections.getSortedStations()));
        }

        return lineResponses;
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line foundLine = findExistLineById(id);
        Sections sections = sectionService.findAllByLineId(foundLine.getId());

        return LineResponse.of(foundLine, sections.getSortedStations());
    }

    public void update(Long id, LineRequest lineRequest) {
        Line foundLine = findExistLineById(id);
        if (isDuplicateName(lineRequest) && !foundLine.isSameName(lineRequest.getName())) {
            throw new IllegalArgumentException(
                    StringFormat.errorMessage(lineRequest.getName(), LINE_DUPLICATION_EXCEPTION_MESSAGE));
        }

        lineDao.update(foundLine.getId(), lineRequest.toEntity());
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findExistLineById(lineId);

        Sections origin = sectionService.findAllByLineId(line.getId());
        Section newSection = sectionService.makeSectionByRequest(sectionRequest);
        Sections resultSections = origin.copy();

        resultSections.add(newSection);
        sectionService.deleteAndSaveSections(lineId, origin, resultSections);
    }

    public void delete(Long id) {
        lineDao.delete(findExistLineById(id));
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findExistLineById(lineId);
        Station toDeleteStation = stationService.getById(stationId);

        Sections origin = sectionService.findAllByLineId(line.getId());
        Sections results = origin.copy();
        results.delete(toDeleteStation);

        sectionService.deleteAndSaveSections(lineId, origin, results);
    }

    private Line findExistLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        StringFormat.errorMessage(id, NO_SUCH_LINE_EXCEPTION_MESSAGE)));
    }

    private boolean isDuplicateName(LineRequest request) {
        return lineDao.findByName(request.getName()).isPresent();
    }
}
