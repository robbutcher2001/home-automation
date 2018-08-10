import { combineReducers } from 'redux';

import UserGeolocationReducer from './userGeolocationReducer';
import LoungeStatusReducer from './loungeStatusReducer';
import NotificationRequestReducer from './notificationRequestReducer';

const rootReducer = combineReducers({
  userGeolocation: UserGeolocationReducer,
  lounge: LoungeStatusReducer,
  notificationBar: NotificationRequestReducer
});

export default rootReducer;
