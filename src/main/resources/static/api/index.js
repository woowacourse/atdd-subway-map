const BASE_URL = "localhost:8080";

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
      method: "DELETE",
    };
  },
  POST(data) {
    return {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data),
    };
  }
};

const api = (() => {
  const request = (uri, config) => fetch(uri, config);

  const station = {
    getAll() {
      return request(`/stations`);
    },
    getById(id) {
      return request(`/stations/${id}`);
    },
    create(stationRequest) {
      return request(`/station`, METHOD.POST(stationRequest));
    },
    update(stationRequest, id) {
      return request(`/station/${id}`, METHOD.PUT(stationRequest));
    },
    delete(id) {
      return request(`/station/${id}`, METHOD.DELETE);
    }
  };

  const line = {
    getAll() {
      return request(`/lines`);
    },
    getById(id) {
      return request(`/lines/${id}`);
    },
    create(lineRequest) {
      return request(`/lines`, METHOD.POST(lineRequest));
    },
    update(lineRequest, id) {
      return request(`/lines/${id}`, METHOD.PUT(lineRequest));
    },
    delete(id) {
      return request(`/lines/${id}`, METHOD.DELETE());
    }
  };

  return {
    station,
    line,
  };
})();

export default api;
