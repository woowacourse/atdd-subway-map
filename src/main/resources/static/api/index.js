const BASE_URL = "localhost:8080";

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
    const requestWithData = (uri, config) => fetch(uri, config).then(data => data.json());
    const requestWithNoData = (uri, config) => fetch(uri, config);
    const station = {
        get() {
            return requestWithData(`/stations`);
        },
        create(data) {
            return requestWithNoData(`/stations`, METHOD.POST(data));
        },
        update(data, id) {
            return requestWithData(`/stations/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return fetch(`/stations/${id}`, METHOD.DELETE()).then()
            // return request(`/stations/${id}`, METHOD.DELETE());
        }
    };
    const line = {
        get(path = "") {
            return requestWithData(`/lines` + path);
        },

        getById(id) {
            return requestWithData(`/lines/${id}`);
        },
        create(data, path = "",) {
            return fetch(`/lines${path}`, METHOD.POST(data)).then()
            // return request(`/lines${path}` , METHOD.POST(data));
        },
        update(id, data) {
            return requestWithNoData(`/lines/${id}`, METHOD.PUT(data));
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

