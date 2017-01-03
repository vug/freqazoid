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
    /**
     * If the analysis buffer is shorter than the canvas width, draw a line for each sample,
     * Otherwise, split the buffer into slices (number of slices = width), calculate the maximum and minimum sample
     * value at each slice and draw one line for maximum samples and another one for minimum samples.
     */
    render() {
        this.buffer = this.analyser.frame;
        var ctx = this.context;

        ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        if(this.buffer.length <= this.canvas.width) {
            this.plotShortArray(ctx, this.buffer);
        }
        else {
            var slices = utils.slicesOfArray(this.buffer, this.canvas.width);
            // var sliceAbsMaxes = slices.map((slice) => utils.absoluteMax(slice));
            var slcMaxs = slices.map(slice => Math.max(...slice));
            var slcMins = slices.map(slice => Math.min(...slice));

            this.plotArray(ctx, slcMaxs);
            this.plotArray(ctx, slcMins);
        }
    }

    plotShortArray(ctx, arr) {
        var halfHeight = this.canvas.height * 0.5;

        ctx.beginPath();
        ctx.moveTo(0, arr[0] * halfHeight + halfHeight);
        for (var ix = 1; ix < arr.length; ix++) {
            var sample = arr[ix];
            var x = ix / arr.length * this.canvas.width;
            var y = sample * halfHeight + halfHeight;
            ctx.lineTo(x, y);
        }
        ctx.stroke();
    }

    plotArray(ctx, arr) {
        var halfHeight = this.canvas.height * 0.5;

        ctx.beginPath();
        ctx.moveTo(0, arr[0] * halfHeight + halfHeight);
        for (var x = 1; x < this.canvas.width; x++) {
            var sample = arr[x];
            var y = sample * halfHeight + halfHeight;
            ctx.lineTo(x, y);
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
            var [peakFreq, peakMag] = peaks[ix];
            ctx.beginPath();
            var x = peakFreq / 22050 * width;
            var y = - Math.log(peakMag) * height * 0.1;
            ctx.arc(x, y, 5, 0, 2.0 * Math.PI, false);
            ctx.moveTo(x, height);
            ctx.lineTo(x, y);
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
