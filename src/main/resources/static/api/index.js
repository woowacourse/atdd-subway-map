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
    const request = (uri, config) => fetch(uri, config).then(data => data.json());
    const nonRequest = (uri, config) => fetch(uri, config);

    const station = {
        get() {
            return request(`/stations`);
        },
        create(data) {
            return nonRequest(`/stations`, METHOD.POST(data));
        },
        update(data, id) {
            return nonRequest(`/stations/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return nonRequest(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        get(id) {
            if (!id) {
                return request(`/lines`);
            }
            return request(`/lines/${id}`);
        },
        create(data) {
            return nonRequest(`/lines`, METHOD.POST(data));
        },
        update(data, id) {
            return nonRequest(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return nonRequest(`/lines/${id}`, METHOD.DELETE());
        }
    };

    const lineStation = {
        get(id) {
          return request(`/lines/${id}/stations`);
        },
        update(data, id) {
            return nonRequest(`/lines/${id}/stations`, METHOD.PUT(data));
        },
        delete(lineId, stationId) {
            return nonRequest(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
        }
    }

    return {
        station,
        line,
        lineStation
    };
})();

export default api;
