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
import wooteco.subway.repository.StationRepository;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final StationRepository stationRepository;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao,
        StationRepository stationRepository) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.stationRepository = stationRepository;
    }

    public LineResponse create(LineRequest lineRequest) {
        validateDuplicateNameAndColor(lineRequest.getName(), lineRequest.getColor());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());

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
        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());
        int distance = request.getDistance();
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        Section newSection = new Section(line, upStation, downStation, distance);

        for (Section updateSection : sections.findUpdateSections(newSection)) {
            sectionDao.save(updateSection);
        }
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        Station station = findStation(stationId);
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));

        List<Section> removedSections = sections.findDeleteSections(line, station);
        for (Section removedSection : removedSections) {
            sectionDao.deleteById(removedSection.getId());
        }

        if (removedSections.size() == Sections.COMBINE_SIZE) {
            Section combineSection = sections.combine(line, removedSections);
            sectionDao.save(combineSection);
        }
    }

    private List<Section> toSections(List<SectionEntity> entities) {
        return entities.stream()
            .map(entity -> new Section(
                entity.getId(),
                findLine(entity.getLineId()),
                findStation(entity.getUpStationId()),
                findStation(entity.getDownStationId()),
                entity.getDistance()))
            .collect(Collectors.toList());
    }

    private void validateDuplicateNameAndColor(String name, String color) {
        if (lineDao.existByNameAndColor(name, color)) {
            throw new BadRequestException("노선이 이름과 색상은 중복될 수 없습니다.");
        }
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId);
    }

    private Line findLine(Long lineId) {
        return lineDao.findById(lineId)
            .orElseThrow(() -> new NotFoundException("조회하려는 id가 존재하지 않습니다. id : " + lineId));
    }

    private List<Station> getStations(Long lineId) {
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        return sections.getStations();
    }
}

