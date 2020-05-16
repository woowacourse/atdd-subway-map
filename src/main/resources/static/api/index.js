const BASE_URL = "http://localhost:8080";

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
    const request = (uri, config) => fetch(uri, config);

    const station = {
        get() {
            return request(`${BASE_URL}/api/stations`).then(data => data.json());
        },
        find(id) {
            return request(`${BASE_URL}/api/stations/${id}`).then(data => data.json());
        },
        create(data) {
            return request(`${BASE_URL}/api/stations`, METHOD.POST(data));
        },
        delete(id) {
            return request(`${BASE_URL}/api/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        get() {
            return request(`${BASE_URL}/api/lines`).then(data => data.json());
        },
        find(id) {
            return request(`${BASE_URL}/api/lines/${id}`).then(data => data.json());
        },
        create(data) {
            return request(`${BASE_URL}/api/lines`, METHOD.POST(data));
        },
        update(id, data) {
            return request(`${BASE_URL}/api/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return request(`${BASE_URL}/api/lines/${id}`, METHOD.DELETE());
        }
    };

    const lineStation = {
        create(id, data) {
            return request(`${BASE_URL}/api/lines/${id}/stations`, METHOD.PUT(data)).then(data => data.json());
        },
        delete(lineId, stationId) {
            return request(`${BASE_URL}/api/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
        }
    };

    return {
        station, line, lineStation
    };
})();

export default api;