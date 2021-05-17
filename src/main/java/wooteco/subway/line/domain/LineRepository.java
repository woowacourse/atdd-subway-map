package wooteco.subway.line.domain;

public interface LineRepository {
    Line save(Line line);

    Lines findAll();

    Line findById(Long id);

    boolean hasLine(String name);

    void update(Line line);

    void deleteById(Long id);

    void addSection(Long id, Section section);

    void deleteSection(Long id, Section section);
}
