const BASE_URL = "http://localhost:8080";

const METHOD = {
    PUT() {
        return {
            method: "PUT"
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

export const stationApi = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => data.json());

    const station = {
        get() {
            return request(`${BASE_URL}/station`);
        },
        create(data) {
            console.log(data)
            return request(`${BASE_URL}/station`, METHOD.POST(data));
        },
        update(data) {
            return request(`${BASE_URL}/station/${id}`, METHOD.PUT(data));
        },
        delete(name) {
            return request(`${BASE_URL}/station/${name}`, METHOD.DELETE());
        }

    };

    return {
        station
    };
})();


export const lineApi = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => data.json());

    const line = {
        get() {
            return request(`${BASE_URL}/line`);
        },
        create(data) {
            console.log(data)
            return request(`${BASE_URL}/line`, METHOD.POST(data));
        },
        update(lineId, data) {
            return request(`${BASE_URL}/line/${lineId}`, METHOD.PUT(data));
        },
        delete(lineId) {
            return request(`${BASE_URL}/line/${lineId}`, METHOD.DELETE());
        }
    };

    return {
        line
    };
})();

