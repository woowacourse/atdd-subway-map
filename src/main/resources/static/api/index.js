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
    const noContentRequest = (uri, config) => fetch(uri, config).then(response => {
        if (!response.ok) {
            throw Error("메서드 호출에 실패했습니다.");
        }
    });
    const request = (uri, config) => fetch(uri, config).then(response => {
        if (response.ok) {
            return response.json();
        }
        return response.json()
            .then(error => {
                throw Error(error.errorType);
            });
    });

    const station = {
        get() {
            return request(`/stations`);
        },
        create(data) {
            return request(`/stations`, METHOD.POST(data));
        },
        update(data) {
            return request(`/stations/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return noContentRequest(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        get() {
            return request(`/lines`);
        },
        findBy(id) {
            return request(`/lines/${id}`);
        },
        create(data) {
            return request(`/lines`, METHOD.POST(data));
        },
        update(id, data) {
            return request(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return noContentRequest(`/lines/${id}`, METHOD.DELETE());
        }
    };

    const lineStation = {
        get() {
            return request(`/lines/stations`);
        },
        create(lineId, data) {
            return request(`/lines/${lineId}/stations`, METHOD.POST(data));
        },
        update(lineId, stationId) {
            return noContentRequest(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
        }
    };

    return {
        station,
        line,
        lineStation
    };
})();

export default api;
