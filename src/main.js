var visualizations = require('./visualizations');
var TwoWayMismatch = require('./twoWayMismatch').TwoWayMismatch;
var Oscilloscope = visualizations.Oscilloscope;
var Spectroscope = visualizations.Spectroscope;
var windowFunctions = require('./utils').windowFunctions;

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
        this.windowFunction = 'HANN';
        this.frame = null;
        this.windowed = null;
        this.fft = null;
        this.node = null;
        this.processes = [];

        this.createProperties();
    }

    createProperties() {
        if (this.node) {
            this.node.onaudioprocess = null;
        }
        this.frame = new Float32Array(this.hopSize * this.numHops);
        this.fft = new FFT(this.frame.length, 44100);
        this.windowed = new Float32Array(this.frame.length);
        this.node = this.context.createScriptProcessor(this.hopSize, 1, 1);
        this.node.onaudioprocess = this.processAudioCallback.bind(this);
    }

    setBufferSize(hopSize, numHops) {
        this.hopSize = hopSize;
        this.numHops = numHops;
        this.createProperties();
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

        this.windowed.set(this.frame);
        windowFunctions[this.windowFunction](this.windowed);
        this.fft.forward(this.windowed);

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
    constructor() {
        this.context = new window.AudioContext();
        this.micInput = null;

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
        this.connectAnalysisBuffer();
        this.gain.connect(this.context.destination);
    }

    connectAnalysisBuffer() {
        this.oscVolume.connect(this.analysisBuffer.node);
        if (this.micInput) { this.micInput.connect(this.analysisBuffer.node); }
        this.analysisBuffer.node.connect(this.gain);
        this.analysisBuffer.node.connect(this.analyser);
    }

    disconnectAnalysisBuffer() {
        this.analysisBuffer.node.disconnect();
        this.oscVolume.disconnect();
        if (this.micInput) { this.micInput.disconnect(); }
    }

    setBufferSize(hopSize, numHops) {
        this.disconnectAnalysisBuffer();
        this.analysisBuffer.setBufferSize(hopSize, numHops);
        this.connectAnalysisBuffer();
    }

    setMicrophone(micStream) {
        if (this.micInput) { this.micInput.disconnect(); }
        this.micInput = this.context.createMediaStreamSource(micStream);
        this.micInput.connect(this.analysisBuffer.node);
    }

    requestMicrophone() {
        navigator.mediaDevices.getUserMedia({audio: true, video: false})
            .then(
                micStream => {
                    this.setMicrophone(micStream);
                }
            )
            .catch(
                error => {
                    console.log('MIC REQUEST ERROR', error);
                    return null;
                }
            );
    }
}

class Freqazoid {
    constructor() {
        var ae = new AudioEngine();
        var twm = new TwoWayMismatch();
        ae.analysisBuffer.registerProcess(twm.process.bind(twm));
        var osc = new Oscilloscope(ae.analysisBuffer, 'osc');
        var spc = new Spectroscope(ae.analysisBuffer, twm, 'spc');
        var visualizations = [osc, spc];
        for (let vis of visualizations) {
            window.addEventListener('resize', vis.resize.bind(vis), false);
            vis.animate();
        }
        this.vue =  new Vue({
            el: '#app',
            data: {
                ae: ae,
                twm: twm,
                enginePlaying: true,
                hopSize: ae.analysisBuffer.hopSize,
                numHops: ae.analysisBuffer.numHops,
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
                },
                requestMicrophone: function() {
                    this.ae.requestMicrophone();
                }
            },
            computed: {
                windowSize: function() {
                    return this.hopSize * this.numHops;
                }
            },
            watch: {
                hopSize: function(newHopSize) {
                    this.ae.setBufferSize(newHopSize, this.ae.analysisBuffer.numHops);
                },
                numHops: function(newNumHops) {
                    this.ae.setBufferSize(this.ae.analysisBuffer.hopSize, newNumHops);
                }
            }
        });


        ae.requestMicrophone();
    }
}

freqazoid = new Freqazoid();
