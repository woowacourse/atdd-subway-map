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
      return request(`/lines`, METHOD.POST(data));
    },
    createLineStation(id, data) {
      return fetch(`/lines/${id}/stations/`, {
        method: 'POST',
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      });
    },
    update(id, data) {
      return request(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return fetch(`lines/${id}`, {
        method: 'DELETE'
      })},
    deleteLineStation(lindId, stationId) {
      return fetch(`/lines/${lindId}/stations/${stationId}`, {
        method: 'DELETE'
      })
    }
  }

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(data) {
      request(`/station/${data.id}`, METHOD.PUT(data));
    },
    delete(id) {
      return fetch(`stations/${id}`, {
        method: 'DELETE'
      });
    }
  };

  return {
    station,
    line
  };
})();

export default api;
