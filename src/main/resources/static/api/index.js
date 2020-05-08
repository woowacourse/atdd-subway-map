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
  const nonRequest = (uri, config) => fetch(uri, config);

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(data, id) {
      return request(`/stations/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return nonRequest(`/stations/${id}`, METHOD.DELETE());
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
      return request(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return nonRequest(`/lines/${id}`, METHOD.DELETE());
    }
  };

  return {
    station,
    line
  };
})();

export default api;
