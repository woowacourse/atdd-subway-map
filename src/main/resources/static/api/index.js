const BASE_URL = "localhost:8080";

const METHOD = {
    PUT(data) {
        return {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                ...data
            })
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
            body: JSON.stringify({
                ...data
            })
        };
    }
};

const api = (() => {
    const request = (uri, config) => fetch(uri, config);

    const station = {
        get() {
            return request(`/stations`).then(data => data.json());
        },
        create(data) {
            return request(`/stations`, METHOD.POST(data)).then(data => data.json());
        },
        update(data) {
            return request(`/stations/${id}`, METHOD.PUT(data)).then(data => data.json());
        },
        delete(id) {
            return request(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        get() {
            return request(`/lines/${id}`).then(data => data.json());
        },
        create(data) {
            return request(`/lines`, METHOD.POST(data)).then(data => data.json());
        },
        update(id, data) {
            return request(`/lines/${id}`, METHOD.PUT(data)).then(data => data.json());
        },
        delete(id) {
            return request(`/lines/${id}`, METHOD.DELETE());
        }
    };

    const lines = {
        get() {
            return request(`/lines`).then(data => data.json());
        },
    };

    return {
        station,
        line,
        lines
    };
})();

export default api;
