const METHOD = {
  PUT(data) {
    return {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "charset": "UTF-8"
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
    getByName(name) {
      return request(`/stations/id/${name}`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(data, id) {
      return fetch(`/stations/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return fetch(`/stations/${id}`, METHOD.DELETE());
    }
  };
  const line = {
    get() {
      return request(`/lines`);
    },
    getBy(id) {
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
  const lineStation = {
    create(data, id) {
      return request(`/lines/${id}/stations`, METHOD.POST(data));
    }
  };
  return {
    station, line, lineStation
  };
})();
export default api;