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
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    update(data, id) {
      return request(`/station/${id}`, METHOD.PUT(data));
    },
    delete(id) {
        return fetch(`/stations/${id}`, METHOD.DELETE()).then();
    }
  };

    const line = {
        get() {
            return request(`/lines`);
        },
        getLineById(id) {
            return request(`/lines/${id}`);
        },
        create(data) {
            return request(`/lines`, METHOD.POST(data));
        },
        update(id, data) {
            return request(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return fetch(`/lines/${id}`, METHOD.DELETE()).then();
        },
        registerLineStation(data) {
            return request(`/lines/line-stations`, METHOD.POST(data));
        },
        deleteLineStation(lineId, stationId) {
            return fetch(`/lines/${lineId}/line-stations/${stationId}`, METHOD.DELETE()).then();
        }
    };

    return {
        station, line
    };
})();

export default api;
