package wooteco.subway.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.LineResponse;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Station upStation = stationDao.findById(lineRequest.getUpStationId());
        final Station downStation = stationDao.findById(lineRequest.getDownStationId());

        checkExistStation(upStation);
        checkExistStation(downStation);
        checkDuplicateLine(lineRequest);

        final Line savedLine = lineDao.save(line);
        final Section section = new Section(savedLine, upStation, downStation, lineRequest.getDistance());
        sectionDao.save(section);

        return LineResponse.of(savedLine, Arrays.asList(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineDao.findAll();

        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lines) {
            Sections sections = getSections(line);
            responses.add(LineResponse.of(line, sections.getStations()));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        checkExistLine(id);
        final Line line = lineDao.findById(id);
        final Sections sections = getSections(line);

        return LineResponse.of(line, sections.getStations());
    }

    public void updateLine(Long id, String name, String color) {
        checkExistLine(id);
        lineDao.updateById(id, name, color);
    }

    public void deleteLine(Long id) {
        checkExistLine(id);
        lineDao.deleteById(id);
    }

    public void saveSection(Long lineId, SectionRequest sectionRequest) {
        checkExistLine(lineId);

        final Line line = lineDao.findById(lineId);
        final Sections sections = getSections(line);

        final Station upStation = stationDao.findById(sectionRequest.getUpStationId());
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId());
        final int distance = sectionRequest.getDistance();

        checkSectionHasNotAnyStation(sections, upStation, downStation);
        checkSectionHasAllStation(sections, upStation, downStation);

        if (sections.hasSameUpStation(upStation)) {
            Section existSection = sectionDao.findByLineIdAndUpStationId(lineId, sectionRequest.getUpStationId());
            int existDistance = existSection.getDistance();
            Section updateSection = new Section(existSection.getId(), line, downStation,
                    existSection.getDownStation(), existDistance - distance);
            sectionDao.update(updateSection);
        }

        if (sections.hasSameDownStation(downStation)) {
            Section existSection = sectionDao.findByLineIdAndDownStationId(lineId, sectionRequest.getDownStationId());
            int existDistance = existSection.getDistance();
            Section updateSection = new Section(existSection.getId(), line, existSection.getUpStation(),
                    upStation, existDistance - distance);
            sectionDao.update(updateSection);
        }

        sectionDao.save(new Section(line, upStation, downStation, distance));
    }

    public void deleteSection(Long lineId, Long stationId) {
        checkExistLine(lineId);

        final Line line = lineDao.findById(lineId);
        final Station station = stationDao.findById(stationId);
        final Sections sections = getSections(line);
        checkOnlyOneSection(sections);
        checkNotContainStation(sections, station);

        //종점이 제거될 경우
        //1.상행종점
        if (sections.getLastUpStation().equals(station)) {
            Section existSection = sectionDao.findByLineIdAndUpStationId(lineId, station.getId());
            sectionDao.deleteById(existSection.getId());
            return;
        }

        //2.하행종점
        if (sections.getLastDownStation().equals(station)) {
            Section existSection = sectionDao.findByLineIdAndDownStationId(lineId, station.getId());
            sectionDao.deleteById(existSection.getId());
            return;
        }

        //중간역 제거
        if (sections.hasSameUpStation(station) && sections.hasSameDownStation(station)) {
            Section upSection = sectionDao.findByLineIdAndDownStationId(lineId, stationId);
            Section downSection = sectionDao.findByLineIdAndUpStationId(lineId, stationId);
            int upSectionDistance = upSection.getDistance();
            int downSectionDistance = downSection.getDistance();
            sectionDao.deleteById(downSection.getId());
            sectionDao.update(new Section(upSection.getId(), line, upSection.getUpStation(),
                    downSection.getDownStation(), upSectionDistance + downSectionDistance));
        }
    }

    private void checkExistLine(Long id) {
        final Line line = lineDao.findById(id);
        if (line == null) {
            throw new IllegalArgumentException("해당하는 노선이 존재하지 않습니다.");
        }
    }

    private void checkExistStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("해당하는 역이 존재하지 않습니다.");
        }
    }

    private void checkDuplicateLine(LineRequest lineRequest) {
        if (lineDao.hasLine(lineRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
    }

    private Sections getSections(Line line) {
        final List<Section> sections = sectionDao.findByLineId(line.getId());
        return new Sections(sections);
    }

    private void checkNotContainStation(Sections sections, Station station) {
        if (!sections.hasStation(station)) {
            throw new IllegalArgumentException("역이 노선에 등록되어 있지 않다면 삭제할 수 없습니다.");
        }
    }

    private void checkSectionHasNotAnyStation(Sections sections, Station upStation, Station downStation) {
        if (!sections.hasStation(upStation) && !sections.hasStation(downStation)) {
            throw new IllegalArgumentException("등록하려는 구간 중 하나 이상의 역은 무조건 노선에 등록되어 있어야 합니다.");
        }
    }

    private void checkSectionHasAllStation(Sections sections, Station upStation, Station downStation) {
        if (sections.hasSameUpStation(upStation) && sections.hasSameDownStation(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
        }
    }

    private void checkOnlyOneSection(Sections sections) {
        if (sections.hasOnlyOneSection()) {
            throw new IllegalArgumentException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");
        }
    }
}
