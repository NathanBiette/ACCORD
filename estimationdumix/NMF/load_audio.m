function [x1, x2, x3, x4, x5, N] = load_audio(str, n, delta, NMF)% On charge la piste de batterie et on calcule le diagramme temps-fr�quence[y,] = audioread(strcat('../Data/essai',str,'/instrument01.wav'));y = y(:,1);N = length(y);if NMF == 1  x1 = imag(diagrammeTF(y, N, n, delta));else  x1 = abs(diagrammeTF(y, N, n, delta));end;% On charge la piste de la m�lodie et on calcule le diagramme temps-fr�quence[y,] = audioread(strcat('../Data/essai',str,'/instrument02.wav'));y = y(:,1);if NMF == 1  x2 = imag(diagrammeTF(y, N, n, delta));else  x2 = abs(diagrammeTF(y, N, n, delta));end;% On charge la piste de la basse et on calcule le diagramme temps-fr�quence[y,] = audioread(strcat('../Data/essai',str,'/instrument03.wav'));y = y(:,1);if NMF == 1  x3 = imag(diagrammeTF(y, N, n, delta));else  x3 = abs(diagrammeTF(y, N, n, delta));end;% On charge la piste de l'accompagnement et on calcule le diagramme temps-fr�quence[y,] = audioread(strcat('../Data/essai',str,'/instrument04.wav'));y = y(:,1);if NMF == 1  x4 = imag(diagrammeTF(y, N, n, delta));else  x4 = abs(diagrammeTF(y, N, n, delta));end;% On charge la piste de sortie et on calcule le diagramme temps-fr�quence[y, Fs] = audioread(strcat('../Data/essai',str,'/global.wav'));y = y(:,1);if NMF == 1  x5 = imag(diagrammeTF(y, N, n, delta));else  x5 = abs(diagrammeTF(y, N, n, delta));end;return;end