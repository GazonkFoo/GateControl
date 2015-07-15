var path = require("path");

var config = module.exports = {};

config.port = 8080;
config.basePath = "/gate";
config.androidPath = "/android";

config.statePin = 15;
config.buttonPin = 7;

config.buttonDuration = 1000;
config.checkInterval = 250;

config.gcmKey = '';