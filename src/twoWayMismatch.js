class TwoWayMismatch {
    constructor() {
        this.peaks = [];
        this.initial = 0.05;
        this.final = 0.004;
        this.freqDecay = 1000.0;
    }

    detectPeaks(spectrum) {
        this.peaks = [];
        var mag = spectrum;
        for(var ix = 1; ix < mag.length - 1; ix++) {
            if(mag[ix] > mag[ix - 1] && mag[ix] > mag[ix + 1]) {
                var freq = ix * 44100 / mag.length;
                var threshold = this.threshold(freq);
                if(mag[ix] > threshold) {
                    this.peaks.push(freq);
                }
            }
        }
    }

    process(samples, spectrum) {
        this.detectPeaks(spectrum);
    }

    threshold(freq) {
        return (this.initial - this.final) * Math.exp(- freq / this.freqDecay) + this.final;
    }
}

module.exports['TwoWayMismatch'] = TwoWayMismatch;
