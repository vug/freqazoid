## C'ye geçiş ##
## yapılacaklar ##
  * dandik bir buffer oluştur. Callback onu doldursun. dolunca bir iş yapsın ve buffer'ı boşaltsın. (yasak olan dosya erişimi, database sorgulamaları, grafik gibi işlermiş)

http://music.columbia.edu/pipermail/music-dsp/2009-February/067481.html
Intensive math operations are OK, particularly if they are operating on small blocks of audio. Filters, oscillators, small FFTs are all fine in the callback.

But you should avoid doing high level things like file I/O, database access, graphics, etc. These can block the CPU and wreak havoc with your audio callback. If you have to do those along with your audio then then do them in a thread and stream the audio to the device using a blocking write.

## yapılanlar ##
  * Portaudio Visual Studio projesinde kodun içine yedirilebiliyor. WMME kullanılarak realtime ses input'u sağlanabiliyor.
  * SDL ve SDL graphics kütüphaneleri Visual Studio'da kullanılabiliyorlar

## To Do ##
  * Use a parabolic interpolation to find a peaks frequency more precise

## Done ##
  * Implement the two-way mismatch algorithm
  * Implement a peak detection algorithm for spectral peaks. (Read the PARSHL paper of JOS)
  * Options for selecting different types of windows (Rectangular, Hann, Hamming, Kaiser, Black something... vs.)
  * Options for window size
  * View menu to select one of: Oscilloscope, Spectroscope (+ show peaks), Spectrogram, Pitch
  * Options for allowing or stopping audio input and output.
  * Facility for opening a sound file and sending its content to the analyser in real-time.