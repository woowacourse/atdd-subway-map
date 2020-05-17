const METHOD = {
    POST(data) {
      return {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      };
    },
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
    }
  }
;

const api = (() => {
  const request = (uri, config) => fetch(uri, config).then(async res => {
    console.log(res);
    if (!res.ok) {
      const error = await res.json();
      throw new Error(error.message);
    }
    return await res.json();
  }).catch(console.log);

  const station = {
    get() {
      return request(`/stations`);
    },
    create(data) {
      return request(`/stations`, METHOD.POST(data));
    },
    delete(id) {
      return request(`/stations/${id}`, METHOD.DELETE());
    }
  };

  const line = {
    get() {
      return request(`/lines`);
    },
    getLine(id) {
      return request(`/lines/${id}`);
    },
    create(data) {
      return request(`/lines`, METHOD.POST(data))
    },
    update(id, data) {
      return request(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
      return request(`/lines/${id}`, METHOD.DELETE());
    }
  };

  const edge = {
    get() {
      return request(`/edges`);
    },
    create(data) {
      return request(`/edges`, METHOD.POST(data))
    },
    delete(lineId, stationId) {
      return request(`/edges/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
    }
  }

  return {
    station, line, edge
  };
})
();

export default api;
