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
        Line savedLine = lineDao.save(line);

        Station upStation = stationDao.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new NotFoundException("조회하려는 상행역이 없습니다."));
        Station downStation = stationDao.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new NotFoundException("조회하려는 하행역이 없습니다."));

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

    private Line findLine(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new NotFoundException("조회하려는 id가 존재하지 않습니다."));
    }

    private List<Station> getStations(Long lineId) {
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        return sections.getStations();
    }

    private void validateDuplicateNameAndColor(String name, String color) {
        if (lineDao.existByNameAndColor(name, color)) {
            throw new BadRequestException("노선이 이름과 색상은 중복될 수 없습니다.");
        }
    }

    private List<Section> toSections(List<SectionEntity> entities) {
        return entities.stream()
            .map(entity -> new Section(
                entity.getId(),
                findLine(entity.getLineId()),
                stationDao.findById(entity.getUpStationId()).orElseThrow(),
                stationDao.findById(entity.getDownStationId()).orElseThrow(),
                entity.getDistance()))
            .collect(Collectors.toList());
    }

    public void createSection(Long lineId, SectionRequest request) {
        Line line = findLine(lineId);
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        Station upStation = stationDao.findById(request.getUpStationId()).orElseThrow();
        Station downStation = stationDao.findById(request.getDownStationId()).orElseThrow();
        int distance = request.getDistance();
        Section newSection = new Section(line, upStation, downStation, distance);

        List<Section> makeSections = sections.add(newSection);
        if (makeSections.size() == 1) {
            sectionDao.save(new Section(line, upStation, downStation, distance));
            return;
        }

        if (makeSections.size() == 2) {
            sectionDao.update(makeSections.get(BEFORE_SECTION));
            sectionDao.save(makeSections.get(AFTER_SECTION));
        }
    }

    public void delete(Long lineId, Long stationId) {
        Station station = stationDao.findById(stationId).orElseThrow();
        Sections sections = new Sections(toSections(sectionDao.findByLineId(lineId)));
        List<Section> removedSections = sections.deleteStation(station);
        if (removedSections.size() == 2) {
            Section upSection = sections.findContainsDownStation(station);
            Section downSection = sections.findContainsUpStation(station);
            sectionDao.save(new Section(
                findLine(lineId),
                upSection.getUpStation(),
                downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance())
            );
            sectionDao.deleteById(upSection.getId());
            sectionDao.deleteById(downSection.getId());
            return;
        }

        for (Section removedSection : removedSections) {
            sectionDao.deleteById(removedSection.getId());
        }
    }
}

