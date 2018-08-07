const LOUNGE_URL = "http://localhost:3000/deviceStatus/lounge";

export const LOUNGE_ACTION = 'LOUNGE_ACTION';

export function doGet() {
  console.log('Fetching lounge status');

  const response = fetch(LOUNGE_URL)
    .then(data => data.json())
    .catch(err => { err });

  return {
    type: LOUNGE_ACTION,
    payload: response
  }
}
