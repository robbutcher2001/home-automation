import { NOTIFICATION_REQUEST_SHOW, NOTIFICATION_REQUEST_HIDE } from '../../globals';

export default function(state = {
  text: null,
  type: '',
  show: false,
  persist: false
}, action) {
  switch (action.type) {
    case NOTIFICATION_REQUEST_SHOW:
      return {
        ...action.payload
      };
    case NOTIFICATION_REQUEST_HIDE:
      return {
        text: state.text,
        type: state.type,
        show: false
      };
    default:
      return state;
  }
}
