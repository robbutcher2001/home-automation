import { LOUNGE_STATUS_SUCCESS, LOUNGE_STATUS_FAILURE } from '../../globals';

export default function(state = {}, action) {
  switch (action.type) {
    case LOUNGE_STATUS_SUCCESS:
      console.log(`Server said: ${action.payload.status}`);
      return {
        ...action.payload.lounge
      };
    case LOUNGE_STATUS_FAILURE:
      console.log(`Argh, error - server said: ${action.payload}`);
      return action.payload;
    default:
      return state;
  }
}
