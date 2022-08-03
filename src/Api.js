import axios from 'axios';
import { apiURL } from './config';

const axiosObj = axios.create({ baseURL: apiURL });

const Api = {
  get: (url = '', params = {}) =>
    axiosObj.get(url, {
      headers: {
        authorization: localStorage.getItem('token') || '',
      },
      params,
    }),
  getFile: (url = '', params = {}) =>
    axiosObj.get(url, {
      headers: {
        authorization: localStorage.getItem('token') || '',
      },
      responseType: 'blob',
      params,
    }),
  post: (url = '', body = {}, options = {}) =>
    axiosObj.post(url, body, {
      ...options,
      headers: {
        ...(options.headers || {}),
        authorization: localStorage.getItem('token') || '',
      },
    }),
  put: (url = '', body = {}) =>
    axiosObj.put(url, body, {
      headers: {
        authorization: localStorage.getItem('token') || '',
      },
    }),
  delete: (url = '', body = {}) =>
    axiosObj.delete(url, {
      headers: {
        authorization: localStorage.getItem('token') || '',
      },
      data: body,
    }),
};

export default Api;
