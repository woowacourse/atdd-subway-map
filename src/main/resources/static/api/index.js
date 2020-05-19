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
        getAll() {
            return requestWithData(`/stations`);
        },
        getById(id) {
            return requestWithData(`/stations/${id}`);
        },
        create(data) {
            return requestWithNoData(`/stations`, METHOD.POST(data));
        },
        delete(id) {
            return requestWithNoData(`/stations/${id}`, METHOD.DELETE());
        }
    };
    const line = {
        getAll() {
            return requestWithData(`/lines`);
        },
        getLinesWithStations() {
            return requestWithData(`/lines/stations`);
        },
        getById(id) {
            return requestWithData(`/lines/${id}`);
        },
        create(data) {
            return requestWithNoData(`/lines`, METHOD.POST(data))
        },
        createEdge(lineId, data) {
            return requestWithNoData(`/lines/${lineId}/stations`, METHOD.POST(data));
        },
        update(id, data) {
            return requestWithNoData(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return requestWithNoData(`/lines/${id}`, METHOD.DELETE());
        },
        deleteEdge(lineId, stationId){
            return requestWithNoData(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
        }
    };
    return {
        station, line
    };
})();
export default api;

