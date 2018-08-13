const random = require('../util/random');
const jsend = require('../util/jsend');

const robRoom = () => {
  const humidity = random(100);
  const batteryLevel = random(100);
  const lux = random(1000);
  const temp = random(40);
  const percentOpen = random(80);

  return jsend('rob_room', {
    'electric_blanket1': {
      'next_state': 'Warm the bed for 20 minutes',
      'is_warming': true
    },
    'dehumidifier1': {
      'dehumidifying': false
    },
    'next_lighting_state': 'full',
    'window_sensor': {
      'open': true,
      'last_triggered': '03\/08 18:36',
      'battery_level': batteryLevel
    },
    'door_sensor': {
      'open': true,
      'last_triggered': '03\/08 10:17',
      'battery_level': batteryLevel
    },
    'multisensor': {
      'occupied': false,
      'humidity': humidity,
      'battery_level': batteryLevel,
      'last_occupied': '03\/08 23:12',
      'luminiscence': lux,
      'temperature': temp
    },
    'full_bedroom_mode': 'disabled',
    'blind1': {
      'tilted': false,
      'percent_open': percentOpen
    }
  })
};

module.exports = robRoom;
