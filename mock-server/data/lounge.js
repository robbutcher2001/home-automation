const random = require('../util/random');
const jsend = require('../util/jsend');

const lounge = () => {
  const humidity = random(100);
  const batteryLevel = random(100);
  const lux = random(1000);
  const temp = random(40);
  const percentOpen = random(80);

  return jsend('lounge', {
    'bedroom_mode': 'disabled',
    'multisensor': {
      'occupied': false,
      'humidity': humidity,
      'battery_level': batteryLevel,
      'last_occupied': '03\/08 23:11',
      'luminiscence': lux,
      'temperature': temp
    },
    'blind1': {
      'tilted': false,
      'percent_open': percentOpen
    },
    'blind2': {
      'tilted': true,
      'percent_open': percentOpen
    }
  })
};

module.exports = lounge;
