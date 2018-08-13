const random = require('../util/random');
const jsend = require('../util/jsend');

const study = () => {
  const batteryLevel = random(100);

  return jsend('scarlett_room',  {
      'window_sensor': {
        'open': false,
        'last_triggered': '01\/08 19:09',
        'battery_level': batteryLevel
      }
    })
};

module.exports = study;
