package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.StringFormat;

@Service
public class LineService {

    private static final String LINE_DUPLICATION_EXCEPTION_MESSAGE = "이름이 중복되는 지하철 노선이 존재합니다.";
    private static final String NO_SUCH_LINE_EXCEPTION_MESSAGE = "해당 ID의 지하철 노선이 존재하지 않습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        if (isDuplicateName(lineRequest.getName())) {
            throw new IllegalArgumentException(
                    StringFormat.errorMessage(lineRequest.getName(), LINE_DUPLICATION_EXCEPTION_MESSAGE));
        }
        Line newLine = lineDao.save(lineRequest.toEntity());
        Station up = findExistStationById(lineRequest.getUpStationId());
        Station down = stationDao.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));

        sectionDao.save(newLine.getId(), new Section(up, down, lineRequest.getDistance()));

        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(),
                List.of(new StationResponse(up), new StationResponse(down)));
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line findLine = findExistLineById(id);
        List<Section> findSections = sectionDao.findAllByLineId(findLine.getId());
        Sections sections = new Sections(findSections);

        List<Station> sortedStations = sections.getSortedStations();
        return LineResponse.of(findLine, sortedStations);
    }

    public Void update(Long id, LineRequest lineRequest) {
        Line findLine = findExistLineById(id);
        if (isDuplicateName(lineRequest.getName()) && !findLine.isSameName(lineRequest.getName())) {
            throw new IllegalArgumentException(
                    StringFormat.errorMessage(lineRequest.getName(), LINE_DUPLICATION_EXCEPTION_MESSAGE));
        }
        lineDao.update(findLine.getId(), lineRequest.toEntity());
        return null;
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findExistLineById(lineId);
        List<Section> findSections = sectionDao.findAllByLineId(line.getId());
        Sections origin = new Sections(findSections);

        Station up = findExistStationById(sectionRequest.getUpStationId());
        Station down = findExistStationById(sectionRequest.getDownStationId());
        Section newSection = new Section(up, down, sectionRequest.getDistance());

        Sections resultSections = new Sections(findSections);
        resultSections.insert(newSection);
        deleteAndSaveSections(lineId, origin, resultSections);
    }

    private void deleteAndSaveSections(Long line_id, Sections origin, Sections resultSections) {
        List<Section> createdSections = resultSections.getDifferentList(origin);
        List<Section> toDeleteSections = origin.getDifferentList(resultSections);

        for (Section deleteTargetSection : toDeleteSections) {
            sectionDao.remove(deleteTargetSection);
        }
        for (Section createdSection : createdSections) {
            sectionDao.save(line_id, createdSection);
        }
    }

    public Void delete(Long id) {
        lineDao.delete(findExistLineById(id));
        return null;
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findExistLineById(lineId);
        Station stationToDelete = findExistStationById(stationId);
        List<Section> savedSections = sectionDao.findAllByLineId(line.getId());

        Sections origin = new Sections(savedSections);
        Sections results = new Sections(savedSections);
        results.delete(stationToDelete);

        deleteAndSaveSections(lineId, origin, results);
    }

    private Line findExistLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        StringFormat.errorMessage(id, NO_SUCH_LINE_EXCEPTION_MESSAGE)));
    }

    private Station findExistStationById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }

    private boolean isDuplicateName(String name) {
        return lineDao.findByName(name).isPresent();
    }
}
