const method = {
  put(data) {
    return {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    };
  },
  delete() {
    return {
      method: "DELETE"
    };
  },
  post(data) {
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
      return request(`/stations`, method.post(data));
    },
    delete(id) {
      return fetch(`/stations/${id}`, method.delete());
    }
  };

  const line = {
    get() {
      return request(`/lines`);
    },
    create(data) {
      return request(`/lines`, method.post(data));
    },
    update(data, id) {
      return fetch(`/lines/${id}`, method.put(data));
    },
    delete(id) {
      return fetch(`/lines/${id}`, method.delete());
    }
  };

  const edge = {
    get(lineId) {
      return request(`/lines/${lineId}/stations`);
    },
    create(lineId, data) {
      return request(`/lines/${lineId}/stations`, method.post(data));
    },
    delete(lineId, stationId) {
      return requestWithEmptyResponse(`/lines/${lineId}/stations/${stationId}`, method.delete());
    }
  };
  return {
    station, line, edge
  };
})();
export default api;
