let mymodule = require('./mymodule');
let myvar = mymodule.myvariable;
let myfunc = mymodule.myfunction;

console.log('Hello, from JS!');
console.log('This is a variable:', myvar);
console.log('This is a function result:', myfunc(3, 5));

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
