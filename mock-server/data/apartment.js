const jsend = require('../util/jsend');

const apartment = () => jsend('apartment', {
    "occupied": true,
    "unexpected_occupancy": "false",
    "continuous_alarm_mode": "disabled",
    "last_occupied": null,
    "alarm_system": false,
    "announcements_muted": false,
    "bedroom_to_render": "implicitLogin",
    "force_disabled": false
  });

module.exports = apartment;
