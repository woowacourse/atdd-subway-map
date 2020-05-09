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
	const request = (uri, config) => fetch(uri, config).then(data => data.json());
	const nonRequest = (uri, config) => fetch(uri, config);

	const station = {
		get() {
			return request(`/stations`);
		},
		getByName(name) {
			return request(`/stations/${name}`);
		},
		create(data) {
			return request(`/stations`, METHOD.POST(data));
		},
		update(id, data) {
			return request(`/stations/${id}`, METHOD.PUT(data));
		},
		delete(id) {
			return nonRequest(`/stations/${id}`, METHOD.DELETE());
		}
	};

	const line = {
		get(id) {
			if (!id) {
				return request(`/lines`);
			}
			return request(`/lines/${id}`);
		},
		getByName(name) {
			return request(`/lines/name/${name}`);
		},
		create(data) {
			return request(`/lines`, METHOD.POST(data));
		},
		update(id, data) {
			return request(`/lines/${id}`, METHOD.PUT(data));
		},
		delete(id) {
			return nonRequest(`/lines/${id}`, METHOD.DELETE());
		}
	};

	const edge = {
		get(id) {
			return request(`/lines/${id}/stations`);
		},
		update(id, data) {
			return request(`/lines/${id}/stations`, METHOD.PUT(data));
		},
		delete(lineId, stationId) {
			return nonRequest(`/lines/${lineId}/stations/${stationId}`, METHOD.DELETE());
		}
	}

	return {
		station,
		line,
		edge,
	};
})();

export default api;
