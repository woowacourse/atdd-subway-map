const METHOD = {
    PUT(data) {
        return {
            method: "PUT",
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(
                data
            )
        };
    },
    DELETE(data) {
        return {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(
                data
            )
        };
    },
    POST(data) {
        return {
            method: "POST",
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(
                data
            )
        };
    }
};

const resolver = (response) => {
    return new Promise((resolve, reject) => {
        let func;
        response.status < 400 ? func = resolve : func = reject;
        if (response.status !== 204) {
            response.json().then(data => func({'status': response.status, 'body': data}));
            return;
        }
        func({'status': response.status})
    });
}

const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(resolver);

    const station = {
        get() {
            return request(`/stations`);
        },
        create(data) {
            return request(`/stations`, METHOD.POST(data));
        },
        update(data, id) {
            return request(`/station/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return request(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        get() {
            return request(`/line`);
        },
        findById(id) {
            return request(`/line/${id}`);
        },
        create(data) {
            return request(`/line`, METHOD.POST(data));
        },
        update(id, data) {
            return request(`/line/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return request(`/line/${id}`, METHOD.DELETE());
        }
    };

    const edge = {
        findByLineId(lineId) {
            return request(`/line/${lineId}/edge`);
        },
        create(lineId, data) {
            return request(`/line/${lineId}/edge`, METHOD.POST(data));
        },
        delete(lineId, data) {
            return request(`/line/${lineId}/edge`, METHOD.DELETE(data));
        },
        getLineEdge() {
            return request(`/line/edge`);
        }
    };

    return {
        station, line, edge
    };
})();

export default api;
