var utils = require('./utils');

class AnalysisBuffer {
    constructor(context, hopSize, numHops) {
        this.context = context;
        this.hopSize = hopSize;
        this.numHops = numHops;

        this.frame = new Float32Array(this.hopSize * this.numHops);
        this.fft = new FFT(this.frame.length, 44100);
        this.peaks = [];

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

            this.peaks = [];
            var mag = this.fft.spectrum;
            for(var ix = 1; ix < mag.length - 1; ix++) {
                if(mag[ix] > mag[ix - 1] && mag[ix] > mag[ix + 1] && mag[ix] > 0.03) {
                    this.peaks.push(ix);
                }
            }
        };
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

class AudioVisualization {
    constructor(analyser, elemId) {
        this.analyser = analyser;
        this.elemId = elemId;

        this.div = document.getElementById(this.elemId);
        this.div.style.setProperty('outline', 'solid 1px');
        this.div.style.setProperty('display', 'inline-block');
        this.canvas = document.createElement('canvas');
        this.resize();
        this.div.appendChild(this.canvas);
        this.context = this.canvas.getContext('2d');

        this.buffer = new Float32Array(this.analyser.frequencyBinCount);

        this.animate();
    }

    render() {

    }

    resize() {
        this.canvas.width = this.div.clientWidth;
        this.canvas.height = this.div.clientHeight;
    }

    animate() {
        this.render();

        requestAnimationFrame(this.animate.bind(this));
    }
}

class Oscilloscope extends AudioVisualization {
    render() {
        this.buffer = this.analyser.frame;
        var ctx = this.context;
        var width = this.canvas.width;
        var height = this.canvas.height;
        var halfHeight = height * 0.5;

        var slices = utils.slicesOfArray(this.buffer, width);
        var sliceMaxes = slices.map((slice) => utils.absoluteMax(slice));


        ctx.clearRect(0, 0, width, height);
        ctx.beginPath();
        if(false) {
            // A line for every sample
            ctx.moveTo(0, this.buffer[0]);
            for (var x=1; x<this.buffer.length; x++) {
                var sample = this.buffer[x];
                var y = sample * halfHeight + halfHeight;
                ctx.lineTo(x / this.buffer.length * width, y);
            }
        }
        else {
            // Absolute Max
            ctx.moveTo(0, sliceMaxes[0]);
            for (var x=1; x<width; x++) {
                var sample = sliceMaxes[x];
                var y = sample * halfHeight + halfHeight;
                ctx.lineTo(x, y);
            }
        }
        ctx.stroke();
    }
}

class Spectroscope extends AudioVisualization {
    render() {
        var spectrum = this.analyser.fft.spectrum.slice(0, this.analyser.fft.spectrum.length / 2);
        var ctx = this.context;
        var width = this.canvas.width;
        var height = this.canvas.height;

        ctx.clearRect(0, 0, width, height);
        ctx.strokeStyle = 'black';
        ctx.beginPath();
        ctx.moveTo(0, 0);
        for (var ix=0; ix<spectrum.length; ix++) {
//            var y = height - spectrum[ix] * height;
            var y = - Math.log(spectrum[ix]) * height * 0.1;
            var x = ix / spectrum.length * width;
            ctx.lineTo(x, y);
        }
        ctx.stroke();

        ctx.strokeStyle = 'green';
        for (var ix=0; ix<this.analyser.peaks.length; ix++) {
            var peak = this.analyser.peaks[ix];
            ctx.beginPath();
            var x = peak / spectrum.length * width;
            ctx.moveTo(x, 0);
            ctx.lineTo(x, height);
            ctx.stroke();
        }


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
