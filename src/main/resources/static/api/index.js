const BASE_URL = "localhost:8080";

const METHOD = {
  PUT(data) {
    return {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        content: data
      })
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
      body: JSON.stringify({
        content: data
      })
    };
  }
};

const api = (() => {
  const request = (uri, config) => fetch(uri, config).then(data => data.json());
  const requestWithEmptyResponse = (uri, config) => fetch(uri, config).then();
  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(data, id) {
      return requestWithEmptyResponse(`/stations/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return requestWithEmptyResponse(`/stations/${id}`, METHOD.DELETE());
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
      return requestWithEmptyResponse(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return requestWithEmptyResponse(`/lines/${id}`, METHOD.DELETE());
    }
  };
  return {
    station, line
  };
})();
export default api;
