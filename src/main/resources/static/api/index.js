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
  const request = (uri, config) => fetch(uri, config)
    .then(res => {
      if (!res.ok) {
        throw res.json();
      }
      return res;
    })
    .then(data => data.json())
    .catch(e => e.then(data => alert(data.message)));
  const requestNoResponse = (uri, config) => fetch(uri, config)
    .then(res => {
      if (!res.ok) {
        throw res.json();
      }
      return res;
    })
    .catch(e => e.then(data => alert(data.message)));

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(data, id) {
      return requestNoResponse(`/stations/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return requestNoResponse(`/stations/${id}`, METHOD.DELETE());
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
      return requestNoResponse(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return requestNoResponse(`/lines/${id}`, METHOD.DELETE());
    }
  };
  const lineStation = {
    get(lineId) {
      return request(`/lines/${lineId}/stations`);
    },
    create(data, lineId) {
      return requestNoResponse(`/lines/${lineId}/stations`, METHOD.POST(data));
    },
    delete(lineId, stationId) {
      return requestNoResponse(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    }
  };
  return {
    station,
    line,
    lineStation
  };
})();
export default api;