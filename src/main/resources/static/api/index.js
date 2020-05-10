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
    const request = (uri, config) => fetch(uri, config).then(
        data => data.json());

    const line = {
        get(path = "") {
            return request('/api/lines' + path);
        },
        create(data, path = "") {
            return fetch(`/api/lines${path}`, METHOD.POST(data));
        },
        delete(path = "") {
            return fetch('/api/lines' + path, METHOD.DELETE());
        },
        update(path, data) {
            return fetch('/api/lines' + path, METHOD.PUT(data))
        }
    };

    const station = {
        get() {
            return request(`/api/stations`);
        },
        create(data) {
            return fetch(`/api/stations`, METHOD.POST(data));
        },
        delete(id) {
            return fetch(`/api/stations/${id}`, METHOD.DELETE());
        }
    };

    return {
        line,
        station
    };
})();

export default api;