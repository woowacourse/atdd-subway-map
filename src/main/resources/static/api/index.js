const METHOD = {
  PUT(data) {
    return {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data),
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
  const deleteRequest = (uri, config) => fetch(uri, config);
  const request = (uri, config) => fetch(uri, config).then(data => data.json());

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(id, data) {
      return request(`/stations/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return deleteRequest(`/stations/${id}`, METHOD.DELETE());
    }
  };

  const line = {
    get() {
      return request(`/lines`);
    },
    findById(id) {
      return request(`/lines/${id}`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data));
    },
    update(id, data) {
      return request(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return deleteRequest(`/lines/${id}`, METHOD.DELETE());
    }
  };

  const edge = {
    create(id, data) {
      return request(`/edges/${id}`, METHOD.POST(data));
    },
    delete(lineId, stationId) {
      return deleteRequest(`/edges/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    }
  };

  return {
    station, line, edge
  };
})();

export default api;
