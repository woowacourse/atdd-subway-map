const BASE_URL = "http://localhost:8080";

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

const api = (() => {
  const dataRequest = (uri, config) => fetch(uri, config).then(data => data.json());
  const noDataRequest = (uri, config) => fetch(uri, config);

  const station = {
    create(data) {
      return noDataRequest(`${BASE_URL}/stations`, METHOD.POST(data));
    },
    show() {
      return dataRequest(`${BASE_URL}/stations`);
    },
    find(id) {
      return dataRequest(`${BASE_URL}/stations/${id}`);
    },
    update(id, data) {
      return noDataRequest(`${BASE_URL}/stations/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return noDataRequest(`${BASE_URL}/stations/${id}`, METHOD.DELETE());
    }
  };

  const lines = {
    create(data) {
      return noDataRequest(`${BASE_URL}/lines`, METHOD.POST(data));
    },
    createLineStation(id, data) {
      return noDataRequest(`${BASE_URL}/lines/${id}/stations`, METHOD.POST(data));
    },
    show() {
      return dataRequest(`${BASE_URL}/lines`);
    },
    find(id) {
      return dataRequest(`${BASE_URL}/lines/${id}`);
    },
    update(id, data) {
      return noDataRequest(`${BASE_URL}/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return noDataRequest(`${BASE_URL}/lines/${id}`, METHOD.DELETE());
    },
    deleteLineStation(lineId, stationId) {
      return noDataRequest(`${BASE_URL}/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    }
  };

  return {
    lines,
    station
  };
})();

export default api;
