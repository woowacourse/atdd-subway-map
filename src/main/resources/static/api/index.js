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
  const request = (uri, config) => fetch(uri, config)
      .then((data) => {
        if (data.status === 204) {
          return null;
        }
        return data.json();
      });

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
    get(lineId) {
      return request(`/lines/${lineId}/stations`);
    },
    delete(lineId, stationId) {
      return request(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    },
    create(lineId, data) {
      return request(`lines/${lineId}/stations/`, METHOD.POST(data));
    }
  }

  return {
    station, line, lineStation
  };
})();
export default api;
