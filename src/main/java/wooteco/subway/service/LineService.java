package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.reopository.Entity.SectionEntity;
import wooteco.subway.reopository.LineRepository;
import wooteco.subway.reopository.SectionRepository;
import wooteco.subway.reopository.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.BadRequestException;

@Service
public class LineService {

    private static final int BEFORE_SECTION = 0;
    private static final int AFTER_SECTION = 1;
    private static final int NO_NEED_MERGE = 1;
    private static final int TARGET_SECTION_INDEX = 0;
    private static final int MARGE_SECTION_INDEX = 0;
    private static final int DELETE_SECTION_UP_SECTION_INDEX = 1;
    private static final int DELETE_SECTION_DOWN_SECTION_INDEX = 2;
    public static final String NOT_FOUNT_ID_ERROR_MESSAGE = "조회하려는 id가 존재하지 않습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
                       SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicateNameAndColor(line.getName(), line.getColor());

        Station upStation = findStation(lineRequest.getUpStationId(), "조회하려는 상행역이 없습니다.");
        Station downStation = findStation(lineRequest.getDownStationId(), "조회하려는 하행역이 없습니다.");
        line = new Line(lineRepository.save(line), line.getName(), line.getColor());
        sectionRepository.save(new Section(line, upStation, downStation, lineRequest.getDistance()));
        return LineResponse.of(line, List.of(upStation, downStation));
    }

    public LineResponse showById(Long lineId) {
        return LineResponse.of(findLine(lineId), getStations(lineId));
    }

    public List<LineResponse> showAll() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line, getStations(line.getId())))
                .collect(Collectors.toList());
    }

    public void updateById(Long id, LineUpdateRequest request) {
        validateDuplicateNameAndColor(request.getName(), request.getColor());
        Line line = findLine(id);
        line.validateUpdate(request.getName(), request.getColor());
        lineRepository.modifyById(id, line);
    }

    public void removeById(Long id) {
        lineRepository.deleteById(id);
    }

    public void createSection(Long lineId, SectionRequest request) {
        Line line = findLine(lineId);
        Sections sections = new Sections(toSections(sectionRepository.findByLineId(lineId)));
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

        Sections sections = new Sections(toSections(sectionRepository.findByLineId(lineId)));
        List<Section> result = sections.margeSection(station, findLine(lineId));

        mergeAndDelete(result);
    }

    private void updateSection(Section wait, List<Section> result) {
        if (!result.contains(wait)) {
            sectionRepository.update(result.get(BEFORE_SECTION));
            sectionRepository.save(result.get(AFTER_SECTION));
            return;
        }
        sectionRepository.save(wait);
    }

    private void mergeAndDelete(List<Section> result) {
        if (result.size() == NO_NEED_MERGE) {
            sectionRepository.deleteById(result.get(TARGET_SECTION_INDEX).getId());
            return;
        }
        sectionRepository.save(result.get(MARGE_SECTION_INDEX));
        sectionRepository.deleteById(result.get(DELETE_SECTION_UP_SECTION_INDEX).getId());
        sectionRepository.deleteById(result.get(DELETE_SECTION_DOWN_SECTION_INDEX).getId());
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
        if (lineRepository.existByNameAndColor(name, color)) {
            throw new BadRequestException("노선의 이름과 색상은 중복될 수 없습니다.");
        }
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id, NOT_FOUNT_ID_ERROR_MESSAGE);
    }

    private Station findStation(Long stationId, String errorMessage) {
        return stationRepository.findById(stationId, errorMessage);
    }

    private List<Station> getStations(Long lineId) {
        Sections sections = new Sections(toSections(sectionRepository.findByLineId(lineId)));
        return sections.getStations();
    }
}

