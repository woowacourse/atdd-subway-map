package wooteco.subway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NoSuchLineException;
import wooteco.subway.exception.section.NoSuchSectionException;
import wooteco.subway.exception.station.NoSuchStationException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(final LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Line savedLine = lineDao.insert(line)
                .orElseThrow(DuplicateLineException::new);

        final Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(NoSuchStationException::new);
        final Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(NoSuchStationException::new);

        final Section section = new Section(
                savedLine.getId(),
                upStation.getId(),
                downStation.getId(),
                request.getDistance());
        sectionDao.insert(section);

        return LineResponse.of(savedLine, List.of(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> {
                    final List<Station> stations = stationDao.findAllByLineId(line.getId());
                    return LineResponse.of(line, stations);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long id) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);

        final List<Station> stations = findSortedStationsByLineId(line.getId());

        return LineResponse.of(line, stations);
    }

    private List<Station> findSortedStationsByLineId(final Long lineId) {
        final List<Section> sections = sectionDao.findAllByLineId(lineId);

        final List<Long> endStationIds = findEnsStationIds(sections);
        Long upStationId = findEndUpStation(sections, endStationIds);

        final List<Long> sortedStationIds = new ArrayList<>();
        sortedStationIds.add(upStationId);
        while (sortedStationIds.size() != sections.size() + 1) {
            final Section section = findSectionByUpStationId(upStationId, sections);
            upStationId = section.getDownStationId();
            sortedStationIds.add(upStationId);
        }
        return toStations(sortedStationIds);
    }

    private List<Long> findEnsStationIds(final List<Section> sections) {
        return toCountByStationId(sections)
                .entrySet()
                .stream()
                .filter(it -> it.getValue().equals(1))
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> toCountByStationId(final List<Section> sections) {
        final Map<Long, Integer> countByStationId = new HashMap<>();
        for (Section section : sections) {
            final Long upStationId = section.getUpStationId();
            countByStationId.put(upStationId, countByStationId.getOrDefault(upStationId, 0) + 1);

            final Long downStationId = section.getDownStationId();
            countByStationId.put(downStationId, countByStationId.getOrDefault(downStationId, 0) + 1);
        }
        return countByStationId;
    }

    private Long findEndUpStation(final List<Section> sections, final List<Long> endStationIds) {
        return sections
                .stream()
                .map(Section::getUpStationId)
                .filter(endStationIds::contains)
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }

    private Section findSectionByUpStationId(final Long upStationId, final List<Section> sections) {
        return sections
                .stream()
                .filter(it -> it.getUpStationId().equals(upStationId))
                .findFirst()
                .orElseThrow(NoSuchSectionException::new);
    }

    private List<Station> toStations(final List<Long> stationIds) {
        return stationIds
                .stream()
                .map(it -> stationDao.findById(it).orElseThrow(NoSuchStationException::new))
                .collect(Collectors.toList());
    }

    public void updateById(final Long id, final LineRequest request) {
        final Line line = lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new);
        line.updateName(request.getName());
        line.updateColor(request.getColor());
        lineDao.updateById(id, line)
                .orElseThrow(DuplicateLineException::new);
    }

    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
