export const auth = (state = { type: false, user: null }, action) => {
  if (action.type === 'FETCH_TOKEN') {
    return action.payload;
  }
  if (action.type === 'REMOVE_TOKEN') {
    return { type: false, user: null };
  }
  return state;
};
