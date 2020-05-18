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
  const line = {
    get() {
      return request(`/lines`);
    },
    getById(id) {
      return request(`/lines/${id}`);
    },
    create(data) {
      return fetch(`/lines`, METHOD.POST(data));
    },
    createLineStation(id, data) {
      return fetch(`/lines/${id}/stations`,  METHOD.POST(data));
    },
    update(id, data) {
      return fetch(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return fetch(`lines/${id}`, METHOD.DELETE());
    },
    deleteLineStation(lindId, stationId) {
      return fetch(`/lines/${lindId}/stations/${stationId}`, METHOD.DELETE())
    }
  }

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return fetch(`/stations`, METHOD.POST(data));
    },
    update(data) {
      return fetch(`/station/${data.id}`, METHOD.PUT(data));
    },
    delete(id) {
      return fetch(`stations/${id}`, METHOD.DELETE())
    }
  };

  return {
    station,
    line
  };
})();

export default api;
