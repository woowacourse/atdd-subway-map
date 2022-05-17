# Subway Map V2 API Document

## 지하철역

### 지하철역 등록

<details>
<summary>HTTP request</summary>

```
POST /stations HTTP/1.1
Content-Type: application/json
Accept: application/json
Content-Length: 35
Host: localhost:8080

{
  "name" : "지하철역이름"
}

```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 201 Created
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Location: /stations/1
Content-Type: application/json
Content-Length: 47

{
  "id" : 1,
  "name" : "지하철역이름"
}
```
</details>


### 지하철역 목록

<details>
<summary>HTTP request</summary>


```
GET /stations HTTP/1.1
Accept: application/json
Host: localhost:8080
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 167

[ {
  "id" : 1,
  "name" : "지하철역이름"
}, {
  "id" : 2,
  "name" : "새로운지하철역이름"
}, {
  "id" : 3,
  "name" : "또다른지하철역이름"
} ]
```
</details>


### 지하철역 삭제

<details>
<summary>HTTP request</summary>


```
DELETE /stations/1 HTTP/1.1
Host: localhost:8080
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```
</details>

---

## 지하철 노선

### 지하철 노선 등록

<details>
<summary>HTTP request</summary>


```
POST /lines HTTP/1.1
Content-Type: application/json
Accept: application/json
Content-Length: 118
Host: localhost:8080

{
  "name" : "신분당선",
  "color" : "bg-red-600",
  "upStationId" : 1,
  "downStationId" : 2,
  "distance" : 10
}
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 201 Created
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Location: /lines/1
Content-Type: application/json
Content-Length: 193

{
  "id" : 1,
  "name" : "신분당선",
  "color" : "bg-red-600",
  "stations" : [ {
    "id" : 1,
    "name" : "지하철역"
  }, {
    "id" : 2,
    "name" : "새로운지하철역"
  } ]
}
```
</details>


### 지하철 노선 목록

<details>
<summary>HTTP request</summary>


```
GET /lines HTTP/1.1
Accept: application/json
Host: localhost:8080
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 391

[ {
  "id" : 1,
  "name" : "신분당선",
  "color" : "bg-red-600",
  "stations" : [ {
    "id" : 1,
    "name" : "지하철역"
  }, {
    "id" : 2,
    "name" : "새로운지하철역"
  } ]
}, {
  "id" : 2,
  "name" : "분당선",
  "color" : "bg-green-600",
  "stations" : [ {
    "id" : 1,
    "name" : "지하철역"
  }, {
    "id" : 3,
    "name" : "또다른지하철역"
  } ]
} ]
```
</details>


### 지하철 노선 조회

<details>
<summary>HTTP request</summary>


```
GET /lines/1 HTTP/1.1
Accept: application/json
Host: localhost:8080
```
</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Content-Length: 193

{
  "id" : 1,
  "name" : "신분당선",
  "color" : "bg-red-600",
  "stations" : [ {
    "id" : 1,
    "name" : "지하철역"
  }, {
    "id" : 2,
    "name" : "새로운지하철역"
  } ]
}
```
</details>


### 지하철 노선 수정

<details>
<summary>HTTP request</summary>


```
PUT /lines/1 HTTP/1.1
Content-Type: application/json
Content-Length: 58
Host: localhost:8080

{
  "name" : "다른분당선",
  "color" : "bg-red-600"
}
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```
</details>


### 지하철 노선 삭제

<details>
<summary>HTTP request</summary>


```
DELETE /lines/1 HTTP/1.1
Host: localhost:8080
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 204 No Content
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```
</details>


---

## 구간

### 구간 등록

<details>
<summary>HTTP request</summary>


```
POST /lines/1/sections HTTP/1.1
Content-Type: application/json
Content-Length: 65
Host: localhost:8080

{
  "upStationId" : 1,
  "downStationId" : 2,
  "distance" : 10
}
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```
</details>


### 구간 제거

<details>
<summary>HTTP request</summary>


```
DELETE /lines/1/sections?stationId=2 HTTP/1.1
Host: localhost:8080
```

</details>

<details>
<summary>HTTP response</summary>

```
HTTP/1.1 200 OK
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
```
</details>
