const METHOD = {
    PUT(data) {
        return {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
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
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(data)
        };
    }
};

const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(data => {
    if (!data.ok) {
    throw data;
    }
    return data.json()
    });

    const requestAwait = async (uri, config) => await fetch(uri, config);

const station = {
    get() {
        return request(`/stations`);
    },
    create(data) {
        return request(`/station`, METHOD.POST(data));
    },
    update(data, id) {
        return request(`/station/${id}`, METHOD.PUT(data));
    },
    delete(id) {
        return fetch(`/stations/${id}`, METHOD.DELETE()).then();
    }
};

const line = {
    get() {
        return request(`/lines`);
    },
    getLineById(id) {
        return request(`/lines/${id}`);
    },
    create(data) {
        return request(`/lines`, METHOD.POST(data));
    },
    update(id, data) {
        return request(`/lines/${id}`, METHOD.PUT(data));
    },
    delete(id) {
        return fetch(`/lines/${id}`, METHOD.DELETE()).then();
    },
    async registerLineStation(data) {
        return await requestAwait(`/lines/${data.id}/stations`, METHOD.POST(data));
    }
};

return {
    station, line
};
})();

export default api;