// const BASE_URL = "http://localhost:8080";

const METHOD = {
  PUT() {
    return {
      method: "PUT"
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
    getByName(name) {
      return request(`/lines/v2/${name}`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data));
    },
    update(data) {
      request(`/line/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      request(`/line/${id}`, METHOD.DELETE);
    }
  }

  const station = {
    get() {
      request(`/stations`);
    },
    create(data) {
      request(`/station`, METHOD.POST(data));
    },
    update(data) {
      request(`/station/${data.id}`, METHOD.PUT(data));
    },
    delete(id) {
      request(`/station/${id}`, METHOD.DELETE);
    }
  };

  return {
    station,
    line
  };
})();

export default api;
