const STATIONS_BASE_URI = "/stations";
const LINES_BASE_URI = "/lines";
const LINE_STATIONS_BASE_URI = "/lineStations";

const METHOD = {
  PUT(data) {
    return {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    };
  },
  DELETE() {
    return {
      method: "DELETE"
    };
  },
  POST(data) {
    return {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    };
  }
};

export const api = (() => {
  const request = (uri, config) => fetch(uri, config).then(data => data.json());
  const requestWithoutResponseBody = (uri, config) => fetch(uri, config);

  const station = {
    get() {
      return request(`${STATIONS_BASE_URI}`);
    },
    create(data) {
      return request(`${STATIONS_BASE_URI}`, METHOD.POST(data));
    },
    update(stationId, data) {
      return request(`${STATIONS_BASE_URI}/${stationId}`, METHOD.PUT(data));
    },
    delete(name) {
      return requestWithoutResponseBody(
          `${STATIONS_BASE_URI}/${name}`, METHOD.DELETE());
    }
  };

  const line = {
    getLines() {
      return request(`${LINES_BASE_URI}`);
    },
    getLine(lineId) {
      return request(`${LINES_BASE_URI}/${lineId}`)
    },
    create(data) {
      return request(`${LINES_BASE_URI}`, METHOD.POST(data));
    },
    update(lineId, data) {
      return requestWithoutResponseBody(
        `${LINES_BASE_URI}/${lineId}`, METHOD.PUT(data));
    },
    delete(lineId) {
      return requestWithoutResponseBody(
          `${LINES_BASE_URI}/${lineId}`, METHOD.DELETE());
    }
  };

  const edge = {
    getLinesWithStations() {
      return request(`${LINE_STATIONS_BASE_URI}`);
    },
    create(lineId, data) {
      return requestWithoutResponseBody(
        `${LINE_STATIONS_BASE_URI}/${lineId}`, METHOD.POST(data));
    },
    delete(lineId, stationId) {
      return requestWithoutResponseBody(
        `${LINES_BASE_URI}/${lineId}/${LINE_STATIONS_BASE_URI}/${stationId}`,
        METHOD.DELETE());
    }
  };

  return {
    station,
    line,
    edge
  };
})();
