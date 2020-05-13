const METHOD = {
    PUT(data) {
        return {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(
                data
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
            body: JSON.stringify(
                data
            )
        };
    }
};

const api = (() => {
    const request = async (uri, config) => {
        try {
            const response = await fetch(uri, config);
            if (response.status === 400) {
                const error = await response.json();
                throw new Error(error.message);
            }
            return await response.json();
        } catch (error) {
            alert(error.message);
        }
    };

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
            return request(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        getLine(id) {
            return request(`/lines/${id}`);
        },
        getLines() {
            return request(`/lines`);
        },
        create(data) {
            return request('/lines', METHOD.POST(data));
        },
        update(data, id) {
            return request(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return request(`/lines/${id}`, METHOD.DELETE());
        }
    };

    const edge = {
        get() {
            return request(`/lines/edges`);
        },
        post(data, lineId) {
            return request(`/lines/${lineId}/edges`, METHOD.POST(data));
        },
        delete(lineId, stationId) {
            return request(`/lines/${lineId}/edges/${stationId}`, METHOD.DELETE());
        }
    };

    return {
        station, line, edge
    };
})();

export default api;