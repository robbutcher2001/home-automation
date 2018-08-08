import { POLL_START, POLL_STOP, GET_DATA_SUCCESS } from '../sagas';

const initialState = {
  data: false,
  polling: false,
};

export default function(state = initialState, action) {
  switch (action.type) {
    case POLL_START:
      console.log("polling started!");
      return {
        ...state,
        polling: true,
      }
    case POLL_STOP:
      return {
        ...state,
        polling: false,
      }
    case GET_DATA_SUCCESS:
      return {
        ...state,
        data: action.payload
      };
    default:
      return state;
  }
}
