let mymodule = require('./mymodule');
let myvar = mymodule.myvariable;
let myfunc = mymodule.myfunction;

console.log('This is a variable:', myvar);
console.log('This is a function result:', myfunc(3, 5));

let ac;
let frq;

class Freqazoid {
    constructor() {
        abstractAwayVendorPrefixes();
        let isSatisfied = checkRequirements();
        if (!isSatisfied) {
            alert('Some of the requirements are not satisfied by your browser or computer.');
            return;
        }

        ac = new window.AudioContext();
        navigator.getUserMedia(
            {audio: true, video: false},
            stream => {
                this.micInput = ac.createMediaStreamSource(stream);
                this.out = ac.createAnalyser();
                this.micInput.connect(this.out);
            },
            error => {
                console.log('ERROR', error);
            }
        );
        this.isMuted = true;
    }

    toggleSoundOutput() {
        if (this.isMuted) {
            this.out.connect(ac.destination);
        }
        else {
            this.out.disconnect();
        }

        this.isMuted = !this.isMuted;
    }
}


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

/**
 * Initializes the app
 */
let initialize = function() {
    frq = new Freqazoid();

};


initialize();
