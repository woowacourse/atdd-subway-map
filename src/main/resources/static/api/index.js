const BASE_URL = "localhost:8080";

const METHOD = {
  PUT(data) {
    return {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        content: data
      })
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
      body: JSON.stringify({
        content: data
      })
    };
  }
};

const api = (() => {
  const request = (uri, config) => fetch(uri, config).then(data => data.json()).catch(error => alert(error));
  const requestWithEmptyResponse = (uri, config) => fetch(uri, config).then().catch(error => alert(error));
  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(data, id) {
      return requestWithEmptyResponse(`/stations/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return requestWithEmptyResponse(`/stations/${id}`, METHOD.DELETE());
    }
  };

  const line = {
    get() {
      return request(`/lines`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data));
    },
    update(data, id) {
      return requestWithEmptyResponse(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return requestWithEmptyResponse(`/lines/${id}`, METHOD.DELETE());
    }
  };

  const edge = {
    get(lineId) {
      return request(`/lines/${lineId}/stations`);
    },
    create(lineId, data) {
      return request(`/lines/${lineId}/stations`, METHOD.POST(data));
    },
    update(lineId, data) {
      return requestWithEmptyResponse(`/lines/${lineId}/stations`, METHOD.PUT(data));
    },
    delete(lineId, stationId) {
      return requestWithEmptyResponse(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    }
  };
  return {
    station, line, edge
  };
})();
export default api;
