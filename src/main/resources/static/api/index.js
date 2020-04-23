const request = (uri, config) => fetch(uri, config).then(data => data.json());

const apiService = {
  get(uri) {
    return request(`${uri}`)
  },
  post(uri, data) {
    return request(`${uri}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        content: data
      })
    });
  },
  delete(uri) {
    return request(`${uri}`, {
      method: "DELETE"
    });
  }
}

export default apiService;
