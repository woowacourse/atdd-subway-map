const METHOD = {
    // todo: 업데이트를 위해 메서드 수정
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
    const request = (uri, config) => fetch(uri, config).then(data => data.json());

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
            return request(`/station/${id}`, METHOD.DELETE);
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
        update(data, id) {
            // todo: fetch 방식으로 변경
            return fetch(`/lines/${id}`, METHOD.PUT(data)).then();
        },
        delete(id) {
            // todo: request 메서드를 사용하면 json으로 바꿔야 하는데..그럴 데이터가 없음
            return fetch(`/lines/${id}`, METHOD.DELETE()).then();
        }
    };

    return {
        station, line
    };
})();

export default api;
