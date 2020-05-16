const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => data.json());

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
    update(data, stationId) {
      return request(`/stations/${stationId}`, METHOD.PUT(data));
    },
    delete(stationId) {
      return request(`/stations/${stationId}`, METHOD.DELETE());
    }
  };

  const line = {
    get() {
      return request(`/lines`);
    },
    getDetail(lienId) {
      return request(`/lines/${lienId}`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data));
    },
    update(data, lineId) {
      return request(`/lines/${lineId}`, METHOD.PUT(data));
    },
    delete(lineId) {
      return request(`/lines/${lineId}`, METHOD.DELETE());
    },
    createLineStation(data, lineId) {
      return request(`/line/${lineId}/stations`, METHOD.POST(data));
    }
  };

  return {
    station, line
  };

})();
export default api;