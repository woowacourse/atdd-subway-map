const api = (() => {
  const request = (uri, config) => fetch(uri, config);

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
      return request(`/stations/${id}`, METHOD.DELETE());
    }
  };

  const line = {
    get() {
      return request(`/lines`);
    },
    getDetail(id) {
      return request(`/lines/${id}`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data));
    },
    update(data, id) {
      return request(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return request(`/lines/${id}`, METHOD.DELETE());
    }
  };

  return {
    station, line
  };

})();
export default api;