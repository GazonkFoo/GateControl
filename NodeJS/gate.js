var config = require("./config");
var gpio = require("pi-gpio");

function Gate() {
    if (!(this instanceof Gate)) {
        return new Gate();
    }

    this.callbacks = [];
    this.gateState = undefined;

    gpio.open(config.statePin, "input pullup");
    gpio.open(config.buttonPin, "output");

    setInterval(checkState, config.checkInterval, this);

    process.on("SIGTERM", unwatchGPIO);
    process.on("SIGINT", unwatchGPIO);
}

function unwatchGPIO() {
    gpio.close(config.statePin);
    gpio.close(config.buttonPin);

    process.exit();
}

function checkState (_this) {
    gpio.read(config.statePin, function(err, value) {
        if(err) {
            console.warn(err);
            _this.callbacks.forEach(function(callback) {
                callback(err, null);
            });
        } else if (value != _this.gateState) {
            _this.gateState = value;
            _this.callbacks.forEach(function(callback) {
                callback(null, _this.gateState);
            });
        }
    });
}

Gate.prototype.getGateState = function() {
    return this.gateState;
};

Gate.prototype.addCallback = function(callback) {
    this.callbacks.push(callback);
};

Gate.prototype.pushButton = function(callback) {
    gpio.write(config.buttonPin, 1, function(err) {
        if(err) {
            console.warn(err);
            callback(err);
            return;
        }

        setTimeout(function() {
            gpio.write(config.buttonPin, 0, function(err) {
                if(err) {
                    console.warn(err);
                    callback(err);
                } else {
                    callback(null);
                }
            });
        }, config.buttonDuration);
    });
};

module.exports = Gate;