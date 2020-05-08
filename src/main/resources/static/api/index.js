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
            return request(`${BASE_URL}/stations`);
        },
        create(data) {
            return request(`${BASE_URL}/stations`, METHOD.POST(data));
        },
        update(data) {
            return request(`${BASE_URL}/stations/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return request(`${BASE_URL}/stations/${id}`, METHOD.DELETE);
        }
    };

    const line = {
        get() {
            return request(`${BASE_URL}/lines`).then(data => data.json());
        },
        find(id) {
            return request(`${BASE_URL}/lines/${id}`).then(data => data.json());
        },
        create(data) {
            return request(`${BASE_URL}/lines`, METHOD.POST(data)).then(data => data.json());
        },
        update(id, data) {
            return request(`${BASE_URL}/lines/${id}`, METHOD.PUT(data)).then(data => data.json());
        },
        delete(id) {
            return request(`${BASE_URL}/lines/${id}`, METHOD.DELETE());
        }
    };

    return {
        station, line
    };
})();

export default api;
