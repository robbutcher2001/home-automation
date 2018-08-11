import { LOUNGE_STATUS_SUCCESS, LOUNGE_STATUS_FAILURE } from '../../globals';

export default function(state = {}, action) {
  switch (action.type) {
    case LOUNGE_STATUS_SUCCESS:
      return {
        ...action.payload.lounge
      };
    case LOUNGE_STATUS_FAILURE:
      return action.payload;
    default:
      return state;
  }
}
