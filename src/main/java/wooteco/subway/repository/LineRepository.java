package wooteco.subway.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.service.SectionsDirtyChecking;

@Repository
public class LineRepository {

    private static final int ONE_SECTION = 1;

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final SectionsDirtyChecking dirtyChecking;

    public LineRepository(LineDao lineDao, SectionDao sectionDao,
        SectionsDirtyChecking dirtyChecking) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.dirtyChecking = dirtyChecking;
    }

    public Line saveLineWithSection(String name, String color, Long upStationId, Long downStationId,
        int distance) {
        Long createdLineId = lineDao.create(name, color);
        Long createdSectionId = sectionDao
            .create(createdLineId, upStationId, downStationId, distance);
        List<Section> sections = new ArrayList<>();
        Section section = new Section(createdSectionId, createdLineId, upStationId, downStationId,
            distance);
        sections.add(section);
        return new Line(createdLineId, name, color, sections);
    }

    public List<Line> findAllLine() {
        return lineDao.findAll();
    }

    public Line findLineWithSectionsById(Long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        Optional<Line> line = lineDao.findById(lineId);
        return new Line(line.get(), sections);
    }

    public int edit(Long lineId, String name, String color) {
        return lineDao.edit(lineId, name, color);
    }

    public int deleteLineWithSectionByLineId(Long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        for (Section section : sections) {
            sectionDao.deleteById(section.getId());
        }
        return lineDao.deleteById(lineId);
    }

    public void deleteSectionInLine(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        if (sections.size() == ONE_SECTION) {
            throw new SubwayException("구간이 하나인 노선에서는 역을 삭제할 수 없습니다.");
        }

        Optional<Section> upSectionOptional = sectionDao
            .findByDownStationIdAndLineId(stationId, lineId);
        Optional<Section> downSectionOptional = sectionDao
            .findByUpStationIdAndLineId(stationId, lineId);

        if (isEndStation(lineId, stationId)) {
            sectionDao.deleteById(upSectionOptional.get().getId());
            return;
        }
        if (isStartStation(lineId, stationId)) {
            sectionDao.deleteById(downSectionOptional.get().getId());
            return;
        }

        Section upSection = upSectionOptional.get();
        Section downSection = downSectionOptional.get();
        sectionDao.deleteById(upSection.getId());
        sectionDao.deleteById(downSection.getId());
        sectionDao.create(
            lineId,
            upSection.getUpStationId(),
            downSection.getDownStationId(),
            upSection.getDistance() + downSection.getDistance()
        );
    }

    private boolean isEndStation(Long lineId, Long stationId) {
        Optional<Section> downSectionOptional = sectionDao
            .findByUpStationIdAndLineId(stationId, lineId);
        return !downSectionOptional.isPresent();
    }

    private boolean isStartStation(Long lineId, Long stationId) {
        Optional<Section> upSectionOptional = sectionDao
            .findByDownStationIdAndLineId(stationId, lineId);
        return !upSectionOptional.isPresent();
    }
}
