const BASE_URL = "localhost:8080";

const METHOD = {
    PUT(data) {
        return {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(
                data
            )
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
            body: JSON.stringify(
                data
            )
        };
    }
};

const api = (() => {
    const request = (uri, config) => fetch(uri, config)
        .then(data => data.json());

    const station = {
        get() {
            return request(`/stations`);
        },
        create(data) {
            return request(`/stations`, METHOD.POST(data));
        },
        update(data, id) {
            return request(`/stations/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return request(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        getLine(id) {
            return request(`/lines/${id}`);
        },
        getLines() {
            return request(`/lines`);
        },
        create(data) {
            return request('/lines', METHOD.POST(data));
        },
        update(data, id) {
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
        post(data, lineId) {
            return request(`/edges/${lineId}`, METHOD.POST(data));
        },
        delete(lineId, stationId) {
            return request(`/edges/${lineId}/${stationId}`, METHOD.DELETE());
        }
    };

    return {
        station, line, edge
    };
})();

export default api;