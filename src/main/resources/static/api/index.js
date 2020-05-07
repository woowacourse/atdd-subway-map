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
      body: JSON.stringify({
        name: data.title,
        startTime: data.startTime,
        endTime: data.endTime,
        intervalTime: data.intervalTime,
        bgColor: data.bgColor
      })
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
      return request(`/station`, METHOD.POST(data));
    },
    update(data, id) {
      return request(`/station/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return request(`/station/${id}`, METHOD.DELETE());
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

  return {
    station,
    line
  };
})();

export default api;
