const BASE_URL = "localhost:8080";

const method = {
    put(data) {
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
    delete() {
        return {
            method: "DELETE"
        };
    },
    post(data) {
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
            return request(`/stations`, method.post(data));
        },
        update(data, id) {
            return request(`/stations/${id}`, method.put(data));
        },
        delete(id) {
            return request(`/stations/${id}`, method.delete());
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
            return request('/lines', method.post(data));
        },
        update(data, id) {
            return request(`/lines/${id}`, method.put(data));
        },
        delete(id) {
            return request(`/lines/${id}`, method.delete());
        }
    };

    const edge = {
        get() {
            return request(`/line-stations`);
        },
        post(data, lineId) {
            return request(`/line-stations/${lineId}`, method.post(data));
        },
        delete(lineId, stationId) {
            return request(`/line-stations/${lineId}/${stationId}`, method.delete());
        }
    };

    return {
        station, line, edge
    };
})();

export default api;