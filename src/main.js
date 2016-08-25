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

        this.graphGenerated = false;
        this.getRequirements();
    }

    getRequirements() {
        ac = new window.AudioContext();

        navigator.getUserMedia(
            {audio: true, video: false},
            stream => {
                this.micInput = ac.createMediaStreamSource(stream);
                this.afterGettingRequirements();
            },
            error => {
                console.log('ERROR', error);
                return;
            }
        );
    }

    afterGettingRequirements() {
        this.generateAudioGraph();

        this.graphGenerated = true;
        this.isMuted = true;
    }

    generateAudioGraph() {
        this.analyser = ac.createAnalyser();
        this.analyser.fftSize = 2048;
        this.out = ac.createGain();
        this.out.gain.value = 0.0;

        this.micInput.connect(this.analyser);
        this.analyser.connect(this.out);
        this.out.connect(ac.destination);
    }

    getOscilloscopeBufferLength() {
        return this.analyser.frequencyBinCount;
    }

    getOscilloscopeData(array) {
        this.analyser.getByteTimeDomainData(array);
    }

    toggleSoundOutput() {
        if (this.isMuted) {
            this.out.gain.value = 1.0;
        }
        else {
            this.out.gain.value = 0.0;
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

    /* UI */
    let container = document.getElementById('container');
    let toggleSound = document.getElementById('btnToggleSound');
    toggleSound.addEventListener('click', ev => {
        ev.preventDefault();
        frq.toggleSoundOutput();
    });

    let osc = document.getElementById('oscilloscope').getContext('2d');
    let bufferLength = 2048;
    let dataArray = new Uint8Array(bufferLength);

    let draw = function () {
        requestAnimationFrame(draw);

        if (! frq.graphGenerated) return;

        frq.getOscilloscopeData(dataArray);

        WIDTH = 800;
        HEIGHT = 300;
        osc.fillStyle = 'rgb(200, 200, 200)';
        osc.fillRect(0, 0, WIDTH, HEIGHT);

        osc.lineWidth = 2;
        osc.strokeStyle = 'rgb(0, 0, 0)';

        osc.beginPath();

        var sliceWidth = WIDTH * 1.0 / bufferLength;
        var x = 0;

        for (var i = 0; i < bufferLength; i++) {

            var v = dataArray[i] / 128.0;
            var y = v * HEIGHT / 2;

            if (i === 0) {
                osc.moveTo(x, y);
            } else {
                osc.lineTo(x, y);
            }

            x += sliceWidth;
        }

        osc.lineTo(WIDTH, HEIGHT / 2);
        osc.stroke();
    };

    draw();

};

initialize();

global.ac = ac;
global.frq = frq;
