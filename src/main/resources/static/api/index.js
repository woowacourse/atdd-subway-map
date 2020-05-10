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
		return await data.json();
	}
	const noDataRequest = async (uri, config) => await fetch(uri, config);

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
		async getById(id) {
			return await request(`/lines/${id}`);
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
		async get(lineId) {
			return await request(`/lines/${lineId}/stations`);
		},
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
