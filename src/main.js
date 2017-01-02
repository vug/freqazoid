var visualizations = require('./visualizations');
var TwoWayMismatch = require('./twoWayMismatch').TwoWayMismatch;
var Oscilloscope = visualizations.Oscilloscope;
var Spectroscope = visualizations.Spectroscope;


/**
 * An audio buffer of size hopSize * numHops. At each iteration it takes hopSize new samples, puts them at the end of
 * the frame after sliding old samples towards the left of the frame (while getting rid of the earliest hop in the
 * frame)
 * Assume that each place represents a hop of data, and the array is one frame, this is its evolution:
 * [0 0 0 0] -> [0 0 0 1] -> [0 0 1 2] -> [0 1 2 3] -> [1 2 3 4] -> [2 3 4 5] -> ...
 */
class AnalysisBuffer {
    /**
     * Create an AnalysisBuffer.
     * @param {AudioContext} context - Web Audio API audio context
     * @param {int} hopSize - an audio process event is triggered every hopSize samples
     * @param {int} numHops - the window size in number of hops
     */
    constructor(context, hopSize, numHops) {
        this.context = context;
        this.hopSize = hopSize;
        this.numHops = numHops;

        this.processes = [];
        this.createWindowAndFFT();

        this.node = this.context.createScriptProcessor(this.hopSize, 1, 1);
        this.node.onaudioprocess = this.processAudioCallback.bind(this);
    }

    createWindowAndFFT() {
        this.frame = new Float32Array(this.hopSize * this.numHops);
        this.fft = new FFT(this.frame.length, 44100);
    }

    setHopSize(hopSize) {
        this.hopSize = hopSize;
        this.frame = new Float32Array(this.hopSize * this.numHops);
        this.createWindowAndFFT();
    }

    setNumHops(numHops) {
        this.numHops = numHops;
        this.createWindowAndFFT();
    }

    /**
     * Shift the frame towards left by hopSize samples and copy the inputData at the end of the frame.
     * Calculate FFT.
     * Run registered process functions.
     *
     * This function is a callback function that listens to the AudioProcess event.
     * @param audioProcessingEvent
     */
    processAudioCallback(audioProcessingEvent) {
        var time = audioProcessingEvent.playbackTime;
        var inputBuffer = audioProcessingEvent.inputBuffer;
        var outputBuffer = audioProcessingEvent.outputBuffer;
        var inputData = inputBuffer.getChannelData(0);
        var outputData = outputBuffer.getChannelData(0);

        outputData.set(inputData);

        this.frame.copyWithin(0, this.hopSize);
        this.frame.set(inputData, this.hopSize * (this.numHops - 1));

        this.fft.forward(this.frame);

        for (var process of this.processes) {
            process(this.frame, this.fft.spectrum);
        }
    }

    registerProcess(processFunc) {
        this.processes.push(processFunc);
    }
}

window.AudioContext = window.AudioContext || window.webkitAudioContext;
class AudioEngine {
    constructor(context, mic) {
        this.context = context;
        this.micInput = mic;

        this.oscillator = this.context.createOscillator();
        this.oscVolume = this.context.createGain();
        this.gain = this.context.createGain();
        this.analyser = this.context.createAnalyser();
//        this.analysisBuffer = new AnalysisBuffer(this.context, 1024, 4);
        this.analysisBuffer = new AnalysisBuffer(this.context, 2048, 2);

        this.oscillator.type = 'sine';
        this.oscillator.frequency.value = 440;
        this.oscillator.start();
        this.oscVolume.gain.value = 0.0;
        this.gain.gain.value = 0.0;
        this.analyser.smoothingTimeConstant = 0.5;

        this.analyser.fftSize = 2048;

        this.oscillator.connect(this.oscVolume);
        this.oscVolume.connect(this.analysisBuffer.node);
        this.micInput.connect(this.analysisBuffer.node);
        this.analysisBuffer.node.connect(this.gain);
        this.analysisBuffer.node.connect(this.analyser);
        this.gain.connect(this.context.destination);
    }
}

navigator.getUserMedia(
    {audio: true, video: false},
    stream => {
        var context = new window.AudioContext();
        var mic = context.createMediaStreamSource(stream);

        var ae = new AudioEngine(context, mic);
        init(ae);
    },
    error => {
        console.log('ERROR', error);
        return;
    }
);


function init(ae) {
    var twm = new TwoWayMismatch();
    ae.analysisBuffer.registerProcess(twm.process.bind(twm));
    var osc = new Oscilloscope(ae.analysisBuffer, 'osc');
    var spc = new Spectroscope(ae.analysisBuffer, twm, 'spc');
    var visualizations = [osc, spc];
    for (let vis of visualizations) {
        window.addEventListener('resize', vis.resize.bind(vis), false);
        vis.animate();
    }
    vue =  new Vue({
        el: '#app',
        data: {
            ae: ae,
            twm: twm,
            enginePlaying: true,
            x: 100
        },
        methods: {
            pauseAudioEngine: function() {
                if(this.ae.context.state === "running") {
                    this.ae.context.suspend();
                    this.enginePlaying = false;
                }
                else if(this.ae.context.state === "suspended") {
                    this.ae.context.resume();
                    this.enginePlaying = true;
                }
            }
        },
        computed: {

        }
    });
}
