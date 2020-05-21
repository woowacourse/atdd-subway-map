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
  const request = async (uri, config) => await fetch(uri, config)
    .then(async data => {
      try {
        if (data.ok) {
          return data.json()
        }
        const error = await data.json();
        throw new Error(error.message);
      } catch (e) {
        alert(e.message);
        return;
      }
    });

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(id, data) {
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
    getBy(id) {
      return request(`/lines/${id}`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data));
    },
    update(id, data) {
      return request(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return request(`/lines/${id}`, METHOD.DELETE());
    }
  };

  const lineStation = {
    get() {
      return request(`/line-stations`);
    },
    create(lineId, data) {
      return request(`/lines/${lineId}/stations`, METHOD.POST(data));
    },
    update(id, data) {
      return request(`/line-stations/${id}`, METHOD.PUT(data));
    },
    delete(lineId, stationId) {
      return request(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    }
  };

  return {
    station,
    line,
    lineStation
  };
})();

export default api;
