const random = require('../util/random');
const jsend = require('../util/jsend');

const hallway = () => {
  const batteryLevel = random(100);

  return jsend('hallway', {
    "door_sensor": {
      "open": false,
      "last_triggered": "03\/08 19:49",
      "battery_level": batteryLevel
    },
    "alarm_unit1": {
      "battery_level": batteryLevel
    }
  })
};

module.exports = hallway;
