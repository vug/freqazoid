var utils = require('./utils');

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
    constructor(analyser, twm, elemId) {
        super(analyser, elemId);
        this.twm = twm;
    }

    render() {
        var spectrum = this.analyser.fft.spectrum.slice(0, this.analyser.fft.spectrum.length / 2);
        var peaks = this.twm.peaks;
        var ctx = this.context;
        var width = this.canvas.width;
        var height = this.canvas.height;

        this.renderSpectrum(ctx, spectrum, width, height);
        if(typeof(this.twm) !== "undefined") {
            this.renderPeaks(ctx, peaks, spectrum, width, height);
            this.renderThreshold(ctx, spectrum, width, height);
        }
    }

    renderSpectrum(ctx, spectrum, width, height) {
        ctx.clearRect(0, 0, width, height);
        ctx.strokeStyle = 'black';
        ctx.beginPath();
        ctx.moveTo(0, 0);
        for (var ix=0; ix<spectrum.length; ix++) {
            // var y = height - spectrum[ix] * height;
            var y = - Math.log(spectrum[ix]) * height * 0.1;
            var x = ix / spectrum.length * width;
            ctx.lineTo(x, y);
        }
        ctx.stroke();
    }

    renderPeaks(ctx, peaks, spectrum, width, height) {
        ctx.strokeStyle = 'green';
        for (var ix=0; ix<peaks.length; ix++) {
            var peakFreq = peaks[ix];
            ctx.beginPath();
            var x = peakFreq / 22050 * width;
            ctx.moveTo(x, 0);
            ctx.lineTo(x, height);
            ctx.stroke();
        }
    }

    renderThreshold(ctx, spectrum, width, height) {
        ctx.strokeStyle = 'orange';
        ctx.beginPath();
        for (var ix = 0; ix < spectrum.length; ix++) {
            var freq = ix * 22050 / spectrum.length;
            var threshold = this.twm.threshold(freq);
            // var y = height - threshold * height;
            var y = - Math.log(threshold) * height * 0.1;
            var x = ix / spectrum.length * width;
            ctx.lineTo(x, y);
        }
        ctx.stroke();
    }
}

module.exports['Oscilloscope'] = Oscilloscope;
module.exports['Spectroscope'] = Spectroscope;
