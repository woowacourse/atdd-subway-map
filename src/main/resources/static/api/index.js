const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => data.json());

    const method = {
        put(data) {
      return {
          method: "put",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      };
    },
        delete() {
      return {
          method: "delete"
      };
    },
        post(data) {
      return {
          method: "post",
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
        return request(`/stations`, method.post(data));
    },
    update(data, stationId) {
        return request(`/stations/${stationId}`, method.put(data));
    },
    delete(stationId) {
        return request(`/stations/${stationId}`, method.delete());
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
        return request(`/lines`, method.post(data));
    },
    update(data, lineId) {
        return request(`/lines/${lineId}`, method.put(data));
    },
    delete(lineId) {
        return request(`/lines/${lineId}`, method.delete());
    },
    createLineStation(data, lineId) {
        return request(`/line/${lineId}/stations`, method.post(data));
    },
      deleteLineStation(lineId, stationId) {
          return request(`/line/${lineId}/stations/${stationId}`, method.delete());
      }
  };

  return {
    station, line
  };

})();
export default api;