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
    const request = (uri, config) => fetch(uri, config).then(data => data.json());

    const station = {
        get() {
            request(`${BASE_URL}/stations`);
        },
        create(data) {
            request(`${BASE_URL}/station`, METHOD.POST(data));
        },
        update(data) {
            request(`${BASE_URL}/station/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            request(`${BASE_URL}/station/${id}`, METHOD.DELETE);
        }
    };

    const line = {
        get() {
            return request(`${BASE_URL}/lines`);
        },
        find(id) {
            return request(`${BASE_URL}/lines/${id}`);
        },
        create(data) {
            return request(`${BASE_URL}/lines`, METHOD.POST(data));
        },
        update(id, data) {
           return request(`${BASE_URL}/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            request(`${BASE_URL}/lines/${id}`, METHOD.DELETE);
        }
    };

    return {
        station, line
    };
})();

export default api;
