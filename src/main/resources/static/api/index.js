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
  const request = (uri, config) => fetch(uri, config).then(data => data.json());

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    delete(id) {
      return fetch(`/stations/${id}`, METHOD.DELETE());
    }
  };

  const line = {
    get(id = "") {
      return request(`/lines/${id}`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data));
    },
    update(data, id) {
      return fetch(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return fetch(`/lines/${id}`, METHOD.DELETE());
    }
  };

  const edge = {
    get(id) {
      return !id ? request(`/lines/stations`) : request(`/lines/${id}/stations`);
    },
    create(data, id) {
      return fetch(`/lines/${id}/stations`, METHOD.POST(data));
    },
    delete(lineId, stationId) {
      return fetch(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    }
  };

  return {
    station,
    line,
    edge
  };
})();

export default api;
