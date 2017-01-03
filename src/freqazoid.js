var audio = require('./audio');
var visualizations = require('./visualizations');
var TwoWayMismatch = require('./twoWayMismatch').TwoWayMismatch;
var Oscilloscope = visualizations.Oscilloscope;
var Spectroscope = visualizations.Spectroscope;

class Freqazoid {
    constructor() {
        var ae = new audio.AudioEngine();
        var twm = new TwoWayMismatch();
        ae.analysisBuffer.registerProcess(twm.process.bind(twm));
        var osc = new Oscilloscope(ae.analysisBuffer, 'osc');
        var spc = new Spectroscope(ae.analysisBuffer, twm, 'spc');
        var visualizations = [osc, spc];
        for (let vis of visualizations) {
            window.addEventListener('resize', vis.resize.bind(vis), false);
            vis.animate();
        }
        this.vue =  new Vue({
            el: '#app',
            data: {
                ae: ae,
                twm: twm,
                enginePlaying: true,
                hopSize: ae.analysisBuffer.hopSize,
                numHops: ae.analysisBuffer.numHops
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
                },
                requestMicrophone: function() {
                    this.ae.requestMicrophone();
                }
            },
            computed: {
                windowSize: function() {
                    return this.hopSize * this.numHops;
                }
            },
            watch: {
                hopSize: function(newHopSize) {
                    this.ae.setBufferSize(newHopSize, this.ae.analysisBuffer.numHops);
                },
                numHops: function(newNumHops) {
                    this.ae.setBufferSize(this.ae.analysisBuffer.hopSize, newNumHops);
                }
            }
        });


        ae.requestMicrophone();
    }
}

module.exports['Freqazoid'] = Freqazoid;
