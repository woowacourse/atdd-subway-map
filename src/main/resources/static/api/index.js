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

export const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => data.json());
    const requestWithoutResponseBody = (uri, config) => fetch(uri, config);

    const station = {
        get() {
            return request(`${BASE_URL}/stations`);
        },
        create(data) {
            console.log(data)
            return request(`${BASE_URL}/stations`, METHOD.POST(data));
        },
        update(data) {
            return request(`${BASE_URL}/stations/${id}`, METHOD.PUT(data));
        },
        delete(name) {
            return requestWithoutResponseBody(
                `${BASE_URL}/stations/${name}`, METHOD.DELETE());
        }
    };


    const line = {
        getLines() {
            return request(`${BASE_URL}/lines`);
        },
        getLine(lineId) {
            return request(`${BASE_URL}/lines/${lineId}`)
        },
        create(data) {
            return request(`${BASE_URL}/lines`, METHOD.POST(data));
        },
        update(lineId, data) {
            return requestWithoutResponseBody(`${BASE_URL}/lines/${lineId}`, METHOD.PUT(data));
        },
        delete(lineId) {
            return requestWithoutResponseBody(
                `${BASE_URL}/lines/${lineId}`, METHOD.DELETE());
        }
    };

    const lineStation = {
        getLines() {
            return request(`${BASE_URL}/lineStations`);
        },
        create(lineId, data) {
            requestWithoutResponseBody(`${BASE_URL}/lineStations/${lineId}`, METHOD.POST(data));
        },
        delete(lineId, stationId) {
            return requestWithoutResponseBody(
                `${BASE_URL}/lineStations/${lineId}/${stationId}`, METHOD.DELETE());
        }
    };

    return {
        station,
        line,
        lineStation
    };
})();
