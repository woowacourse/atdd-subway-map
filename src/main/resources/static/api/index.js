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

const api = (() => {
    const request = (uri, config) => fetch(uri, config).then(response => {
        if (!response.ok) {
            throw Error("메서드 호출에 실패했습니다.");
        }
        return response.json();
    });
    const noContentRequest = (uri, config) => fetch(uri, config).then(response => {
        if (!response.ok) {
            throw Error("메서드 호출에 실패했습니다.");
        }
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
        update(data) {
            return request(`/lines/${id}`, METHOD.PUT(data));
        },
        delete(id) {
            return noContentRequest(`/lines/${id}`, METHOD.DELETE());
        }
    };

    return {
        station,
        line
    };
})();

export default api;
