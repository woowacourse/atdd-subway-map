<p align="center">
    <img src="./woowacourse.png" alt="우아한테크코스" width="250px">
</p>

# Level 2, 지하철 노선도

---

![Generic badge](https://img.shields.io/badge/Level2-subway_map-green.svg)
![Generic badge](https://img.shields.io/badge/test-32_passed-blue.svg)
![Generic badge](https://img.shields.io/badge/version-1.0.0-brightgreen.svg)

> 우아한테크코스 웹 백엔드 4기, 지하철 노선 저장소입니다.

<p align="center">
    <img src="./front.png" alt="front" width="500px">
</p>

<br>

# How to Start

---

### 1. Run SpringBootApplication

```
cd ..
./gradlew bootRun
```

<br>

### 2. Open in Browser

- [https://d2owgqwkhzq0my.cloudfront.net/](https://d2owgqwkhzq0my.cloudfront.net/)

<br><br>

# 목표

- 스프링 프레임워크가 제공하는 객체 관리 기능의 장점을 경험
- 애플리케이션 개발에 필요한 다양한 테스트를 작성하는 경험

<br><br>

# 도메인 안내

<p align="center">
    <img src="./domain.png" alt="front" width="500px">
</p>

### 지하철 역(station)

- 지하철 역 속성:
    - 이름(name)

### 지하철 구간(section)

- 지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보
- 지하철 구간 속성:
    - 길이(distance)

### 지하철 노선(line)

- 지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보
- 지하철 노선 속성:
    - 노선 이름(name)
    - 노선 색(color)

<br><br>

## API Document

- https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f

<br><br>

