/**
 * Cut an array into numSlices arrays with mostly equal length
 * @param {Array} arr An array of numbers
 * @param numSlices Number of slice arrays
 * @returns {Array}
 */
function slicesOfArray(arr, numSlices) {
	var q = arr.length / numSlices;
	var t = 0;
	var slices = [];

	for(var i=0; i<numSlices; i++) {
		var ixp = Math.floor(t);
		var ix = Math.floor(t + q);
		var slice = arr.slice(ixp, ix);
		slices.push(slice);
		t += q;
	}
	return slices;
}

/**
 * The sum of all items in an array
 * @param {Array} arr
 * @returns {Number} Sum
 */
function sum(arr) {
    return arr.reduce((prev, curr) => prev + curr);
}

/**
 * The item in an array that has the largest absolute value
 * @param {Array} arr
 * @returns {Number}
 */
function absoluteMax(arr) {
    var ix = -1;
    var absMax = -Infinity;
    for (var k = 0; k < arr.length; k++) {
        var abs = Math.abs(arr[k]);
        if (abs > absMax) {
            ix = k;
            absMax = abs;
        }
    }
    return arr[ix];
}

var windowFunctions = {
    'RECTANGLE': function() {

    },
    'HANN': function(arr) {
        for(var n = 0; n < arr.length; n++) {
            arr[n] = 0.5 * (1.0 - Math.cos(2.0 * Math.PI * n / (arr.length - 1))) * arr[n];
        }
    },
    'HAMMING': function(arr) {
        for(var n = 0; n < arr.length; n++) {
            arr[n] = (0.53836 - 0.46164 * Math.cos(2.0 * Math.PI * n / (arr.length - 1))) * arr[n];
        }
    },
    'BLACKMANN': function(arr) {
        for(var n = 0; n < arr.length; n++) {
            arr[n] = (0.42 - 0.5 * Math.cos(2.0 * Math.PI * n / (arr.length - 1)) + 0.08 * Math.cos(4.0 * Math.PI * n / (arr.length - 1))) * arr[n];
        }
    }
};

var noteNames = [
    "A0", "A#0", "B0",
    "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
    "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
    "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
    "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
    "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
    "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6",
    "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7",
    "C8", "C#8", "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "B8",
    "C9", "C#9", "D9", "D#9", "E9", "F9", "F#9", "G9", "G#9", "A9", "A#9", "B9"
];

function freqToNoteName(freq) {
    var freqA0 = 27.5;
    if(freq < freqA0) return 'below A0';
    if(freq > 16740) return 'above C10'
    var semitones = 12.0 * Math.log2(freq / freqA0);
    var ix = Math.floor(semitones);
    var cents = (semitones - ix) * 100;
    if (cents > 50) {
        ix += 1;
        cents -= 100;
    }
    return noteNames[ix] + ' + ' + cents.toFixed() + ' cents';
}

module.exports['slicesOfArray'] = slicesOfArray;
module.exports['sum'] = sum;
module.exports['absoluteMax'] = absoluteMax;
module.exports['windowFunctions'] = windowFunctions;
module.exports['freqToNoteName'] = freqToNoteName;
