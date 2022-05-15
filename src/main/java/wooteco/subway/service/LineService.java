package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionEntity;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.BadRequestException;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private static final int BEFORE_SECTION = 0;
    private static final int AFTER_SECTION = 1;
    private static final int NO_NEED_MERGE = 1;
    private static final int TARGET_SECTION_INDEX = 0;
    private static final int MARGE_SECTION_INDEX = 0;
    private static final int DELETE_SECTION_UP_SECTION_INDEX = 1;
    private static final int DELETE_SECTION_DOWN_SECTION_INDEX = 2;

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicateNameAndColor(line.getName(), line.getColor());

        Station upStation = findStation(lineRequest.getUpStationId(), "조회하려는 상행역이 없습니다.");
        Station downStation = findStation(lineRequest.getDownStationId(), ("조회하려는 하행역이 없습니다."));

        Line savedLine = lineDao.save(line);
        sectionDao.save(new Section(savedLine, upStation, downStation, lineRequest.getDistance()));
        return LineResponse.of(savedLine, List.of(upStation, downStation));
    }

    public LineResponse showById(Long lineId) {
        return LineResponse.of(findLine(lineId), getStations(lineId));
    }

    public List<LineResponse> showAll() {
        return lineDao.findAll().stream()
                .map(line -> LineResponse.of(line, getStations(line.getId())))
                .collect(Collectors.toList());
    }

    public void updateById(Long id, LineUpdateRequest request) {
        validateDuplicateNameAndColor(request.getName(), request.getColor());
        Line line = findLine(id);
        line.update(request.getName(), request.getColor());
        lineDao.modifyById(id, line);
    }

    public void removeById(Long id) {
        lineDao.deleteById(id);
    }

    public void createSection(Long lineId, SectionRequest request) {
        Line line = findLine(lineId);
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        Station upStation = findStation(request.getUpStationId(), "출발지로 요청한 역이 없습니다.");
        Station downStation = findStation(request.getDownStationId(), "도착지로 요청한 역이 없습니다.");
        int distance = request.getDistance();
        Section wait = new Section(line, upStation, downStation, distance);

        validate(sections, upStation, downStation);
        List<Section> result = sections.splitSection(upStation, downStation, wait);

        updateSection(wait, result);
    }

    public void delete(Long lineId, Long stationId) {
        Station station = findStation(stationId, "삭제 요청한 역이 없습니다.");

        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        List<Section> result = sections.margeSection(station, findLine(lineId));

        mergeAndDelete(result);
    }

    private void updateSection(Section wait, List<Section> result) {
        if (!result.contains(wait)) {
            sectionDao.update(result.get(BEFORE_SECTION));
            sectionDao.save(result.get(AFTER_SECTION));
            return;
        }
        sectionDao.save(wait);
    }

    private void mergeAndDelete(List<Section> result) {
        if (result.size() == NO_NEED_MERGE) {
            sectionDao.deleteById(result.get(TARGET_SECTION_INDEX).getId());
            return;
        }
        sectionDao.save(result.get(MARGE_SECTION_INDEX));
        sectionDao.deleteById(result.get(DELETE_SECTION_UP_SECTION_INDEX).getId());
        sectionDao.deleteById(result.get(DELETE_SECTION_DOWN_SECTION_INDEX).getId());
    }

    private List<Section> toSections(List<SectionEntity> entities) {
        return entities.stream()
                .map(entity -> new Section(entity.getId(), findLine(entity.getLineId()),
                        findStation(entity.getUpStationId(), "출발지로 요청한 역이 없습니다."),
                        findStation(entity.getDownStationId(), "도착지로 요청한 역이 없습니다."),
                        entity.getDistance()))
                .collect(Collectors.toList());
    }

    private void validate(Sections sections, Station upStation, Station downStation) {
        sections.validateHasSameSection(upStation, downStation);
        sections.hasStationFrontOrBack(upStation, downStation);
        sections.validateContainsStation(upStation, downStation);
    }

    private void validateDuplicateNameAndColor(String name, String color) {
        if (lineDao.existByNameAndColor(name, color)) {
            throw new BadRequestException("노선의 이름과 색상은 중복될 수 없습니다.");
        }
    }

    private Line findLine(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NotFoundException("조회하려는 id가 존재하지 않습니다."));
    }

    private Station findStation(Long stationId, String errorMessage) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(errorMessage));
    }

    private List<Station> getStations(Long lineId) {
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        return sections.getStations();
    }
}

