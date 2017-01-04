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
        var dFreq = 22050 / mag.length;
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
        this.fundamentalFrequency = 0.0;
        this.totalError = 0.0;
        this.peakDetector = new PeakDetector();
        this.pmP = 0.5;
        this.pmQ = 1.4;
        this.pmR = 0.5;
        this.mpP = 0.5;
        this.mpQ = 1.4;
        this.mpR = 0.5;
        this.rho = 0.33;

        this.errors1 = [];
        this.ftrials1 = [];
        this.errors2 = [];
        this.ftrials2 = [];

        this.errorThreshold = 7.0;

        this.f0Min = 220;
        this.f0Max = 880;
    }

    process(samples, spectrum) {
        this.peakDetector.detectPeaks(spectrum);
        this.computeFundamentalFrequency();
    }

    computeFundamentalFrequency() {
        var peaks = this.peakDetector.peaks;
        if(peaks.length === 0) {
            this.fundamentalFrequency = null;
            return;
        }

		// divide the frequency range to equal tempered semitone steps.
		var log102 = 1.0 / Math.log10(2.0);
		var nFreqs = Math.floor(12.0 * log102 * Math.log10(this.f0Max / this.f0Min));

		this.errors1 = [];
		this.ftrials1 = [];

		for (var i = 0; i < nFreqs; i++) {
			this.ftrials1[i] = this.f0Min * Math.pow(2.0, i / 12.0);
			this.errors1[i] = this.calculateTotalError(this.ftrials1[i], peaks);
		}

		var minErrorVal = Number.POSITIVE_INFINITY;
		var minErrorIdx = 0;
		for (let i = 0; i < nFreqs; i++) {
			if(this.errors1[i] < minErrorVal) {
				minErrorVal = this.errors1[i];
				minErrorIdx = i;
			}
		}

		// Second pass
        var f0Min2 = this.ftrials1[minErrorIdx] * Math.pow(2.0, -1.0 / 12.0);
        var f0Max2 = this.ftrials1[minErrorIdx] * Math.pow(2.0, 1.0 / 12.0);

        log102 = 1.0 / Math.log10(2.0);
        // nFreqs = Math.floor(12.0 * log102 * Math.log10(f0Max2 /  f0Min2));
        nFreqs = 20;

		this.errors2 = Array(nFreqs);
		this.ftrials2 = Array(nFreqs);

        for (let i = 0; i < nFreqs; i++) {
            // ftrials[i] = f0Min*Math.pow(2, (double)i/10);
            this.ftrials2[i] = i * (f0Max2 - f0Min2) / nFreqs + f0Min2;
            this.errors2[i] = this.calculateTotalError(this.ftrials2[i], this.peaks);
        }

		var minErrorVal = Number.POSITIVE_INFINITY;
		var minErrorIdx = 0;
		for (let i = 0; i < nFreqs; i++) {
			if(this.errors2[i] < minErrorVal) {
				minErrorVal = this.errors2[i];
				minErrorIdx = i;
			}
		}

        this.fundamentalFrequency = this.ftrials2[minErrorIdx];
        this.totalError = minErrorVal;
    }

    calculateTotalError(f0Trial) {
        var peaks = this.peakDetector.peaks;

		var predictedToMeasuredError = 0.0;
		var measuredToPredictedError = 0.0;
		var totalError;

		var measuredPartials = []; // of length peaks
		var measuredAmplitudes = [];

		// determine maximum measured amplitude and partial frequency
		var ampMax = 0.0;
		var freqMax = 0.0;
		for (var i = 0; i < peaks.length; i++) {
            var freq = peaks[i][0];
            var mag = peaks[i][1];
			measuredPartials[i] = freq;
			measuredAmplitudes[i] = mag;

			if( measuredAmplitudes[i] > ampMax ) {
				ampMax = measuredPartials[i];
			}

			if( measuredPartials[i] > freqMax ) {
				freqMax = measuredPartials[i];
			}
		}

		// calculate predicted frequencies of harmonics
		var nPredictedHarmonics = Math.ceil(freqMax / f0Trial);
		var predictedHarmonics = new Float32Array(nPredictedHarmonics);
		var predictedAmplitudes = new Float32Array(nPredictedHarmonics);
		for (let n = 0; n < nPredictedHarmonics; n++) {
			predictedHarmonics[n] = f0Trial * (n + 1);
		}

		// predicted to measured
		var differences = new Float32Array(nPredictedHarmonics);
		for (let n = 0; n < predictedHarmonics.length; n++) {
			differences[n] = Number.POSITIVE_INFINITY;
			for (let k = 0; k < measuredPartials.length; k++) {
				let diff = Math.abs(predictedHarmonics[n] - measuredPartials[k]);
				if(diff < differences[n]) {
					differences[n] = diff;
					predictedAmplitudes[n] = measuredAmplitudes[k];
				}
			}
			predictedToMeasuredError += this.computePredictedToMeasuredError(differences[n], predictedHarmonics[n], predictedAmplitudes[n], ampMax);
		}

        // measured to predicted
		differences = new Float32Array(peaks.length);
		for (let k = 0; k < differences.length; k++) {
			differences[k] = Number.POSITIVE_INFINITY;
			for (let n = 0; n < predictedHarmonics.length; n++) {
				let diff = Math.abs(predictedHarmonics[n] - peaks[k][0]);
				if(diff < differences[k]) {
					differences[k] = diff;
				}
			}
			measuredToPredictedError += this.computeMeasuredToPredictedError(differences[k], peaks[k][0], peaks[k][1], ampMax);
		}

		totalError = predictedToMeasuredError / predictedHarmonics.length + this.rho * measuredToPredictedError / peaks.length;
        return totalError;
    }

	computePredictedToMeasuredError(deltaFreqN, freqN, ampN, ampMax) {
		var error = deltaFreqN * Math.pow(freqN, -this.pmP) + (ampN / ampMax) * (this.pmQ * deltaFreqN * Math.pow(freqN, -this.pmP) - this.pmR);
		// freqN === 0 => error === Infinity
		return error;
	}

	computeMeasuredToPredictedError(deltaFreqK, freqK, ampK, ampMax) {
		var error = deltaFreqK * Math.pow(freqK, -this.mpP)+(ampK / ampMax) * (this.mpQ * deltaFreqK * Math.pow(freqK, -this.mpP) - this.mpR);
		return error;
	}
}

module.exports['TwoWayMismatch'] = TwoWayMismatch;
