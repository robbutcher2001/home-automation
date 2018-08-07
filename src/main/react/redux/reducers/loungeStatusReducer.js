import { LOUNGE_ACTION } from '../../actions/loungeAction';

export default function(state = {}, action) {
  switch (action.type) {
    case LOUNGE_ACTION:
      console.log(`Server said: ${action.payload.status}`);
      return {
        ...action.payload.lounge
      };
    default:
      return state;
  }
}
