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
            body: JSON.stringify(data)
        };
    }
};

const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => data.json());
    const deleteRequest = (uri, config) => fetch(uri, config).then();

    const station = {
        get() {
            return request(`/api/stations`);
        },
        create(data) {
            return request(`/api/stations`, METHOD.POST(data));
        },
        update(data, id) {
            return request(`/api/stations/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return deleteRequest(`/api/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        getOneLine(id) {
            return request(`/api/lines/${id}`);
        },
        get() {
            return request(`/api/lines`);
        },
        create(data) {
            return request(`/api/lines`, METHOD.POST(data));
        },
        update(data, id) {
            return request(`/api/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return deleteRequest(`/api/lines/${id}`, METHOD.DELETE());
        },
        getLineStations(lineId) {
            return request(`/api/lines/${lineId}/stations`);
        },
        addLineStation(lineId, data) {
            return request(`/api/lines/${lineId}/stations`, METHOD.POST(data));
        },
        deleteLineStation(lineId, stationId) {
            return request(`/api/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
        }
    };

    return {
        line
    };
})();

export default api;
