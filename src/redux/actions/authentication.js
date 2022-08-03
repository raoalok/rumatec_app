import Api from '../Api';

export const login =
  (body = {}) =>
  async dispatch => {
    const { data } = await Api.post('/auth/login', body);

    localStorage.setItem('token', data.token);

    dispatch({
      type: 'FETCH_TOKEN',
      payload: {
        type: true,
        user: data.user,
        permissions: data.permissions,
      },
    });
  };

export const logout = () => {
  localStorage.clear('token');
  return {
    type: 'REMOVE_TOKEN',
  };
};
