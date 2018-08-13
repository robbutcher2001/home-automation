const random = require('../util/random');
const jsend = require('../util/jsend');

const patio = () => {
  const humidity = random(100);
  const batteryLevel = random(100);
  const lux = random(1000);
  const temp = random(40);

  return jsend('patio', {
    'shock_sensor': {
      'shock_detected': false,
      'last_triggered': '03\/08 18:36',
      'battery_level': batteryLevel
    },
    'door_sensor': {
      'open': true,
      'last_triggered': '03\/08 18:36',
      'battery_level': batteryLevel
    },
    'multisensor': {
      'occupied': false,
      'humidity': humidity,
      'battery_level': batteryLevel,
      'last_occupied': '01\/08 19:09',
      'luminiscence': lux,
      'temperature': temp
    },
    'alarm_unit1': {
      'battery_level': batteryLevel
    }
  })
};

module.exports = patio;
