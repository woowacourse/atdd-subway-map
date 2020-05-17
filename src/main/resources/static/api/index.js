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
    const request = async (uri, config) => {
        const data = await fetch(uri, config);
        if (!data.ok) {
            const errorMessage = await data.text();
            alert(errorMessage);
        }
        return await data.json();
    }

    const noDataRequest = async (uri, config) => {
        const data = await fetch(uri, config);
        if (!data.ok) {
            const errorMessage = await data.text();
            alert(errorMessage);
        }
        return data;
    }

    const station = {
        async get() {
            return await request(`/stations`);
        },
        async create(data) {
            return await request(`/stations`, METHOD.POST(data));
        },
        async update(id, data) {
            return await request(`/stations/${id}`, METHOD.PUT(data));
        },
        async delete(id) {
            return await noDataRequest(`/stations/${id}`, METHOD.DELETE());
        }
    };

    const line = {
        async get() {
            return await request(`/lines`);
        },
        async create(data) {
            return await request(`/lines`, METHOD.POST(data));
        },
        async update(id, data) {
            return await request(`/lines/${id}`, METHOD.PUT(data));
        },
        async delete(id) {
            return await noDataRequest(`/lines/${id}`, METHOD.DELETE());
        }
    };

    const edge = {
        async update(lindId, data) {
            return await request(`/lines/${lindId}/stations`, METHOD.PUT(data));
        },
        async delete(lineId, stationId) {
            return await noDataRequest(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
        }
    }

    return {
        station,
        line,
        edge,
    };
})();

export default api;
