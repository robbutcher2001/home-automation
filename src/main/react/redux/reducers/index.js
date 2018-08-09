import { combineReducers } from 'redux';

import UserGeolocationReducer from './userGeolocationReducer';
import LoungeStatusReducer from './loungeStatusReducer';

const rootReducer = combineReducers({
  userGeolocation: UserGeolocationReducer,
  lounge: LoungeStatusReducer
});

export default rootReducer;
