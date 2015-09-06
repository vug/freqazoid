#what is done, what will be done

# To Do #
  * Write a program which splits an audio data into many files according to given window and hop sizes.
  * Add [GNU Scientific Library](http://www.gnu.org/software/gsl/) for FFT and function minimization algorithms to the project.
  * Implement a GUI which displays a waveform of an audio file for offline usage.
  * Implement Oscilloscope and Spectroscope for online usage(1).
  * Implement Peak Detection algorithm(1).
  * Implement TWM(1).


# Done #
  * **9 May 2009** [FFTW](http://www.fftw.org/) a crossplatform FFT library is added to the project. Fourier transform of some sample frames are taken and it is checked that the system works correct.
  * **4 May 2009** A .wav file is read successfully. A little program (stereo2mono) is written which reads an entire stereo sound file and writes its left and right channels into two different mono sound files.
  * **2 May 2009** Add Cross-platform soundfile input output library [libsndfile](http://www.mega-nerd.com/libsndfile/) to the project
  * Cross-platform realtime audio API [portaudio](http://www.portaudio.com/) is compiled and tested
  * Cross-platform GUI and Threading API [SDL](http://www.libsdl.org/) is compiled and tested

(1) Previous Java Code will be useful.