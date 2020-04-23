import apiService from '../index.js'

const BASE_URI = `/stations`

const stationApiService = {
  get() {
    return apiService.get(BASE_URI)
  },
  create(data) {
    return apiService.post(BASE_URI, data)
  },
  delete(id) {
    return apiService.delete(`${BASE_URI}/id`)
  }
}

export default stationApiService