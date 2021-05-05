package wooteco.subway.name;

public interface Name {
    String name();

    boolean sameName(final String name);

    Name changeName(String name);
}
