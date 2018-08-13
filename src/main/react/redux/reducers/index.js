import { combineReducers } from 'redux';
import { routerReducer } from 'react-router-redux';

import UserGeolocationReducer from './userGeolocationReducer';
import LoungeStatusReducer from './loungeStatusReducer';
import NotificationRequestReducer from './notificationRequestReducer';

const rootReducer = combineReducers({
  userGeolocation: UserGeolocationReducer,
  lounge: LoungeStatusReducer,
  notificationBar: NotificationRequestReducer,
  routing: routerReducer
});

export default rootReducer;
