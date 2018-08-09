import { DETERMINE_USER_LOCATION_SUCCESS, DETERMINE_USER_LOCATION_FAILURE } from '../../globals';

export default function(state = {}, action) {
  switch (action.type) {
    case DETERMINE_USER_LOCATION_SUCCESS:
      console.log(`Got user co-ords: ${JSON.stringify(action)}`);
      return {
        ...action.payload
      };
    case DETERMINE_USER_LOCATION_FAILURE:
      console.log(`Argh, error - server said: ${action.payload}`);
      return action.payload;
    default:
      return state;
  }
}
