package wooteco.subway.service;

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
import wooteco.subway.dto.SectionEntity;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.SectionNotFoundException;
import wooteco.subway.exception.StationNotFoundException;

@Service
public class LineService {

    private static final int NOT_FOUND = 0;
    private static final int CONNECT_SIZE = 2;

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse createLine(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Long lineId = lineDao.save(line);
        final Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        final Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(StationNotFoundException::new);

        sectionDao.save(new Section(lineId, upStation, downStation, request.getDistance()));

        return LineResponse.of(new Line(lineId, line.getName(), line.getColor()), List.of(upStation, downStation));
    }

    @Transactional
    public void createSection(final Long lineId, final SectionRequest sectionRequest) {
        final Sections sections = new Sections(toSections(sectionDao.findAllByLineId(lineId)));
        final Section newSection = createNewSection(lineId, sectionRequest);
        saveAndUpdateSection(sections, newSection);
    }

    private void saveAndUpdateSection(final Sections sections, final Section newSection) {
        final Long savedId = sectionDao.save(newSection);
        sections.add(new Section(savedId, newSection));
        sectionDao.updateAll(sections.getSections());
    }

    private List<Section> toSections(final List<SectionEntity> sectionEntities) {
        return sectionEntities.stream()
                .map(entity -> new Section(
                        entity.getId(),
                        entity.getLineId(),
                        stationDao.findById(entity.getUpStationId())
                                .orElseThrow(StationNotFoundException::new),
                        stationDao.findById(entity.getDownStationId())
                                .orElseThrow(StationNotFoundException::new),
                        entity.getDistance()
                ))
                .collect(Collectors.toList());
    }

    private Section createNewSection(Long lineId, SectionRequest sectionRequest) {
        final Station upStation = stationDao.findById(sectionRequest.getUpStationId())
                .orElseThrow(StationNotFoundException::new);
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId())
                .orElseThrow(StationNotFoundException::new);
        final int distance = sectionRequest.getDistance();
        return new Section(lineId, upStation, downStation, distance);
    }

    public List<LineResponse> showLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, findStations(line.getId())))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(final long id) {
        final Line line = lineDao.findById(id)
                .orElseThrow(LineNotFoundException::new);
        return LineResponse.of(line, findStations(line.getId()));
    }

    private List<Station> findStations(final Long lineId) {
        final Sections sections = new Sections(toSections(sectionDao.findAllByLineId(lineId)));
        return sections.sortByStation();
    }

    @Transactional
    public void updateLine(final long id, final LineRequest request) {
        if (lineDao.update(id, request.getName(), request.getColor()) == NOT_FOUND) {
            throw new LineNotFoundException();
        }
    }

    @Transactional
    public void deleteAllSectionByLineId(final Long id) {
        if (sectionDao.deleteByLineId(id) == NOT_FOUND) {
            throw new SectionNotFoundException();
        }
    }

    @Transactional
    public void deleteLine(final long id) {
        if (lineDao.delete(id) == NOT_FOUND) {
            throw new LineNotFoundException();
        }
    }

    @Transactional
    public void deleteSection(long lineId, long stationId) {
        final Station deleteStation = stationDao.findById(stationId)
                .orElseThrow(StationNotFoundException::new);
        final Sections sections = new Sections(toSections(sectionDao.findAllByLineId(lineId)));

        final List<Section> deletedSections = sections.delete(deleteStation);
        for (final Section section : deletedSections) {
            sectionDao.delete(section.getId());
        }

        if (isPossibleToConnect(deletedSections)) {
            final Section newSection = connectNewSection(deletedSections);
            saveAndUpdateSection(sections, newSection);
        }
    }

    private boolean isPossibleToConnect(List<Section> deletedSections) {
        return deletedSections.size() == CONNECT_SIZE;
    }

    private Section connectNewSection(List<Section> deletedSections) {
        final Section firstSection = deletedSections.get(0);
        final Section secondSection = deletedSections.get(1);
        return firstSection.connect(secondSection);
    }
}
