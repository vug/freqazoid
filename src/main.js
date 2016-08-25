let mymodule = require('./mymodule');
let myvar = mymodule.myvariable;
let myfunc = mymodule.myfunction;

console.log('Hello, from JS!');
console.log('This is a variable:', myvar);
console.log('This is a function result:', myfunc(3, 5));

let ac;

/**
 Abstract away from browser vendor prefixes for various APIs that are needed by the app
*/
let abstractAwayVendorPrefixes = function() {
    navigator.getUserMedia = (
        navigator.getUserMedia || navigator.webkitGetUserMedia ||
        navigator.mozGetUserMedia || navigator.msGetUserMedia
    );

    window.AudioContext = window.AudioContext || window.webkitAudioContext;
};

/**
 * Check whether requirements of the app are satisfied by the user's computer and browser
 *
 * @returns {boolean} true if all requirements are satisfied
 */
let checkRequirements = function() {
    let support = {
        'microphone input': Boolean(navigator.getUserMedia),
        'audio context': Boolean(window.AudioContext)
    };

    let isRequirementsSatisfied = true;
    for (let functionality in support) {
        console.log(functionality, support[functionality]);
        if (!support[functionality]) {
            console.log('functionality ' + functionality + ' not supported on your device/browser');
            isRequirementsSatisfied = false;
        }
    }
    return isRequirementsSatisfied;
};
