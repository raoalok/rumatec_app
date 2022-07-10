import Api from '../Api';

export const getBanks = () => async dispatch => {
  const { data } = await Api.get('/accounts/banks');
  dispatch({
    type: 'FETCH_BANKS',
    payload: data,
  });
};

export const clearBanks = () => {
  return {
    type: 'CLEAR_BANKS',
  };
};

export const getBank = id => async dispatch => {
  const { data } = await Api.get(`/accounts/banks/${id}`);
  dispatch({
    type: 'FETCH_BANK',
    payload: data,
  });
};

export const clearBank = () => {
  return {
    type: 'CLEAR_BANK',
  };
};

export const getNarrations = () => async dispatch => {
  const { data } = await Api.get('/dropdowns/bank-narrations');
  dispatch({
    type: 'FETCH_NARRATIONS',
    payload: data,
  });
};

export const clearNarrations = () => {
  return {
    type: 'CLEAR_NARRATIONS',
  };
};

export const getNarration = id => async dispatch => {
  const { data } = await Api.get(`/dropdowns/bank-narrations/${id}`);
  dispatch({
    type: 'FETCH_NARRATION',
    payload: data,
  });
};

export const clearNarration = () => {
  return {
    type: 'CLEAR_NARRATION',
  };
};

export const getBankStatements = query => async dispatch => {
  const { data } = await Api.get('/accounts', query);
  dispatch({
    type: 'FETCH_BANK_STATEMENTS',
    payload: data,
  });
};

export const clearBankStatements = () => {
  return {
    type: 'CLEAR_BANK_STATEMENTS',
  };
};

export const getBankStatement = id => async dispatch => {
  const { data } = await Api.get(`/accounts/${id}`);
  dispatch({
    type: 'FETCH_BANK_STATEMENT',
    payload: data,
  });
};

export const clearBankStatement = () => {
  return {
    type: 'CLEAR_BANK_STATEMENT',
  };
};
