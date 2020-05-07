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
      return request(`/station/${id}`, METHOD.DELETE);
    }
  };

  return {
    station
  };

})();
export default api;