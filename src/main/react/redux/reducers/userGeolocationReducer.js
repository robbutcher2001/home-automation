import { DETERMINE_USER_LOCATION_SUCCESS } from '../../globals';

export default function(state = {}, action) {
  switch (action.type) {
    case DETERMINE_USER_LOCATION_SUCCESS:
      console.log(`Got user co-ords: ${JSON.stringify(action)}`);
      return {
        ...action.payload
      };
    default:
      return state;
  }
}
