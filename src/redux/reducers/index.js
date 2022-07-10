import { combineReducers } from 'redux';

import * as authentication from './authentication';

export default combineReducers({
  ...authentication,
});
