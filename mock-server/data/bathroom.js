const random = require('../util/random');
const jsend = require('../util/jsend');

const bathroom = () => {
  const humidity = random(100);
  const batteryLevel = random(100);
  const lux = random(1000);
  const temp = random(40);

  return jsend('bathroom', {
    'multisensor': {
      'occupied': false,
      'humidity': humidity,
      'battery_level': batteryLevel,
      'last_occupied': '03\/08 23:13',
      'luminiscence': lux,
      'temperature': temp
    }
  })
};

module.exports = bathroom;
