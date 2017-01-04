"use strict";

var _slicedToArray = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

(function e(t, n, r) {
    function s(o, u) {
        if (!n[o]) {
            if (!t[o]) {
                var a = typeof require == "function" && require;if (!u && a) return a(o, !0);if (i) return i(o, !0);var f = new Error("Cannot find module '" + o + "'");throw f.code = "MODULE_NOT_FOUND", f;
            }var l = n[o] = { exports: {} };t[o][0].call(l.exports, function (e) {
                var n = t[o][1][e];return s(n ? n : e);
            }, l, l.exports, e, t, n, r);
        }return n[o].exports;
    }var i = typeof require == "function" && require;for (var o = 0; o < r.length; o++) {
        s(r[o]);
    }return s;
})({ 1: [function (require, module, exports) {
        // shim for using process in browser
        var process = module.exports = {};

        // cached from whatever global is present so that test runners that stub it
        // don't break things.  But we need to wrap it in a try catch in case it is
        // wrapped in strict mode code which doesn't define any globals.  It's inside a
        // function because try/catches deoptimize in certain engines.

        var cachedSetTimeout;
        var cachedClearTimeout;

        (function () {
            try {
                cachedSetTimeout = setTimeout;
            } catch (e) {
                cachedSetTimeout = function cachedSetTimeout() {
                    throw new Error('setTimeout is not defined');
                };
            }
            try {
                cachedClearTimeout = clearTimeout;
            } catch (e) {
                cachedClearTimeout = function cachedClearTimeout() {
                    throw new Error('clearTimeout is not defined');
                };
            }
        })();
        function runTimeout(fun) {
            if (cachedSetTimeout === setTimeout) {
                //normal enviroments in sane situations
                return setTimeout(fun, 0);
            }
            try {
                // when when somebody has screwed with setTimeout but no I.E. maddness
                return cachedSetTimeout(fun, 0);
            } catch (e) {
                try {
                    // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
                    return cachedSetTimeout.call(null, fun, 0);
                } catch (e) {
                    // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
                    return cachedSetTimeout.call(this, fun, 0);
                }
            }
        }
        function runClearTimeout(marker) {
            if (cachedClearTimeout === clearTimeout) {
                //normal enviroments in sane situations
                return clearTimeout(marker);
            }
            try {
                // when when somebody has screwed with setTimeout but no I.E. maddness
                return cachedClearTimeout(marker);
            } catch (e) {
                try {
                    // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
                    return cachedClearTimeout.call(null, marker);
                } catch (e) {
                    // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
                    // Some versions of I.E. have different rules for clearTimeout vs setTimeout
                    return cachedClearTimeout.call(this, marker);
                }
            }
        }
        var queue = [];
        var draining = false;
        var currentQueue;
        var queueIndex = -1;

        function cleanUpNextTick() {
            if (!draining || !currentQueue) {
                return;
            }
            draining = false;
            if (currentQueue.length) {
                queue = currentQueue.concat(queue);
            } else {
                queueIndex = -1;
            }
            if (queue.length) {
                drainQueue();
            }
        }

        function drainQueue() {
            if (draining) {
                return;
            }
            var timeout = runTimeout(cleanUpNextTick);
            draining = true;

            var len = queue.length;
            while (len) {
                currentQueue = queue;
                queue = [];
                while (++queueIndex < len) {
                    if (currentQueue) {
                        currentQueue[queueIndex].run();
                    }
                }
                queueIndex = -1;
                len = queue.length;
            }
            currentQueue = null;
            draining = false;
            runClearTimeout(timeout);
        }

        process.nextTick = function (fun) {
            var args = new Array(arguments.length - 1);
            if (arguments.length > 1) {
                for (var i = 1; i < arguments.length; i++) {
                    args[i - 1] = arguments[i];
                }
            }
            queue.push(new Item(fun, args));
            if (queue.length === 1 && !draining) {
                runTimeout(drainQueue);
            }
        };

        // v8 likes predictible objects
        function Item(fun, array) {
            this.fun = fun;
            this.array = array;
        }
        Item.prototype.run = function () {
            this.fun.apply(null, this.array);
        };
        process.title = 'browser';
        process.browser = true;
        process.env = {};
        process.argv = [];
        process.version = ''; // empty string to avoid regexp issues
        process.versions = {};

        function noop() {}

        process.on = noop;
        process.addListener = noop;
        process.once = noop;
        process.off = noop;
        process.removeListener = noop;
        process.removeAllListeners = noop;
        process.emit = noop;

        process.binding = function (name) {
            throw new Error('process.binding is not supported');
        };

        process.cwd = function () {
            return '/';
        };
        process.chdir = function (dir) {
            throw new Error('process.chdir is not supported');
        };
        process.umask = function () {
            return 0;
        };
    }, {}], 2: [function (require, module, exports) {
        var windowFunctions = require('./utils').windowFunctions;

        /**
         * An audio buffer of size hopSize * numHops. At each iteration it takes hopSize new samples, puts them at the end of
         * the frame after sliding old samples towards the left of the frame (while getting rid of the earliest hop in the
         * frame)
         * Assume that each place represents a hop of data, and the array is one frame, this is its evolution:
         * [0 0 0 0] -> [0 0 0 1] -> [0 0 1 2] -> [0 1 2 3] -> [1 2 3 4] -> [2 3 4 5] -> ...
         */

        var AnalysisBuffer = function () {
            /**
             * Create an AnalysisBuffer.
             * @param {AudioContext} context - Web Audio API audio context
             * @param {int} hopSize - an audio process event is triggered every hopSize samples
             * @param {int} numHops - the window size in number of hops
             */
            function AnalysisBuffer(context, hopSize, numHops) {
                _classCallCheck(this, AnalysisBuffer);

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

            _createClass(AnalysisBuffer, [{
                key: "createProperties",
                value: function createProperties() {
                    if (this.node) {
                        this.node.onaudioprocess = null;
                    }
                    this.frame = new Float32Array(this.hopSize * this.numHops);
                    this.fft = new FFT(this.frame.length, 44100);
                    this.windowed = new Float32Array(this.frame.length);
                    this.node = this.context.createScriptProcessor(this.hopSize, 1, 1);
                    this.node.onaudioprocess = this.processAudioCallback.bind(this);
                }
            }, {
                key: "setBufferSize",
                value: function setBufferSize(hopSize, numHops) {
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

            }, {
                key: "processAudioCallback",
                value: function processAudioCallback(audioProcessingEvent) {
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

                    var _iteratorNormalCompletion = true;
                    var _didIteratorError = false;
                    var _iteratorError = undefined;

                    try {
                        for (var _iterator = this.processes[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
                            var process = _step.value;

                            process(this.frame, this.fft.spectrum);
                        }
                    } catch (err) {
                        _didIteratorError = true;
                        _iteratorError = err;
                    } finally {
                        try {
                            if (!_iteratorNormalCompletion && _iterator.return) {
                                _iterator.return();
                            }
                        } finally {
                            if (_didIteratorError) {
                                throw _iteratorError;
                            }
                        }
                    }
                }
            }, {
                key: "registerProcess",
                value: function registerProcess(processFunc) {
                    this.processes.push(processFunc);
                }
            }]);

            return AnalysisBuffer;
        }();

        var AudioEngine = function () {
            function AudioEngine() {
                _classCallCheck(this, AudioEngine);

                window.AudioContext = window.AudioContext || window.webkitAudioContext;
                this.context = new window.AudioContext();
                this.micInput = null;

                this.oscillator = this.context.createOscillator();
                this.oscVolume = this.context.createGain();
                this.gain = this.context.createGain();
                this.analyser = this.context.createAnalyser();
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

            _createClass(AudioEngine, [{
                key: "connectAnalysisBuffer",
                value: function connectAnalysisBuffer() {
                    this.oscVolume.connect(this.analysisBuffer.node);
                    if (this.micInput) {
                        this.micInput.connect(this.analysisBuffer.node);
                    }
                    this.analysisBuffer.node.connect(this.gain);
                    this.analysisBuffer.node.connect(this.analyser);
                }
            }, {
                key: "disconnectAnalysisBuffer",
                value: function disconnectAnalysisBuffer() {
                    this.analysisBuffer.node.disconnect();
                    this.oscVolume.disconnect();
                    if (this.micInput) {
                        this.micInput.disconnect();
                    }
                }
            }, {
                key: "setBufferSize",
                value: function setBufferSize(hopSize, numHops) {
                    this.disconnectAnalysisBuffer();
                    this.analysisBuffer.setBufferSize(hopSize, numHops);
                    this.connectAnalysisBuffer();
                }
            }, {
                key: "setMicrophone",
                value: function setMicrophone(micStream) {
                    if (this.micInput) {
                        this.micInput.disconnect();
                    }
                    this.micInput = this.context.createMediaStreamSource(micStream);
                    this.micInput.connect(this.analysisBuffer.node);
                }
            }, {
                key: "requestMicrophone",
                value: function requestMicrophone() {
                    var _this = this;

                    navigator.mediaDevices.getUserMedia({ audio: true, video: false }).then(function (micStream) {
                        _this.setMicrophone(micStream);
                    }).catch(function (error) {
                        console.log('MIC REQUEST ERROR', error);
                        return null;
                    });
                }
            }]);

            return AudioEngine;
        }();

        module.exports['AnalysisBuffer'] = AnalysisBuffer;
        module.exports['AudioEngine'] = AudioEngine;
    }, { "./utils": 6 }], 3: [function (require, module, exports) {
        var audio = require('./audio');
        var visualizations = require('./visualizations');
        var TwoWayMismatch = require('./twoWayMismatch').TwoWayMismatch;
        var Oscilloscope = visualizations.Oscilloscope;
        var Spectroscope = visualizations.Spectroscope;
        var freqToNoteName = require('./utils').freqToNoteName;

        var Freqazoid = function Freqazoid() {
            _classCallCheck(this, Freqazoid);

            var ae = new audio.AudioEngine();
            var twm = new TwoWayMismatch();
            ae.analysisBuffer.registerProcess(twm.process.bind(twm));
            var osc = new Oscilloscope(ae.analysisBuffer, 'osc');
            var spc = new Spectroscope(ae.analysisBuffer, twm, 'spc');
            var visualizations = [osc, spc];
            var _iteratorNormalCompletion2 = true;
            var _didIteratorError2 = false;
            var _iteratorError2 = undefined;

            try {
                for (var _iterator2 = visualizations[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
                    var vis = _step2.value;

                    window.addEventListener('resize', vis.resize.bind(vis), false);
                    vis.animate();
                }
            } catch (err) {
                _didIteratorError2 = true;
                _iteratorError2 = err;
            } finally {
                try {
                    if (!_iteratorNormalCompletion2 && _iterator2.return) {
                        _iterator2.return();
                    }
                } finally {
                    if (_didIteratorError2) {
                        throw _iteratorError2;
                    }
                }
            }

            this.vue = new Vue({
                el: '#app',
                data: {
                    ae: ae,
                    twm: twm,
                    enginePlaying: true,
                    hopSize: ae.analysisBuffer.hopSize,
                    numHops: ae.analysisBuffer.numHops
                },
                methods: {
                    pauseAudioEngine: function pauseAudioEngine() {
                        if (this.ae.context.state === "running") {
                            this.ae.context.suspend();
                            this.enginePlaying = false;
                        } else if (this.ae.context.state === "suspended") {
                            this.ae.context.resume();
                            this.enginePlaying = true;
                        }
                    },
                    requestMicrophone: function requestMicrophone() {
                        this.ae.requestMicrophone();
                    },
                    noteName: function noteName(f) {
                        return freqToNoteName(f);
                    }
                },
                computed: {
                    windowSize: function windowSize() {
                        return this.hopSize * this.numHops;
                    }
                },
                watch: {
                    hopSize: function hopSize(newHopSize) {
                        this.ae.setBufferSize(newHopSize, this.ae.analysisBuffer.numHops);
                    },
                    numHops: function numHops(newNumHops) {
                        this.ae.setBufferSize(this.ae.analysisBuffer.hopSize, newNumHops);
                    }
                }
            });

            ae.requestMicrophone();
        };

        module.exports['Freqazoid'] = Freqazoid;
    }, { "./audio": 2, "./twoWayMismatch": 5, "./utils": 6, "./visualizations": 7 }], 4: [function (require, module, exports) {
        var Freqazoid = require('./freqazoid').Freqazoid;

        freqazoid = new Freqazoid();
    }, { "./freqazoid": 3 }], 5: [function (require, module, exports) {
        (function (process) {
            var PeakDetector = function () {
                function PeakDetector() {
                    _classCallCheck(this, PeakDetector);

                    this.peaks = [];
                    this.initial = 0.05;
                    this.final = 0.004;
                    this.freqDecay = 1000.0;
                }

                _createClass(PeakDetector, [{
                    key: "detectPeaks",
                    value: function detectPeaks(spectrum) {
                        this.peaks = [];
                        var mag = spectrum;
                        var dFreq = 22050 / mag.length;
                        for (var ix = 1; ix < mag.length - 1; ix++) {
                            if (mag[ix] > mag[ix - 1] && mag[ix] > mag[ix + 1]) {
                                var freq = ix * dFreq;
                                var threshold = this.threshold(freq);
                                if (mag[ix] > threshold) {
                                    var _parabolaVertex = this.parabolaVertex(mag[ix - 1], mag[ix], mag[ix + 1]);

                                    var _parabolaVertex2 = _slicedToArray(_parabolaVertex, 2);

                                    var dix = _parabolaVertex2[0];
                                    var vertexMag = _parabolaVertex2[1];

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

                }, {
                    key: "parabolaVertex",
                    value: function parabolaVertex(y1, y2, y3) {
                        var a = 0.5 * (y1 + y3) - y2;
                        var b = 0.5 * (y3 - y1);
                        var x = -0.5 * b / a;
                        var y = -0.25 * b * b / a + y2;
                        return [x, y];
                    }
                }, {
                    key: "threshold",
                    value: function threshold(freq) {
                        return (this.initial - this.final) * Math.exp(-freq / this.freqDecay) + this.final;
                    }
                }]);

                return PeakDetector;
            }();

            var TwoWayMismatch = function () {
                function TwoWayMismatch() {
                    _classCallCheck(this, TwoWayMismatch);

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

                _createClass(TwoWayMismatch, [{
                    key: "process",
                    value: function process(samples, spectrum) {
                        this.peakDetector.detectPeaks(spectrum);
                        this.computeFundamentalFrequency();
                    }
                }, {
                    key: "computeFundamentalFrequency",
                    value: function computeFundamentalFrequency() {
                        var peaks = this.peakDetector.peaks;
                        if (peaks.length === 0) {
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
                        for (var _i = 0; _i < nFreqs; _i++) {
                            if (this.errors1[_i] < minErrorVal) {
                                minErrorVal = this.errors1[_i];
                                minErrorIdx = _i;
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

                        for (var _i2 = 0; _i2 < nFreqs; _i2++) {
                            // ftrials[i] = f0Min*Math.pow(2, (double)i/10);
                            this.ftrials2[_i2] = _i2 * (f0Max2 - f0Min2) / nFreqs + f0Min2;
                            this.errors2[_i2] = this.calculateTotalError(this.ftrials2[_i2], this.peaks);
                        }

                        var minErrorVal = Number.POSITIVE_INFINITY;
                        var minErrorIdx = 0;
                        for (var _i3 = 0; _i3 < nFreqs; _i3++) {
                            if (this.errors2[_i3] < minErrorVal) {
                                minErrorVal = this.errors2[_i3];
                                minErrorIdx = _i3;
                            }
                        }

                        this.fundamentalFrequency = this.ftrials2[minErrorIdx];
                        this.totalError = minErrorVal;
                    }
                }, {
                    key: "calculateTotalError",
                    value: function calculateTotalError(f0Trial) {
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

                            if (measuredAmplitudes[i] > ampMax) {
                                ampMax = measuredPartials[i];
                            }

                            if (measuredPartials[i] > freqMax) {
                                freqMax = measuredPartials[i];
                            }
                        }

                        // calculate predicted frequencies of harmonics
                        var nPredictedHarmonics = Math.ceil(freqMax / f0Trial);
                        var predictedHarmonics = new Float32Array(nPredictedHarmonics);
                        var predictedAmplitudes = new Float32Array(nPredictedHarmonics);
                        for (var n = 0; n < nPredictedHarmonics; n++) {
                            predictedHarmonics[n] = f0Trial * (n + 1);
                        }

                        // predicted to measured
                        var differences = new Float32Array(nPredictedHarmonics);
                        for (var _n = 0; _n < predictedHarmonics.length; _n++) {
                            differences[_n] = Number.POSITIVE_INFINITY;
                            for (var k = 0; k < measuredPartials.length; k++) {
                                var diff = Math.abs(predictedHarmonics[_n] - measuredPartials[k]);
                                if (diff < differences[_n]) {
                                    differences[_n] = diff;
                                    predictedAmplitudes[_n] = measuredAmplitudes[k];
                                }
                            }
                            predictedToMeasuredError += this.computePredictedToMeasuredError(differences[_n], predictedHarmonics[_n], predictedAmplitudes[_n], ampMax);
                        }

                        // measured to predicted
                        differences = new Float32Array(peaks.length);
                        for (var _k = 0; _k < differences.length; _k++) {
                            differences[_k] = Number.POSITIVE_INFINITY;
                            for (var _n2 = 0; _n2 < predictedHarmonics.length; _n2++) {
                                var _diff = Math.abs(predictedHarmonics[_n2] - peaks[_k][0]);
                                if (_diff < differences[_k]) {
                                    differences[_k] = _diff;
                                }
                            }
                            measuredToPredictedError += this.computeMeasuredToPredictedError(differences[_k], peaks[_k][0], peaks[_k][1], ampMax);
                        }

                        totalError = predictedToMeasuredError / predictedHarmonics.length + this.rho * measuredToPredictedError / peaks.length;
                        return totalError;
                    }
                }, {
                    key: "computePredictedToMeasuredError",
                    value: function computePredictedToMeasuredError(deltaFreqN, freqN, ampN, ampMax) {
                        var error = deltaFreqN * Math.pow(freqN, -this.pmP) + ampN / ampMax * (this.pmQ * deltaFreqN * Math.pow(freqN, -this.pmP) - this.pmR);
                        // freqN === 0 => error === Infinity
                        return error;
                    }
                }, {
                    key: "computeMeasuredToPredictedError",
                    value: function computeMeasuredToPredictedError(deltaFreqK, freqK, ampK, ampMax) {
                        var error = deltaFreqK * Math.pow(freqK, -this.mpP) + ampK / ampMax * (this.mpQ * deltaFreqK * Math.pow(freqK, -this.mpP) - this.mpR);
                        return error;
                    }
                }]);

                return TwoWayMismatch;
            }();

            module.exports['TwoWayMismatch'] = TwoWayMismatch;
        }).call(this, require('_process'));
    }, { "_process": 1 }], 6: [function (require, module, exports) {
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

            for (var i = 0; i < numSlices; i++) {
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
            return arr.reduce(function (prev, curr) {
                return prev + curr;
            });
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
            'RECTANGLE': function RECTANGLE() {},
            'HANN': function HANN(arr) {
                for (var n = 0; n < arr.length; n++) {
                    arr[n] = 0.5 * (1.0 - Math.cos(2.0 * Math.PI * n / (arr.length - 1))) * arr[n];
                }
            },
            'HAMMING': function HAMMING(arr) {
                for (var n = 0; n < arr.length; n++) {
                    arr[n] = (0.53836 - 0.46164 * Math.cos(2.0 * Math.PI * n / (arr.length - 1))) * arr[n];
                }
            },
            'BLACKMANN': function BLACKMANN(arr) {
                for (var n = 0; n < arr.length; n++) {
                    arr[n] = (0.42 - 0.5 * Math.cos(2.0 * Math.PI * n / (arr.length - 1)) + 0.08 * Math.cos(4.0 * Math.PI * n / (arr.length - 1))) * arr[n];
                }
            }
        };

        var noteNames = ["A0", "A#0", "B0", "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1", "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6", "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7", "C8", "C#8", "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "B8", "C9", "C#9", "D9", "D#9", "E9", "F9", "F#9", "G9", "G#9", "A9", "A#9", "B9"];

        function freqToNoteName(freq) {
            var freqA0 = 27.5;
            if (freq < freqA0) return 'below A0';
            if (freq > 16740) return 'above C10';
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
    }, {}], 7: [function (require, module, exports) {
        var utils = require('./utils');

        var AudioVisualization = function () {
            function AudioVisualization(analyser, elemId) {
                _classCallCheck(this, AudioVisualization);

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

            _createClass(AudioVisualization, [{
                key: "render",
                value: function render() {}
            }, {
                key: "resize",
                value: function resize() {
                    this.canvas.width = this.div.clientWidth;
                    this.canvas.height = this.div.clientHeight;
                }
            }, {
                key: "animate",
                value: function animate() {
                    this.render();

                    requestAnimationFrame(this.animate.bind(this));
                }
            }]);

            return AudioVisualization;
        }();

        var Oscilloscope = function (_AudioVisualization) {
            _inherits(Oscilloscope, _AudioVisualization);

            function Oscilloscope() {
                _classCallCheck(this, Oscilloscope);

                return _possibleConstructorReturn(this, (Oscilloscope.__proto__ || Object.getPrototypeOf(Oscilloscope)).apply(this, arguments));
            }

            _createClass(Oscilloscope, [{
                key: "render",

                /**
                 * If the analysis buffer is shorter than the canvas width, draw a line for each sample,
                 * Otherwise, split the buffer into slices (number of slices = width), calculate the maximum and minimum sample
                 * value at each slice and draw one line for maximum samples and another one for minimum samples.
                 */
                value: function render() {
                    this.buffer = this.analyser.frame;
                    var ctx = this.context;

                    ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
                    if (this.buffer.length <= this.canvas.width) {
                        this.plotShortArray(ctx, this.buffer);
                    } else {
                        var slices = utils.slicesOfArray(this.buffer, this.canvas.width);
                        // var sliceAbsMaxes = slices.map((slice) => utils.absoluteMax(slice));
                        var slcMaxs = slices.map(function (slice) {
                            return Math.max.apply(Math, _toConsumableArray(slice));
                        });
                        var slcMins = slices.map(function (slice) {
                            return Math.min.apply(Math, _toConsumableArray(slice));
                        });

                        this.plotArray(ctx, slcMaxs);
                        this.plotArray(ctx, slcMins);
                    }
                }
            }, {
                key: "plotShortArray",
                value: function plotShortArray(ctx, arr) {
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
            }, {
                key: "plotArray",
                value: function plotArray(ctx, arr) {
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
            }]);

            return Oscilloscope;
        }(AudioVisualization);

        var Spectroscope = function (_AudioVisualization2) {
            _inherits(Spectroscope, _AudioVisualization2);

            function Spectroscope(analyser, twm, elemId) {
                _classCallCheck(this, Spectroscope);

                var _this3 = _possibleConstructorReturn(this, (Spectroscope.__proto__ || Object.getPrototypeOf(Spectroscope)).call(this, analyser, elemId));

                _this3.twm = twm;
                return _this3;
            }

            _createClass(Spectroscope, [{
                key: "render",
                value: function render() {
                    var spectrum = this.analyser.fft.spectrum;
                    var f0 = this.twm.fundamentalFrequency;
                    var peaks = this.twm.peakDetector.peaks;
                    var ctx = this.context;
                    var width = this.canvas.width;
                    var height = this.canvas.height;

                    this.renderSpectrum(ctx, spectrum, width, height);
                    if (typeof this.twm !== "undefined") {
                        this.renderPeaks(ctx, peaks, spectrum, width, height);
                        this.renderThreshold(ctx, spectrum, width, height);
                        if (f0) this.renderFundamentalFrequency(ctx, f0, width, height);
                    }
                }
            }, {
                key: "renderSpectrum",
                value: function renderSpectrum(ctx, spectrum, width, height) {
                    ctx.clearRect(0, 0, width, height);
                    ctx.strokeStyle = 'black';
                    ctx.beginPath();
                    ctx.moveTo(0, 0);
                    for (var ix = 0; ix < spectrum.length; ix++) {
                        // var y = height - spectrum[ix] * height;
                        var y = -Math.log(spectrum[ix]) * height * 0.1;
                        var x = ix / spectrum.length * width;
                        ctx.lineTo(x, y);
                    }
                    ctx.stroke();
                }
            }, {
                key: "renderPeaks",
                value: function renderPeaks(ctx, peaks, spectrum, width, height) {
                    ctx.strokeStyle = 'green';
                    for (var ix = 0; ix < peaks.length; ix++) {
                        var _peaks$ix = _slicedToArray(peaks[ix], 2);

                        var peakFreq = _peaks$ix[0];
                        var peakMag = _peaks$ix[1];

                        ctx.beginPath();
                        var x = peakFreq / 22050 * width;
                        var y = -Math.log(peakMag) * height * 0.1;
                        ctx.arc(x, y, 5, 0, 2.0 * Math.PI, false);
                        ctx.moveTo(x, height);
                        ctx.lineTo(x, y);
                        ctx.stroke();
                    }
                }
            }, {
                key: "renderFundamentalFrequency",
                value: function renderFundamentalFrequency(ctx, f0, width, height) {
                    ctx.strokeStyle = 'red';
                    ctx.beginPath();
                    var x = f0 / 22050 * width;
                    ctx.moveTo(x, height);
                    ctx.lineTo(x, 0);
                    ctx.stroke();
                }
            }, {
                key: "renderThreshold",
                value: function renderThreshold(ctx, spectrum, width, height) {
                    ctx.strokeStyle = 'orange';
                    ctx.beginPath();
                    for (var ix = 0; ix < spectrum.length; ix++) {
                        var freq = ix * 22050 / spectrum.length;
                        var threshold = this.twm.peakDetector.threshold(freq);
                        // var y = height - threshold * height;
                        var y = -Math.log(threshold) * height * 0.1;
                        var x = ix / spectrum.length * width;
                        ctx.lineTo(x, y);
                    }
                    ctx.stroke();
                }
            }]);

            return Spectroscope;
        }(AudioVisualization);

        module.exports['Oscilloscope'] = Oscilloscope;
        module.exports['Spectroscope'] = Spectroscope;
    }, { "./utils": 6 }] }, {}, [4]);

