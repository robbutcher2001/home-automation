import { NOTIFICATION_REQUEST_SHOW, NOTIFICATION_REQUEST_HIDE } from '../../globals';

export default function(state = {}, action) {
  switch (action.type) {
    case NOTIFICATION_REQUEST_SHOW:
      console.log(`Showing notification bar: ${JSON.stringify(action)}`);
      return {
        ...action.payload.notificationBar
      };
    case NOTIFICATION_REQUEST_HIDE:
      console.log(`Hiding notification bar: ${JSON.stringify(action)}`);
      return {
        ...action.payload.notificationBar
      };
    default:
      return state;
  }
}
