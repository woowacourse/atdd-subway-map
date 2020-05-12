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
    const request = (uri, config) => fetch(uri, config)
    .then(response => {
        if (!response.ok) {
            alert("뭔가 잘못되었어요! 실패했습니다.");
            console.log(response.text());
        }
        return response;
    });

    const station = {
        get() {
            return request(`/stations`);
        },
        create(data) {
            return request(`/stations`, METHOD.POST(data));
        },
        delete(id) {
            return request(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        get() {
            return request(`/lines`);
        },
        getById(id) {
            return request(`/lines/${id}`);
        },
        create(data) {
            return request(`/lines`, METHOD.POST(data));
        },
        update(id, data) {
            return request(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return request(`/lines/${id}`, METHOD.DELETE());
        }
    };

    const edge = {
        get(id) {
            return request(`/lines/${id}/stations`);
        },
        create(id, data) {
            return request(`/lines/${id}/stations`, METHOD.POST(data));
        },
        delete(lineId, stationId) {
            return request(`/lines/${lineId}/stations/${stationId}`,
                METHOD.DELETE());
        }
    };

    return {
        station,
        line,
        edge
    };
})();

export default api;
