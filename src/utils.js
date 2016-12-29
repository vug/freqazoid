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

module.exports['slicesOfArray'] = slicesOfArray;
module.exports['sum'] = sum;
module.exports['absoluteMax'] = absoluteMax;
