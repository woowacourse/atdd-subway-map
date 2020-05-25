const BASE_URL = "localhost:8080";

const METHOD = {
    PUT(data) {
        return {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data
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
            body: JSON.stringify(data
            )
        };
    }
};

const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => data.json());
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
            return fetch(`/stations/${id}`, METHOD.DELETE()).then()
        }
    };
    const line = {
        get(path = "") {
            return request(`/lines` + path);
        },

        getById(id) {
            return request(`/lines/${id}`);
        },
        create(data, path = "",) {
            return fetch(`/lines${path}`, METHOD.POST(data))
            // return request(`/lines${path}` , METHOD.POST(data));
        },
        update(id, data) {
            return request(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return fetch(`/lines/${id}`, METHOD.DELETE()).then()
            // return request(`/lines/${id}`, METHOD.DELETE());
        }
    };
    return {
        station, line
    };
})();
export default api;

