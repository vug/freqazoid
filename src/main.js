var visualizations = require('./visualizations');
var Oscilloscope = visualizations.Oscilloscope;
var Spectroscope = visualizations.Spectroscope;

class TwoWayMismatch {
    constructor() {

    }

    detectPeaks(spectrum) {
        var peaks = [];
        var mag = spectrum;
        for(var ix = 1; ix < mag.length - 1; ix++) {
            if(mag[ix] > mag[ix - 1] && mag[ix] > mag[ix + 1] && mag[ix] > 0.03) {
                peaks.push(ix);
            }
        }
        return peaks;
    }

    process(samples, spectrum) {
        return this.detectPeaks(spectrum);
    }

    threshold(freq) {
        var t0 = 1.0;
        var freqDecay = 1000.0;
        return t0 * Math.exp(- freq / freqDecay);
    }
}

class AnalysisBuffer {
    constructor(context, hopSize, numHops) {
        this.context = context;
        this.hopSize = hopSize;
        this.numHops = numHops;

        this.processes = [];
        this.peaks = [];
        this.frame = new Float32Array(this.hopSize * this.numHops);
        this.fft = new FFT(this.frame.length, 44100);

        this.node = this.context.createScriptProcessor(this.hopSize, 1, 1);
        this.node.onaudioprocess = (audioProcessingEvent) => {
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
                this.peaks = process(this.frame, this.fft.spectrum);
            }
        };
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
    var spc = new Spectroscope(ae.analysisBuffer, 'spc');
    var visualizations = [osc, spc];
    for (let vis of visualizations) {
        window.addEventListener('resize', vis.resize.bind(vis), false);
    }
    var vue =  new Vue({
        el: '#app',
        data: {
            ae: ae,
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
