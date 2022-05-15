package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final int DELETE_FAIL = 0;

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Long save(LineRequest request) {
        validateDuplicateName(request.getName());
        validateDuplicateSections(request);
        final Line line = new Line(request.getName(), request.getColor());
        final Long lineId = lineDao.save(line);

        final Station upStation = stationDao.findById(request.getUpStationId());
        final Station downStation = stationDao.findById(request.getDownStationId());

        final Section section = new Section(lineId, upStation, downStation, request.getDistance());
        sectionDao.save(section);

        return lineId;
    }

    private void validateDuplicateName(String name) {
        final boolean isExist = lineDao.findAll().stream()
                .anyMatch(line -> line.getName().equals(name));
        if (isExist) {
            throw new IllegalArgumentException("중복된 지하철 노선이 존재합니다.");
        }
    }

    private void validateDuplicateSections(LineRequest request) {
        if (request.getUpStationId().equals(request.getDownStationId())) {
            throw new IllegalArgumentException("상행과 하행 종점이 동일합니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(),
                        line.getName(),
                        line.getColor(),
                        sortSections(line.getSections())))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        Sections sections = new Sections(sectionDao.findByLineId(id));
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                sortSections(sections));
    }

    private List<StationResponse> sortSections(Sections sections) {
        List<StationResponse> stationResponses = new ArrayList<>();
        Station firstStation = sections.findFirstStation();
        stationResponses.add(new StationResponse(firstStation.getId(), firstStation.getName()));
        while (sections.nextStation(firstStation).isPresent()) {
            firstStation = sections.nextStation(firstStation).get();
            stationResponses.add(new StationResponse(firstStation.getId(), firstStation.getName()));
        }

        return stationResponses;
    }

    public Long updateByLine(Long id, LineRequest request) {
        final Line updateLine = new Line(id, request.getName(), request.getColor());

        return lineDao.updateByLine(updateLine);
    }

    public void deleteById(Long id) {
        final int isDeleted = lineDao.deleteById(id);

        if (isDeleted == DELETE_FAIL) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }
}
