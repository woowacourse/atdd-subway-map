package wooteco.subway.name.domain;

public interface Name {
    String name();

    boolean sameName(final String name);

    Name changeName(String name);
}
