import { combineReducers } from 'redux';
import LoungeStatusReducer from './loungeStatusReducer';

const rootReducer = combineReducers({
  lounge: LoungeStatusReducer
});

export default rootReducer;
