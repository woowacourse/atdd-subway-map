package wooteco.subway.application;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineSaveRequest;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NoSuchLineException;
import wooteco.subway.exception.NoSuchStationException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionService sectionService;

    public LineService(final StationDao stationDao, final LineDao lineDao, final SectionService sectionService) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public LineResponse createLine(final LineSaveRequest request) {
        if (lineDao.existByName(request.getName()) || lineDao.existByColor(request.getColor())) {
            throw new DuplicateLineException();
        }
        Line createdLine = lineDao.save(request.toLine());
        Section createdSection = sectionService.createSection(createdLine.getId(), request.getUpStationId(), request.getDownStationId(), request.getDistance());
        createdLine.addSection(createdSection);
        return LineResponse.from(createdLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        Line findLine = findLineAndInitializeSections(id);
        return LineResponse.from(findLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<LineResponse> result = new ArrayList<>();
        List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            List<Section> sectionsByLine = sectionService.findSectionsByLineId(line.getId());
            line.addAllSections(sectionsByLine);
            result.add(LineResponse.from(line));
        }
        return result;
    }

    public void updateLine(final Long id, final LineUpdateRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void deleteLineById(final Long id) {
        sectionService.deleteAllSectionsRelevantToLine(id);
        lineDao.deleteById(id);
    }

    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Line line = findLineAndInitializeSections(lineId);

        Section section = sectionService.createSection(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());
        line.addSection(section);
        sectionService.updateSections(lineId, line.getSections());
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = findLineAndInitializeSections(lineId);
        Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new NoSuchStationException(stationId));
        Long removedSectionId = line.removeStation(station);
        sectionService.deleteSection(lineId, removedSectionId, line.getSections());
    }

    private Line findLineAndInitializeSections(final Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new NoSuchLineException(id));
        List<Section> sections = sectionService.findSectionsByLineId(id);
        line.addAllSections(sections);
        return line;
    }
}
