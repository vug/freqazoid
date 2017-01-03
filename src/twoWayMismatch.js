class PeakDetector {
    constructor() {
        this.peaks = [];
        this.initial = 0.05;
        this.final = 0.004;
        this.freqDecay = 1000.0;
    }

    detectPeaks(spectrum) {
        this.peaks = [];
        var mag = spectrum;
        var dFreq = 44100 / mag.length;
        for(var ix = 1; ix < mag.length - 1; ix++) {
            if(mag[ix] > mag[ix - 1] && mag[ix] > mag[ix + 1]) {
                var freq = ix * dFreq;
                var threshold = this.threshold(freq);
                if(mag[ix] > threshold) {
                    var [dix, vertexMag] = this.parabolaVertex(mag[ix - 1], mag[ix], mag[ix + 1]);
                    this.peaks.push([freq + dix * dFreq, vertexMag]);
                }
            }
        }
    }

    /**
     * Compute x-coordinate of the peak of a parabola (vertex) that passes through these three points:
     * (-1, y1), (-1, y2), (-1, y2)
     *
     * Parabola equation: y = a * x ** 2 + b * x + c
     *
     * x == -1 => a - b + c = y1
     * x ==  0 => c = y2
     * x ==  1 => a + b + c = y3
     *
     * a - b = y1 - y2
     * a + b = y3 - y2
     *
     * which gives:
     *
     * a = 0.5 * (y1 + y3) - y2
     * b = 0.5 * (y3 - y1)
     * c = y2
     *
     * therefore the coordinates of the vertex is: [x, y] = [- b / (2 * a), - b ** 2 / (4 * a) + c]
     *
     * @param {float} y1
     * @param {float} y2
     * @param {float} y3
     */
    parabolaVertex(y1, y2, y3) {
        var a = 0.5 * (y1 + y3) - y2;
        var b = 0.5 * (y3 - y1);
        var x = - 0.5 * b / a;
        var y = - 0.25 * b * b / a + y2;
        return [x, y];
    }

    threshold(freq) {
        return (this.initial - this.final) * Math.exp(- freq / this.freqDecay) + this.final;
    }
}

class TwoWayMismatch {
    constructor() {
        this.peakDetector = new PeakDetector();
    }

    process(samples, spectrum) {
        this.peakDetector.detectPeaks(spectrum);
    }
}

module.exports['TwoWayMismatch'] = TwoWayMismatch;
