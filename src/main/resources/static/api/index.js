const method = {
  put(data) {
    return {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    };
  },
  delete() {
    return {
      method: "DELETE"
    };
  },
  post(data) {
    return {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Access-Control-Expose-Headers": "Location"
      },
      body: JSON.stringify(data)
    };
  }
};

const api = (() => {
  const parseJsonIfStatusOk = async data =>  {
    if (!data.ok) {
      const errorObject = await data.json();
      throw new Error(errorObject.message)
    }
    return data.json()
  }

  const throwErrorIfStatusNotOk = async data =>  {
    if (!data.ok) {
      const errorObject = await data.json();
      throw new Error(errorObject.message)
    }
    return data
  }

  const requestGetData = (uri, config) => fetch(uri, config).then(data => parseJsonIfStatusOk(data));
  const requestOther = (uri, config) => fetch(uri, config).then(data => throwErrorIfStatusNotOk(data));

  const station = {
    get() {
      return requestGetData(`/stations`);
    },
    create(data) {
      return requestGetData(`/stations`, method.post(data));
    },
    delete(id) {
      return requestOther(`/stations/${id}`, method.delete());
    }
  };

  const line = {
    get() {
      return requestGetData(`/lines`);
    },
    create(data) {
      return requestGetData(`/lines`, method.post(data));
    },
    update(data, id) {
      return requestOther(`/lines/${id}`, method.put(data));
    },
    delete(id) {
      return requestOther(`/lines/${id}`, method.delete());
    }
  };

  const edge = {
    get(lineId) {
      return requestGetData(`/lines/${lineId}/stations`);
    },
    create(lineId, data) {
      return requestOther(`/lines/${lineId}/stations`, method.post(data));
    },
    delete(lineId, stationId) {
      return requestOther(`/lines/${lineId}/stations/${stationId}`, method.delete());
    }
  };
  return {
    station, line, edge
  };
})();
export default api;
